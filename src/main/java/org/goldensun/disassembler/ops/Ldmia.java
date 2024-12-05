package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

import java.util.ArrayList;
import java.util.List;

public class Ldmia extends OpType {
  public Ldmia() {
    super("LDMIA");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final List<Register> registers = new ArrayList<>();
    Register.unpack(registers, op & 0xff);
    final Register base = Register.values()[op >> 8 & 0x7];
    return new LdmiaState(address, this, registers.toArray(Register[]::new), base);
  }
}
