package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class Mov extends OpType {
  public Mov() {
    super("MOV");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register dst = Register.values()[op >> 8 & 0x7];
    final int imm = op & 0xff;
    return new MovState(address, this, dst, imm);
  }
}
