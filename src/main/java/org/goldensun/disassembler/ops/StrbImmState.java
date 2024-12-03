package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class StrbImmState extends OpState {
  public final Register dst;
  public final Register base;
  public final int offset;

  public StrbImmState(final int address, final OpType opType, final Register dst, final Register base, final int offset) {
    super(address, opType);
    this.dst = dst;
    this.base = base;
    this.offset = offset;
  }

  @Override
  public String toString() {
    return "%s %s,[%s,0x%x]".formatted(super.toString(), this.dst.name, this.base.name, this.offset);
  }
}
