package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class LdrbImm extends OpType {
  public LdrbImm() {
    super("LDRB_IMM");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register dst = Register.values()[op & 0x7];
    final Register base = Register.values()[op >> 3 & 0x7];
    final int offset = op >> 6 & 0x1f;
    return new LdrbImmState(address, this, dst, base, offset);
  }
}
