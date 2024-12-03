package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class LdrPc extends OpType {
  public LdrPc() {
    super("LDRPC");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register dst = Register.values()[op >> 8 & 0x7];
    final int offset = op & 0xff;
    return new LdrPcState(address, this, dst, offset);
  }
}
