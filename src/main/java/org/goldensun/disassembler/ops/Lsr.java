package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;

public class Lsr extends OpType {
  public Lsr() {
    super("LSR");
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final Register dst = Register.values()[op & 0x7];
    final Register src = Register.values()[op >> 3 & 0x7];
    final int amount = op >> 6 & 0x1f;
    return new LsrState(range, address, this, dst, src, amount);
  }
}
