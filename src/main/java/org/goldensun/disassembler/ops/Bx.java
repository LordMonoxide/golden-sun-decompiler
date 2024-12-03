package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;

public class Bx extends OpType {
  public Bx() {
    super("BX");
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final Register dst = Register.values()[op >> 3 & 0x7 | (op >> 6 & 0x1) << 3];
    return new BxState(range, address, this, dst);
  }
}
