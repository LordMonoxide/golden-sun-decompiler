package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;

public class Alu extends OpType {
  private final AluState state;

  public Alu(final String name, final AluState state) {
    super(name);
    this.state = state;
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final Register dst = Register.values()[op & 0x7];
    final Register src = Register.values()[op >> 3 & 0x7];
    return this.state.run(range, address, this, dst, src);
  }
}
