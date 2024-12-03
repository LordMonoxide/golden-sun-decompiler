package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;

public class SubImm extends OpType {
  public SubImm() {
    super("SUB_IMM");
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final Register dst = Register.values()[op & 0x7];
    final Register src = Register.values()[op >> 3 & 0x7];
    final int immediate = op >> 6 & 0x7;
    return new SubImmState(range, address, this, dst, src, immediate);
  }
}