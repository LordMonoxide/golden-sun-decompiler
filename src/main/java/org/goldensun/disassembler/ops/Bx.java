package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class Bx extends OpType {
  public Bx() {
    super("BX");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register offset = Register.values()[op >> 3 & 0x7 | (op >> 6 & 0x1) << 3];
    return new BxState(address, this, offset);
  }
}
