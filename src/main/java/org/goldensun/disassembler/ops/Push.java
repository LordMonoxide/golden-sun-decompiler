package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Push extends OpType {
  public Push() {
    super("PUSH");
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final List<Register> registers = new ArrayList<>();

    Register.unpack(registers, op & 0xff);
    if((op >> 8 & 0x1) != 0) {
      registers.add(Register.R14_LR);
    }

    return new PushState(range, address, this, registers.stream().sorted(Comparator.reverseOrder()).toArray(Register[]::new));
  }
}
