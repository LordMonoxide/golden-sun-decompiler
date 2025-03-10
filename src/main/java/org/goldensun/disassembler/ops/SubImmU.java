package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class SubImmU extends OpType {
  public SubImmU() {
    super("SUB_IMM_U");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register dst = Register.values()[op >> 8 & 0x7];
    final int immediate = op & 0xff;
    return new SubImmUState(address, this, dst, immediate);
  }
}
