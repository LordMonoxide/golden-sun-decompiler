package org.goldensun;

import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goldensun.disassembler.Disassembler;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.FlowControl;
import org.goldensun.disassembler.InstructionSet;
import org.goldensun.disassembler.ReferenceGraph;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.Tracer;
import org.goldensun.disassembler.Translator;
import org.goldensun.disassembler.ops.BlState;
import org.goldensun.disassembler.ops.BxState;
import org.goldensun.disassembler.ops.LdrPcState;
import org.goldensun.disassembler.ops.OpState;
import org.goldensun.disassembler.ops.OpTypes;
import org.goldensun.disassembler.ops.PopState;
import org.goldensun.disassembler.ops.PushState;
import org.goldensun.memory.Memory;
import org.goldensun.memory.Segment;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.goldensun.Decompressor.decompress;
import static org.goldensun.Decompressor.rewritePointers;

public final class Main {
  private Main() { }

  static {
    System.setProperty("log4j.skipJansi", "false");
    System.setProperty("log4j2.configurationFile", "log4j2.xml");
  }

  private static final Logger LOGGER = LogManager.getFormatterLogger(Main.class);

  public static void main(final String[] args) throws IOException {
    final Memory memory = new Memory();
    memory.addSegment(new Segment(0x200_0000, 0x4_0000)); // On-board work RAM
    memory.addSegment(new Segment(0x300_0000, 0x8000, 0xf00_7fff)); // On-chip work RAM
    memory.addSegment(new Segment(0x800_0000, 0x80_0000)); // Game memory
    memory.setBytes(0x800_0000, Files.readAllBytes(Path.of("./game.rom")));

    Files.deleteIfExists(Path.of("out.txt"));
    final DisassemblerConfig config = new DisassemblerConfig(memory, new PrintWriter("out.txt"));

    final DecompReader decompReader = new DecompReader();
    config.functions.putAll(decompReader.loadMethods(Path.of("../goldensun")));

//    config.disassemblyRanges.add(new DisassemblyRange(InstructionSet.THUMB, 0x801e74c, 0x801e79c));
//    config.disassemblyRanges.add(new DisassemblyRange(InstructionSet.THUMB, 0x8000000, 0x801e7ac, 0x801e7bc));
//    config.switches.add(new SwitchConfig(0x80a4380, 6));

    disassembleMap(config, 132);

    config.writer.close();
  }

  private static void disassembleMap(final DisassemblerConfig config, final int mapId) {
    final int pointerTableIndex = config.memory.get(0x809f1a8 + mapId * 0x8, 0x2);
    final int mapPtr = config.memory.get(0x8320000 + pointerTableIndex * 0x4, 0x4);
    final int decompressedSize = decompress(config.memory, mapPtr, 0x2008000);
    rewritePointers(config.memory, 0x2008000, decompressedSize);
    LOGGER.info("Decompressed map %d to 0x2008000 (0x%x bytes)", mapId, decompressedSize);

    try {
      Files.write(Path.of("map%d_%x.bin".formatted(mapId, mapPtr)), config.memory.getBytes(0x2008000, decompressedSize));
    } catch(final IOException e) {
      throw new RuntimeException(e);
    }

    final int initPtr = config.memory.get(0x2008004, 0x4) & ~0x1;
    final int roomsPtr = config.memory.get(0x200800c, 0x4) & ~0x1;
    final int transitionsPtr = config.memory.get(0x2008014, 0x4) & ~0x1;
    final int actorsPtr = config.memory.get(0x200801c, 0x4) & ~0x1;
    final int eventsPtr = config.memory.get(0x2008024, 0x4) & ~0x1;
    final int laddersPtr = config.memory.get(0x200802c, 0x4) & ~0x1;

    config.functionNameOverrides.put(initPtr, "init");
    config.functionNameOverrides.put(roomsPtr, "getRooms");
    config.functionNameOverrides.put(transitionsPtr, "getTransitions");
    config.functionNameOverrides.put(actorsPtr, "getActors");
    config.functionNameOverrides.put(eventsPtr, "getEvents");
    config.functionNameOverrides.put(laddersPtr, "getLadders");

    final Map<Integer, String> functions = new HashMap<>();
    final Map<Integer, Map<Integer, OpState>> deferred = new HashMap<>();
    disassembleFunction(config, functions, deferred, initPtr);
    disassembleFunction(config, functions, deferred, roomsPtr);
    disassembleFunction(config, functions, deferred, transitionsPtr);
    disassembleFunction(config, functions, deferred, actorsPtr);
    disassembleFunction(config, functions, deferred, laddersPtr);

    final Map<Integer, OpState> getEventsDisassembly = disassembleFunction(config, functions, deferred, eventsPtr);
    disassembleEventHandlers(config, functions, deferred, getEventsDisassembly);

    while(!deferred.isEmpty()) {
      translateDeferred(config, functions, deferred);
    }

    final String built = functions.entrySet().stream()
      .sorted(Comparator.comparingInt(Map.Entry::getKey))
      .map(Map.Entry::getValue)
      .collect(Collectors.joining("\n\n"));

    config.writer.println(built);
  }

