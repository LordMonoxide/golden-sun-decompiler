package org.goldensun;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goldensun.disassembler.DataRange;
import org.goldensun.disassembler.Disassembler;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.FlowControl;
import org.goldensun.disassembler.InstructionSet;
import org.goldensun.disassembler.ReferenceGraph;
import org.goldensun.disassembler.Tracer;
import org.goldensun.disassembler.Translator;
import org.goldensun.disassembler.ops.OpState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Main {
  private Main() { }

  static {
    System.setProperty("log4j.skipJansi", "false");
    System.setProperty("log4j2.configurationFile", "log4j2.xml");
  }

  private static final Logger LOGGER = LogManager.getFormatterLogger(Main.class);

  public static void main(final String[] args) throws IOException {
    final DisassemblerConfig config = new DisassemblerConfig();
    config.disassemblyRanges.add(new DisassemblyRange(InstructionSet.THUMB, 0x8000000, 0x80a3ef0, 0x80a3f42));
    config.disassemblyRanges.add(new DisassemblyRange(InstructionSet.THUMB, 0x8000000, 0x80a3f6c, 0x80a4096));
    config.dataRanges.add(new DataRange(0x8000000, 0x80a3ef0, 0x80a40a8));
    config.data = Files.readAllBytes(Path.of("./game.rom"));

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
}
