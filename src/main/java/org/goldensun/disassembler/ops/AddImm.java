package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class AddImm extends OpType {
  public AddImm() {
    super("ADD_IMM");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register dst = Register.values()[op & 0x7];
    final Register src = Register.values()[op >> 3 & 0x7];
    final int immediate = op >> 6 & 0x7;
    return new AddImmState(address, this, dst, src, immediate);
  }
}
