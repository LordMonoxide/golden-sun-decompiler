package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class AddHi extends OpType {
  public AddHi() {
    super("ADD_HI");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register dst = Register.values()[op & 0x7 | (op >> 7 & 0x1) << 3];
    final Register src = Register.values()[op >> 3 & 0x7 | (op >> 6 & 0x1) << 3];
    return new AddHiState(address, this, dst, src);
  }
}
