package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

import java.util.ArrayList;
import java.util.List;

public class Push extends OpType {
  public Push() {
    super("PUSH");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final List<Register> registers = new ArrayList<>();

    if((op >> 8 & 0x1) != 0) {
      registers.add(Register.R14_LR);
    }

    Register.unpack(registers, op & 0xff);
    return new PushState(address, this, registers.toArray(Register[]::new));
  }
}
