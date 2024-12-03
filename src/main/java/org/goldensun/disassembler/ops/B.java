package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;

import static org.goldensun.Util.sign;

public class B extends OpType {
  public B() {
    super("B");
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final int offset = sign(op & 0x7ff, 11) * 0x2;
    return new BState(range, address, this, offset);
  }
}
