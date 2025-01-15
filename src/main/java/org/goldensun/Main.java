package org.goldensun;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goldensun.disassembler.Disassembler;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.FlowControl;
import org.goldensun.disassembler.ReferenceGraph;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.Tracer;
import org.goldensun.disassembler.Translator;
import org.goldensun.disassembler.ops.BlState;
import org.goldensun.disassembler.ops.BxState;
import org.goldensun.disassembler.ops.LdrPcState;
import org.goldensun.disassembler.ops.LdrSpState;
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
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.goldensun.Decompressor.decompress;
import static org.goldensun.Decompressor.rewritePointers;

public final class Main {
  private Main() { }

  static {
    System.setProperty("log4j.skipJansi", "false");
    System.setProperty("log4j2.configurationFile", "log4j2.xml");
  }

  private static final Logger LOGGER = LogManager.getFormatterLogger(Main.class);

  private static final Pattern FUNCTION_PATTERN = Pattern.compile("FUN_[a-f0-9]{7}");

  public static void main(final String[] args) throws IOException {
    final Memory memory = new Memory();
    memory.addSegment(new Segment(0x200_0000, 0x4_0000)); // On-board work RAM
    memory.addSegment(new Segment(0x300_0000, 0x8000, 0xf00_7fff)); // On-chip work RAM
    memory.addSegment(new Segment(0x800_0000, 0x80_0000)); // Game memory
    memory.setBytes(0x800_0000, Files.readAllBytes(Path.of("./game.rom")));

    Files.deleteIfExists(Path.of("out.txt"));
    final DisassemblerConfig config = new DisassemblerConfig(memory, new PrintWriter("out.txt"));

    LOGGER.info("Loading decomp code...");

    final DecompReader decompReader = new DecompReader();
    config.functions.putAll(decompReader.loadMethods(Path.of("../goldensun")));

//    disassembleMap(config, 4);
//    disassembleTable(config, 0x80ee2b4, 407);

    config.address = 0x80a6b64;
//    config.bxAsCall.add(0x80c128a);
//    config.blAsB.add(0x80e65c2);
//    config.switches.add(new SwitchConfig(0x80e4924, 101));
//    loadMap(config, 4);
    disassembleFunction(config);

    config.writer.close();
  }

  private static void disassembleFunction(final DisassemblerConfig config) {
    final Map<Integer, String> functions = new HashMap<>();
    final Map<Integer, Map<Integer, OpState>> deferred = new HashMap<>();
    disassembleFunction(config, functions, deferred, config.address);

    final List<TransformedOutput> transformed = new ArrayList<>();

    while(!deferred.isEmpty()) {
      transformDeferred(config, functions, transformed, deferred);
    }

    translate(config, transformed, functions);

    final String built = functions.entrySet().stream()
      .sorted(Comparator.comparingInt(Map.Entry::getKey))
      .map(Map.Entry::getValue)
      .collect(Collectors.joining("\n\n"));

    config.writer.println(built);
  }

  private static void loadMap(final DisassemblerConfig config, final int mapId) {
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
  }

  private static void disassembleTable(final DisassemblerConfig config, final int tableAddress, final int tableSize) {
    final Map<Integer, String> functions = new HashMap<>();
    final Map<Integer, Map<Integer, OpState>> deferred = new HashMap<>();

    for(int i = 0; i < tableSize; i++) {
      final int functionPtr = config.memory.get(tableAddress + i * 0x4, 4) & ~0x1;

      if(functionPtr != 0) {
        config.docs.computeIfAbsent(functionPtr, k -> new ArrayList<>()).add("Table %07x".formatted(tableAddress));
        disassembleFunction(config, functions, deferred, functionPtr);
      }
    }

    final List<TransformedOutput> transformed = new ArrayList<>();

    while(!deferred.isEmpty()) {
      transformDeferred(config, functions, transformed, deferred);
    }

    translate(config, transformed, functions);

    final String built = functions.entrySet().stream()
      .sorted(Comparator.comparingInt(Map.Entry::getKey))
      .map(Map.Entry::getValue)
      .collect(Collectors.joining("\n\n"));

    config.writer.println(built);
  }

