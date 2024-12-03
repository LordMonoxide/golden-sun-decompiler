package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;

public class StrSp extends OpType {
  public StrSp() {
    super("STR_SP");
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final Register src = Register.values()[op >>> 8 & 0x7];
    final int offset = (op & 0xff) * 0x4;
    return new StrSpState(range, address, this, src, offset);
  }
}
