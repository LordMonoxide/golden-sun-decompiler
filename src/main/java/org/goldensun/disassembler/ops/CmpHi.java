package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class CmpHi extends OpType {
  public CmpHi() {
    super("CMP_HI");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register a = Register.values()[op & 0x7 | (op >> 7 & 0x1) << 3];
    final Register b = Register.values()[op >> 3 & 0x7 | (op >> 6 & 0x1) << 3];
    return new CmpStateHi(address, this, a, b);
  }
}