  private static void disassembleEventHandlers(final DisassemblerConfig config, final Map<Integer, String> functions, final Map<Integer, Map<Integer, OpState>> deferred, final Map<Integer, OpState> getEventsDisassembly) {
    for(final OpState op : getEventsDisassembly.values()) {
      if(op instanceof final LdrPcState ldrpc) {
        final int eventList = config.memory.get(ldrpc.getDest(), 0x4);
        if(eventList >= 0x2008000 && eventList < 0x3000000) {
          LOGGER.info("Disassembling event list 0x%x", eventList);

          for(int i = 0, event = eventList; config.memory.get(event, 0x4) != -1; i++, event += 0xc) {
            final int eventHandlerPtr = config.memory.get(event + 0x8, 0x4) & ~0x1;
            if(eventHandlerPtr >= 0x2008000) {
              config.docs.put(eventHandlerPtr, List.of("Event list 0x%x handler %d".formatted(eventList, i)));
              disassembleFunction(config, functions, deferred, eventHandlerPtr);
            }
          }
        }
      }
    }
  }

  private static Map<Integer, OpState> disassembleFunction(final DisassemblerConfig config, final Map<Integer, String> functions, final Map<Integer, Map<Integer, OpState>> deferred, final int address) {
    if(functions.containsKey(address)) {
      return ImmutableMap.of();
    }

    // Thunk
    if(config.memory.get(address, 0x4) == 0x4720_4c00) {
      LOGGER.info("Disassembling thunk 0x%07x", address);

      final int dest = config.memory.get(address + 0x4, 0x4) & ~0x1;

      final FunctionInfo destInfo = config.functions.get(dest);
      final String destName;
      final String name;
      final String returnType;
      final String params;
      final String args;
      if(destInfo != null) {
        destName = destInfo.name;

        if(config.functionNameOverrides.containsKey(address)) {
          name = config.functionNameOverrides.get(address);
        } else if(destName.endsWith("_")) {
          name = destName.substring(0, destName.length() - 1);
        } else {
          name = destName;
        }

        returnType = destInfo.returnType;
        params = Arrays.stream(destInfo.params).map(param -> "final %s %s".formatted(param.type, param.name)).collect(Collectors.joining(", "));
        args = Arrays.stream(destInfo.params).map(param -> param.name).collect(Collectors.joining(", "));
        config.functions.put(address, new FunctionInfo(address, name, returnType, destInfo.params));
      } else {
        LOGGER.warn("Unknown thunk %07x", dest);
        destName = "FUN_%07x".formatted(dest);
        name = config.functionNameOverrides.getOrDefault(address, "FUN_%07x".formatted(address));
        returnType = "";
        params = "";
        args = "";
      }

      final int destRegion = dest >>> 16;
      final String cls = destRegion == 0x800 ? "" : "_%x".formatted(destRegion);
      final String function =
        "/** {@link GoldenSun%s#%s} */%n".formatted(cls, destName) +
        "@Method(0x%7x)%n".formatted(address) +
        "public static %s %s(%s) {%n".formatted(returnType.isEmpty() ? "void" : returnType, name, params) +
        "%sMEMORY.call(0x%7x%s);".formatted("void".equals(returnType) ? "" : "return ", dest, !args.isEmpty() ? ", " + args : "") +
        "\n}";

      functions.put(address, function);
      return ImmutableMap.of();
    }

    // Regular function
    LOGGER.info("Disassembling function 0x%07x", address);

    final DisassemblerConfig newConfig = new DisassemblerConfig(config);
    newConfig.disassemblyRanges.add(new DisassemblyRange(InstructionSet.THUMB, address, address));
    final Map<Integer, OpState> disassembly = disassemble(newConfig);
    deferred.put(address, disassembly);
    return disassembly;
  }

