package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;

public class AddReg extends OpType {
  public AddReg() {
    super("ADD_REG");
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final Register dst = Register.values()[op & 0x7];
    final Register src = Register.values()[op >> 3 & 0x7];
    final Register operand = Register.values()[op >> 6 & 0x7];
    return new AddRegState(range, address, this, dst, src, operand);
  }
}
