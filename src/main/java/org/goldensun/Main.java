package org.goldensun;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goldensun.disassembler.Disassembler;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.FlowControl;
import org.goldensun.disassembler.InstructionSet;
import org.goldensun.disassembler.ReferenceGraph;
import org.goldensun.disassembler.Tracer;
import org.goldensun.disassembler.Translator;
import org.goldensun.disassembler.ops.OpState;
import org.goldensun.memory.Memory;
import org.goldensun.memory.Segment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.goldensun.Decompressor.decompress;

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

    final DisassemblerConfig config = new DisassemblerConfig(memory);

    config.disassemblyRanges.add(new DisassemblyRange(InstructionSet.THUMB, 0x8000000, 0x801e74c, 0x801e79c));
//    config.disassemblyRanges.add(new DisassemblyRange(InstructionSet.THUMB, 0x8000000, 0x801e7ac, 0x801e7bc));
//    config.switches.add(new SwitchConfig(0x80a4380, 6));

//    decompressMap(config, 3);

    LOGGER.info("Disassembling code...");

    final Disassembler disassembler = new Disassembler();
    final Map<Integer, OpState> ops = disassembler.disassemble(config);

    LOGGER.info("Building node graph...");

    final Tracer tracer = new Tracer();
    final ReferenceGraph references = tracer.trace(config, ops);

    LOGGER.info("Tracking condition dependencies...");

    final OpState[] conditionalOps = references.stream().filter(op -> op.opType.readsConditions()).toArray(OpState[]::new);
    final Map<OpState, List<OpState>> conditionDependencies = new HashMap<>();

    for(final OpState conditionalOp : conditionalOps) {
      LOGGER.info("Searching for conditions for %s...", conditionalOp);
      references.backtrack(conditionalOp, op -> {
        if(conditionalOp.opType.readsOverflow() && !op.overflow() || conditionalOp.opType.readsCarry() && !op.carry() || conditionalOp.opType.readsZero() && !op.zero() || conditionalOp.opType.readsNegative() && !op.negative()) {
          return FlowControl.CONTINUE;
        }

        LOGGER.info("Op %s satisfies conditions", op);
        conditionDependencies.computeIfAbsent(conditionalOp, k -> new ArrayList<>()).add(op);

        return FlowControl.TERMINATE_BRANCH;
      });
    }

    Arrays.stream(conditionalOps).filter(op -> !conditionDependencies.containsKey(op)).forEach(failed -> LOGGER.warn("Failed to find condition for %s!", failed));

//    final Solver solver = new Solver();
//    solver.solve(config, ops);

    final Translator translator = new Translator();
    translator.translate(config, ops, conditionDependencies);
  }

  private static void decompressMap(final DisassemblerConfig config, final int mapId) {
    final int pointerTableIndex = config.memory.get(0x809f1a8 + mapId * 0x8, 0x2);
    final int mapPtr = config.memory.get(0x8320000 + pointerTableIndex * 0x4, 0x4);
    final int decompressedSize = decompress(config.memory, mapPtr, 0x2008000);
    LOGGER.info("Decompressed map %d to 0x2008000 (0x%x bytes)", mapId, decompressedSize);

    final int initPtr = config.memory.get(0x2008004, 0x4);
    final int roomsPtr = config.memory.get(0x200800c, 0x4);
    final int transitionsPtr = config.memory.get(0x2008014, 0x4);
    final int actorsPtr = config.memory.get(0x200801c, 0x4);
    final int eventsPtr = config.memory.get(0x2008024, 0x4);
    final int laddersPtr = config.memory.get(0x200802c, 0x4);
  }
}
