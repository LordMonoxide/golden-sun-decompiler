package org.goldensun.disassembler.ops;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Set;

public class BxState extends OpState {
  private static final Logger LOGGER = LogManager.getFormatterLogger(BxState.class);

  public final Register dst;

  public BxState(final DisassemblyRange range, final int address, final OpType opType, final Register dst) {
    super(range, address, opType);
    this.dst = dst;
  }

  @Override
  public void getReferents(final DisassemblerConfig config, final Set<Integer> referents) {
    LOGGER.warn("Unknowable referents for %s", this);
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    if(this.dst == Register.R14_LR) {
      output.addLine(this, "return %s;".formatted(Register.R0.fullName()));
    } else if(this.dst == Register.R15_PC) {
      output.addLine(this, "%s = MEMORY.call(0x%07x);".formatted(Register.R0.fullName(), this.address + 0x4));
    } else {
      output.addLine(this, "%s = MEMORY.call(%s);".formatted(Register.R0.fullName(), this.dst.fullName()));
    }
  }

  @Override
  public String toString() {
    return "%s %s".formatted(super.toString(), this.dst.name);
  }
}