  private static Map<Integer, OpState> disassemble(final DisassemblerConfig config) {
    LOGGER.info("Disassembling code...");

    final Disassembler disassembler = new Disassembler();
    return disassembler.disassemble(config);
  }

  private static ReferenceGraph trace(final DisassemblerConfig config, final Map<Integer, OpState> ops) {
    LOGGER.info("Building node graph...");

    final Tracer tracer = new Tracer();
    return tracer.trace(config, ops);
  }

  private static void clearRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    for(final Register register : Register.values()) {
      usage.computeIfAbsent(register, k -> EnumSet.noneOf(RegisterUsage.class)).clear();
    }
  }

  private static void removeBxReturn(final ReferenceGraph references) {
    final OpState last = references.last();

    if(last instanceof final BxState bx && bx.dst == Register.R0) {
      references.remove(bx);
    }
  }

  private static List<String> translate(final DisassemblerConfig config, final Map<Integer, OpState> ops, final ReferenceGraph references) {
    // Remove the return bx
    removeBxReturn(references);

    // Build up the register usage graph
    final Map<OpState, Map<Register, List<OpState>>> registerUsage = new HashMap<>();
    final Map<Register, Set<RegisterUsage>> consumerUsage = new EnumMap<>(Register.class);
    final Map<Register, Set<RegisterUsage>> providerUsage = new EnumMap<>(Register.class);
    final Map<OpState, Integer> stackDepths = new HashMap<>();

    // Backtrack from the terminal op
    references.backtrack((consumer, consumerStackDepth) -> {
      stackDepths.put(consumer, consumerStackDepth);

      // Get each op's register usage
      clearRegisterUsage(consumerUsage);
      consumer.getRegisterUsage(consumerUsage);
      registerUsage.put(consumer, new EnumMap<>(Register.class));

      // For each register that is read by the current op...
      for(final var use : consumerUsage.entrySet()) {
        if(use.getValue().contains(RegisterUsage.READ)) {
          final List<OpState> valueProviders = registerUsage.get(consumer).computeIfAbsent(use.getKey(), k -> new ArrayList<>());

          // Backtrack from the current op until each branch has provided a value for that register
          references.backtrack(consumer, (provider, providerStackDepth) -> {
            clearRegisterUsage(providerUsage);
            provider.getRegisterUsage(providerUsage);

            // This op provides a value for the register we're looking for, record it and terminate this branch
            if(providerUsage.get(use.getKey()).contains(RegisterUsage.WRITE)) {
              valueProviders.add(provider);
              return FlowControl.TERMINATE_BRANCH;
            }

            // Op does not provide a value for this register, continue backtracking on this branch
            return FlowControl.CONTINUE;
          });
        }
      }

      return FlowControl.CONTINUE;
    });

    // Find matching push/pops and remove them if they're for r0-r7 or LR

    // Find pushes
    final Map<Register, PushState> registerPushes = new EnumMap<>(Register.class);
    final Map<Integer, Register> registerPushDepths = new HashMap<>();
    for(final var entry : stackDepths.entrySet()) {
      if(entry.getKey() instanceof final PushState push) {
        for(int i = 0; i < push.registers.length; i++) {
          // All registers >= 8 will be copied to a low register, we ignore those
          if(registerUsage.get(push).get(push.registers[i]).isEmpty()) {
            registerPushes.put(push.registers[i], push);
            registerPushDepths.put(entry.getValue() + i * 0x4, push.registers[i]);
          }
        }
      }
    }

    // Find matching pops
    final Map<PushState, Set<Register>> removeFromPush = new HashMap<>();
    final Map<PopState, Set<Register>> removeFromPop = new HashMap<>();
    for(final var entry : stackDepths.entrySet()) {
      if(entry.getKey() instanceof final PopState pop) {
        for(int i = 0; i < pop.registers.length; i++) {
          final Register pushedRegister = registerPushDepths.get(entry.getValue() - 0x4 - i * 0x4);

          if(pushedRegister != null) {
            final Register poppedRegister = pop.registers[i];
            removeFromPush.computeIfAbsent(registerPushes.get(pushedRegister), k -> EnumSet.noneOf(Register.class)).add(pushedRegister);
            removeFromPop.computeIfAbsent(pop, k -> EnumSet.noneOf(Register.class)).add(poppedRegister);
          }
        }
      }
    }

    // Remove registers from push ops
    for(final var entry : removeFromPush.entrySet()) {
      final PushState push = entry.getKey();

      if(push.registers.length == entry.getValue().size()) {
        // All registers were removed, remove op
        references.remove(push);
      } else {
        references.put(new PushState(push.address, push.opType, Arrays.stream(push.registers).filter(register -> !entry.getValue().contains(register)).toArray(Register[]::new)), references.getReferences(push));
      }
    }

    // Remove registers from pop ops
    for(final var entry : removeFromPop.entrySet()) {
      final PopState pop = entry.getKey();

      if(pop.registers.length == entry.getValue().size()) {
        // All registers were removed, remove op
        references.remove(pop);
      } else {
        references.put(new PopState(pop.address, pop.opType, Arrays.stream(pop.registers).filter(register -> !entry.getValue().contains(register)).toArray(Register[]::new)), references.getReferences(pop));
      }
    }

    LOGGER.info("Tracking condition dependencies...");

    final OpState[] conditionalOps = references.stream().filter(op -> op.opType.readsConditions()).toArray(OpState[]::new);
    final Map<OpState, List<OpState>> conditionDependencies = new HashMap<>();

    for(final OpState conditionalOp : conditionalOps) {
      LOGGER.info("Searching for conditions for %s...", conditionalOp);
      references.backtrack(conditionalOp, (op, stackDepth) -> {
        if(conditionalOp.opType.readsOverflow() && !op.overflow() || conditionalOp.opType.readsCarry() && !op.carry() || conditionalOp.opType.readsZero() && !op.zero() || conditionalOp.opType.readsNegative() && !op.negative()) {
          return FlowControl.CONTINUE;
        }

        LOGGER.info("Op %s satisfies conditions", op);
        conditionDependencies.computeIfAbsent(conditionalOp, k -> new ArrayList<>()).add(op);

        return FlowControl.TERMINATE_BRANCH;
      });
    }

    Arrays.stream(conditionalOps).filter(op -> !conditionDependencies.containsKey(op)).forEach(failed -> LOGGER.warn("Failed to find condition for %s!", failed));

    LOGGER.info("Generating output...");

    final Translator translator = new Translator();
    return translator.translate(config, references.ops(), conditionDependencies);
  }

  /** We defer translation of non-thunks to ensure all thunks are available in the function map before translating the rest of the disassembly */
  private static void translateDeferred(final DisassemblerConfig config, final Map<Integer, String> functions, final Map<Integer, Map<Integer, OpState>> deferred) {
    final Map<Integer, Map<Integer, OpState>> deferredCopy = new HashMap<>(deferred);
    deferred.clear();

    for(final var entry : deferredCopy.entrySet()) {
      final int address = entry.getKey();
      final Map<Integer, OpState> ops = entry.getValue();

      final ReferenceGraph references = trace(config, ops);
      functionVisitor(config, functions, deferred, references);

      String function =
        "@Method(0x%7x)%n".formatted(address) +
        "public static void %s() {%n".formatted(config.functionNameOverrides.getOrDefault(address, "FUN_%07x".formatted(address))) +
        String.join("\n", translate(config, ops, references)) +
        "\n}";

      if(config.docs.containsKey(address)) {
        function =
          "/**\n" +
          config.docs.get(address).stream()
            .map(line -> "* " + line)
            .collect(Collectors.joining("\n")) +
          "\n*/\n" + function;
      }

      functions.put(address, function);
    }
  }

  private static void functionVisitor(final DisassemblerConfig config, final Map<Integer, String> functions, final Map<Integer, Map<Integer, OpState>> deferred, final ReferenceGraph references) {
    references.stream().filter(op -> op.opType == OpTypes.BL).forEach(op -> disassembleFunction(config, functions, deferred, ((BlState)op).getDest()));
  }
}
