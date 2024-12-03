package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;

public class MovHi extends OpType {
  public MovHi() {
    super("MOV_HI");
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final Register dst = Register.values()[op & 0x7 | (op >> 7 & 0x1) << 3];
    final Register src = Register.values()[op >> 3 & 0x7 | (op >> 6 & 0x1) << 3];
    return new MovHiState(range, address, this, dst, src);
  }
}
