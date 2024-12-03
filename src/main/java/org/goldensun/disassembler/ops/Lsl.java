package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class Lsl extends OpType {
  public Lsl() {
    super("LSL");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register dst = Register.values()[op & 0x7];
    final Register src = Register.values()[op >> 3 & 0x7];
    final int offset = op >> 6 & 0x1f;
    return new LslState(address, this, dst, src, offset);
  }
}
