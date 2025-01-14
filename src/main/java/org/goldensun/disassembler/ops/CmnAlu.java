package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class CmnAlu extends OpType {
  public CmnAlu() {
    super("CMN_ALU");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register a = Register.values()[op & 0x7];
    final Register b = Register.values()[op >> 3 & 0x7];
    return new CmnAluState(address, this, a, b);
  }
}
