package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class SubReg extends OpType {
  public SubReg() {
    super("SUB_REG");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register dst = Register.values()[op & 0x7];
    final Register src = Register.values()[op >> 3 & 0x7];
    final Register operand = Register.values()[op >> 6 & 0x7];
    return new SubRegState(address, this, dst, src, operand);
  }
}
