package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class StrReg extends OpType {
  public StrReg() {
    super("STR_REG");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final Register src = Register.values()[op & 0x7];
    final Register base = Register.values()[op >> 3 & 0x7];
    final Register offset = Register.values()[op >> 6 & 0x7];
    return new StrRegState(address, this, src, base, offset);
  }
}
