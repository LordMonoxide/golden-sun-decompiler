package org.goldensun.disassembler.ops;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;

import java.util.Set;

public class BxState extends OpState {
  private static final Logger LOGGER = LogManager.getFormatterLogger(BxState.class);

  public final Register offset;

  public BxState(final int address, final OpType opType, final Register offset) {
    super(address, opType);
    this.offset = offset;
  }

  @Override
  public void getReferents(final DisassemblerConfig config, final Set<Integer> referents) {
    LOGGER.warn("Unknowable referents for %s", this);
  }

  @Override
  public String toString() {
    return "%s %s".formatted(super.toString(), this.offset.name);
  }
}