  private static void disassembleMap(final DisassemblerConfig config, final int mapId) {
    loadMap(config, mapId);

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
    config.docs.computeIfAbsent(initPtr, k -> new ArrayList<>()).add("{@link GoldenSunVars#init_2008004}");
    config.docs.computeIfAbsent(roomsPtr, k -> new ArrayList<>()).add("{@link GoldenSunVars#getRooms_200800c}");
    config.docs.computeIfAbsent(transitionsPtr, k -> new ArrayList<>()).add("{@link GoldenSunVars#getTransitions_2008014}");
    config.docs.computeIfAbsent(actorsPtr, k -> new ArrayList<>()).add("{@link GoldenSunVars#getActors_200801c}");
    config.docs.computeIfAbsent(eventsPtr, k -> new ArrayList<>()).add("{@link GoldenSunVars#getEvents_2008024}");
    config.docs.computeIfAbsent(laddersPtr, k -> new ArrayList<>()).add("{@link GoldenSunVars#getLadders_200802c}");

    final Map<Integer, String> functions = new HashMap<>();
    final Map<Integer, Map<Integer, OpState>> deferred = new HashMap<>();
    disassembleFunction(config, functions, deferred, initPtr);
    disassembleFunction(config, functions, deferred, roomsPtr);
    disassembleFunction(config, functions, deferred, transitionsPtr);
    disassembleFunction(config, functions, deferred, actorsPtr);
    disassembleFunction(config, functions, deferred, laddersPtr);

    final Map<Integer, OpState> getEventsDisassembly = disassembleFunction(config, functions, deferred, eventsPtr);
    disassembleEventHandlers(config, functions, deferred, getEventsDisassembly);

    final List<TransformedOutput> transformed = new ArrayList<>();

    while(!deferred.isEmpty()) {
      transformDeferred(config, functions, transformed, deferred);
    }

    translate(config, transformed, functions);

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
              config.ignoreParams.add(eventHandlerPtr);
              disassembleFunction(config, functions, deferred, eventHandlerPtr);
            }
          }
        }
      }
    }
  }

  private static Map<Integer, OpState> disassembleFunction(final DisassemblerConfig config, final Map<Integer, String> functions, final Map<Integer, Map<Integer, OpState>> deferred, final int address) {
    // Bail out if we have already decomp'd this or it already exists in the decomp
    if(functions.containsKey(address) || config.functions.containsKey(address)) {
      LOGGER.info("Skipping 0x%x, already decompiled", address);
      return Map.of();
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
        } else if(FUNCTION_PATTERN.matcher(destName).matches()) {
          name = "FUN_%07x".formatted(address);
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
      return Map.of();
    }

    // Regular function
    LOGGER.info("Disassembling function 0x%07x", address);
    functions.put(address, ""); // so we don't try to disassemble this function again

    final DisassemblerConfig newConfig = new DisassemblerConfig(config);
    newConfig.address = address;
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

  private static TransformedOutput transform(final int address, final DisassemblerConfig config, final Map<Integer, OpState> ops, final ReferenceGraph references) {
    LOGGER.info("Building register dependency graph...");

    // Build up the register usage graph
    final Map<OpState, Map<Register, List<OpState>>> registerUsage = new HashMap<>();
    final Map<Register, Set<RegisterUsage>> consumerUsage = new EnumMap<>(Register.class);
    final Map<Register, Set<RegisterUsage>> providerUsage = new EnumMap<>(Register.class);
    final Map<OpState, Integer> stackDepths = new HashMap<>();

    // Backtrack from the terminal op
    references.backtrack((consumer, consumerStackDepth) -> {
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

    // Find return values
    final List<OpState> returnValues = new ArrayList<>();
    references.backtrack((op, stackDepth) -> {
      clearRegisterUsage(providerUsage);
      op.getRegisterUsage(providerUsage);

      if(providerUsage.get(Register.R0).contains(RegisterUsage.READ)) {
        return FlowControl.TERMINATE_BRANCH;
      }

      if(providerUsage.get(Register.R0).contains(RegisterUsage.WRITE)) {
        returnValues.add(op);
        return FlowControl.TERMINATE_BRANCH;
      }

      return FlowControl.CONTINUE;
    });

    // Find matching push/pops and remove them if they're for r0-r7 or LR
    LOGGER.info("Removing matching push/pops for R0-R7 and LR...");

    references.backtrack((consumer, consumerStackDepth) -> {
      stackDepths.put(consumer, consumerStackDepth);
      return FlowControl.CONTINUE;
    });

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

    // Remove return bx
    final OpState last = references.last();

    if(last instanceof final BxState bx) {
      final List<OpState> providers = registerUsage.get(bx).get(bx.dst);

      if(providers.isEmpty() && bx.dst == Register.R14_LR) {
        LOGGER.info("Removing bx return %s...", bx);
        references.remove(bx);
        registerUsage.remove(bx);
      } else {
        for(final OpState provider : providers) {
          if(provider instanceof final PopState pop) {
            for(int i = 0; i < pop.registers.length; i++) {
              if(registerPushDepths.get(stackDepths.get(pop) - 0x4 - i * 0x4) == Register.R14_LR) {
                LOGGER.info("Removing bx return %s...", bx);
                references.remove(bx);
                registerUsage.remove(bx);
              }
            }
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

    LOGGER.info("Searching for parameters...");
    int params = 0;

    for(final var opEntry : registerUsage.entrySet()) {
      final OpState op = opEntry.getKey();

      // Registers only used in a push before being initialized are not params
      if(!(op instanceof PushState)) {
        // Look at each op...
        for(final var regEntry : opEntry.getValue().entrySet()) {
          // And if it has nothing setting it...
          if(regEntry.getValue().isEmpty() && regEntry.getKey() != Register.R13_SP) {
            // Check to see if the only thing that depends on this op's registers' values is a push
            clearRegisterUsage(providerUsage);
            op.getRegisterUsage(providerUsage);

            // Get all registers that this op provides
            final List<Register> registers = providerUsage.entrySet().stream()
              .filter(e -> e.getValue().contains(RegisterUsage.WRITE))
              .map(Map.Entry::getKey).toList();

            boolean allRefsArePushes = true;
            for(final Register register : registers) {
              allRefsArePushes &= registerUsage.entrySet().stream()
                .filter(e -> e.getValue().containsKey(register))
                .filter(e -> e.getValue().get(register).contains(op))
                .allMatch(e -> e.getKey() instanceof PushState);
            }

            if(registers.isEmpty() || !allRefsArePushes) {
              params = Math.max(params, regEntry.getKey().ordinal() + 1);
            }
          }
        }
      }

      // Look for stack reads that exceed the stack depth of this method (>4 params are passed in via stack)
      if(op instanceof final LdrSpState ldrsp) {
        if(ldrsp.offset >= stackDepths.get(ldrsp)) {
          params = Math.max(params, (ldrsp.offset - stackDepths.get(ldrsp)) / 0x4 + 5);
        }
      }
    }

    config.functions.put(address, new FunctionInfo(address, "FUN_%07x".formatted(address), returnValues.isEmpty() ? "void" : "int", IntStream.range(0, params).mapToObj(i -> new ParamInfo(i < 4 ? "r" + i : "a" + i, "int")).toArray(ParamInfo[]::new)));

    LOGGER.info("Tracking condition dependencies...");

    // List of ops that read conditions
    final OpState[] conditionalOps = references.stream().filter(op -> op.opType.readsConditions()).toArray(OpState[]::new);
    // Map of ops that that read conditions and a list of ops that supply those conditions
    final Map<OpState, Set<OpState>> conditionDependencies = new HashMap<>();

    for(final OpState conditionalOp : conditionalOps) {
      LOGGER.info("Searching for conditions for %s...", conditionalOp);
      references.backtrack(conditionalOp, (op, stackDepth) -> {
        if(conditionalOp.opType.readsOverflow() && !op.overflow() || conditionalOp.opType.readsCarry() && !op.carry() || conditionalOp.opType.readsZero() && !op.zero() || conditionalOp.opType.readsNegative() && !op.negative()) {
          return FlowControl.CONTINUE;
        }

        LOGGER.info("Op %s satisfies conditions", op);
        conditionDependencies.computeIfAbsent(conditionalOp, k -> new HashSet<>()).add(op);

        return FlowControl.TERMINATE_BRANCH;
      });
    }

    // Remove condition suppliers
    conditionDependencies.values().stream().flatMap(Collection::stream).forEach(references::remove);

    Arrays.stream(conditionalOps).filter(op -> !conditionDependencies.containsKey(op)).forEach(failed -> LOGGER.warn("Failed to find condition for %s!", failed));

    return new TransformedOutput(address, references.ops(), conditionDependencies);
  }

  /** We defer translation of non-thunks to ensure all thunks are available in the function map before translating the rest of the disassembly */
  private static void transformDeferred(final DisassemblerConfig config, final Map<Integer, String> functions, final List<TransformedOutput> transformed, final Map<Integer, Map<Integer, OpState>> deferred) {
    final Map<Integer, Map<Integer, OpState>> deferredCopy = new HashMap<>(deferred);
    deferred.clear();

    for(final var entry : deferredCopy.entrySet()) {
      final int address = entry.getKey();
      final Map<Integer, OpState> ops = entry.getValue();

      final ReferenceGraph references = trace(config, ops);
      functionVisitor(config, functions, deferred, references);

      transformed.add(transform(address, config, ops, references));
    }
  }

  private static void translate(final DisassemblerConfig config, final List<TransformedOutput> transformed, final Map<Integer, String> functions) {
    LOGGER.info("Generating output...");

    final Translator translator = new Translator();

    for(final TransformedOutput t : transformed) {
      final List<String> translated = translator.translate(t.address, config, t.ops, t.conditionDependencies);
      final FunctionInfo functionInfo = config.functions.get(t.address);
      final String params = Arrays.stream(functionInfo.params).map(param -> "%s %s".formatted(param.type, param.name)).collect(Collectors.joining(", "));

      String function;
      if(config.ignoreParams.contains(t.address)) {
        function = "@Method(value = 0x%7x, ignoreExtraParams = true)%n".formatted(t.address);
      } else {
        function = "@Method(0x%7x)%n".formatted(t.address);
      }

      function +=
        "public static %s %s(%s) {%n".formatted(functionInfo.returnType, config.functionNameOverrides.getOrDefault(t.address, functionInfo.name), params) +
        String.join("\n", translated);

      if(!"void".equals(functionInfo.returnType)) {
        function += "\nreturn r0;";
      }

      function +=
        "\n}";

      if(config.docs.containsKey(t.address)) {
        function =
          "/**\n" +
            config.docs.get(t.address).stream()
              .map(line -> "* " + line)
              .collect(Collectors.joining("\n")) +
            "\n*/\n" + function;
      }

      functions.put(t.address, function);
    }
  }

  private static void functionVisitor(final DisassemblerConfig config, final Map<Integer, String> functions, final Map<Integer, Map<Integer, OpState>> deferred, final ReferenceGraph references) {
    references.stream().filter(op -> op.opType == OpTypes.BL).filter(op -> !config.blAsB.contains(op.address)).forEach(op -> disassembleFunction(config, functions, deferred, ((BlState)op).getDest()));
  }
}
