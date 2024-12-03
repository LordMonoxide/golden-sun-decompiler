package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;

import java.util.ArrayList;
import java.util.List;

public class Pop extends OpType {
  public Pop() {
    super("POP");
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final List<Register> registers = new ArrayList<>();

    Register.unpack(registers, op & 0xff);

    if((op >> 8 & 0x1) != 0) {
      registers.add(Register.R15_PC);
    }

    return new PopState(range, address, this, registers.toArray(Register[]::new));
  }
}
