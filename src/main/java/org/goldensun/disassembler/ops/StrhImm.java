package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class StrhImm extends OpType {
  public StrhImm() {
    super("STRH_IMM");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register src = Register.values()[op & 0x7];
    final Register base = Register.values()[op >> 3 & 0x7];
    final int offset = (op >> 6 & 0x1f) * 0x2;
    return new StrhImmState(address, this, src, base, offset);
  }
}
