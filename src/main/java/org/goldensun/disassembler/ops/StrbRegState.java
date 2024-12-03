package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class StrbRegState extends OpState {
  public final Register src;
  public final Register base;
  public final Register offset;

  public StrbRegState(final int address, final OpType opType, final Register src, final Register base, final Register offset) {
    super(address, opType);
    this.src = src;
    this.base = base;
    this.offset = offset;
  }

  @Override
  public String toString() {
    return "%s %s,[%s,%s]".formatted(super.toString(), this.src.name, this.base.name, this.offset.name);
  }
}
