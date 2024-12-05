package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class CmpAlu extends OpType {
  public CmpAlu() {
    super("CMP_ALU");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register a = Register.values()[op & 0x7];
    final Register b = Register.values()[op >> 3 & 0x7];
    return new CmpAluState(address, this, a, b);
  }
}
