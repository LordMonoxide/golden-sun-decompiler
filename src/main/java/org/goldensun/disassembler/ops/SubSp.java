package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;

public class SubSp extends OpType {
  public SubSp() {
    super("SUB_SP");
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final int amount = (op & 0x7f) * 0x4;
    return new SubSpState(range, address, this, amount);
  }
}
