package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;

import static org.goldensun.Util.sign;

public class Bl extends OpType {
  public Bl(final String name) {
    super(name);
  }

  @Override
  public int getSize() {
    return 4;
  }

  @Override
  public OpState parse(final DisassemblyRange range, final int address, final int op) {
    final int lower = op & 0x7ff;
    final int upper = op >>> 16 & 0x7ff;
    final int offset = sign(upper << 1 | lower << 12, 23);
    return new BlState(range, address, this, offset);
  }
}
