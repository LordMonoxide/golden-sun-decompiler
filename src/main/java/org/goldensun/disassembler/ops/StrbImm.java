package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class StrbImm extends OpType {
  public StrbImm() {
    super("STRB_IMM");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register dst = Register.values()[op & 0x7];
    final Register base = Register.values()[op >> 3 & 0x7];
    final int offset = op >> 6 & 0x1f;
    return new StrbImmState(address, this, dst, base, offset);
  }
}
