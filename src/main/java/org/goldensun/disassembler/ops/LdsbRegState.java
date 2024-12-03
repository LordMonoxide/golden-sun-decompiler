package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class LdsbRegState extends OpState {
  public final Register dst;
  public final Register base;
  public final Register offset;

  public LdsbRegState(final int address, final OpType opType, final Register dst, final Register base, final Register offset) {
    super(address, opType);
    this.dst = dst;
    this.base = base;
    this.offset = offset;
  }

  @Override
  public String toString() {
    return "%s %s,[%s,%s]".formatted(super.toString(), this.dst.name, this.base.name, this.offset.name);
  }
}
