package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class Swi extends OpType {
  public Swi() {
    super("SWI");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final int index = op & 0xff;
    return new SwiState(address, this, index);
  }
}
