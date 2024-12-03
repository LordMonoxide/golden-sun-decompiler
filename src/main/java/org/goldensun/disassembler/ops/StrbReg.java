package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;

public class StrbReg extends OpType {
  public StrbReg() {
    super("STRB_REG");
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final Register src = Register.values()[op & 0x7];
    final Register base = Register.values()[op >> 3 & 0x7];
    final Register offset = Register.values()[op >> 6 & 0x7];
    return new StrbRegState(range, address, this, src, base, offset);
  }
}
