package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class LdrSpState extends OpState {
  public final Register dst;
  public final int offset;

  public LdrSpState(final int address, final OpType opType, final Register dst, final int offset) {
    super(address, opType);
    this.dst = dst;
    this.offset = offset;
  }

  @Override
  public String toString() {
    return "%s %s,[sp,0x%x]".formatted(super.toString(), this.dst.name, this.offset);
  }
}
