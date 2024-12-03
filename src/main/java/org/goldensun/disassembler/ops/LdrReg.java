package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class LdrReg extends OpType {
  public LdrReg() {
    super("LDR_REG");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register dst = Register.values()[op & 0x7];
    final Register base = Register.values()[op >> 3 & 0x7];
    final Register offset = Register.values()[op >> 6 & 0x7];
    return new LdrRegState(address, this, dst, base, offset);
  }
}
