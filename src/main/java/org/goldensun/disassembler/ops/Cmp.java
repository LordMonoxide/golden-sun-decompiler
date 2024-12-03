package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;

public class Cmp extends OpType {
  public Cmp() {
    super("CMP");
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final Register dst = Register.values()[op >> 8 & 0x7];
    final int immediate = op & 0xff;
    return new CmpState(range, address, this, dst, immediate);
  }
}
