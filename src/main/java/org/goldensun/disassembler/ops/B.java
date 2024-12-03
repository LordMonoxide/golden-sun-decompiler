package org.goldensun.disassembler.ops;

import static org.goldensun.Util.sign;

public class B extends OpType {
  public B() {
    super("B");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final int offset = sign(op & 0x7ff, 11) * 0x2;
    return new BState(address, this, offset);
  }
}
