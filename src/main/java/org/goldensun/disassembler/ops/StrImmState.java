package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class StrImmState extends OpState {
  public final Register src;
  public final Register base;
  public final int offset;

  public StrImmState(final int address, final OpType opType, final Register src, final Register base, final int offset) {
    super(address, opType);
    this.src = src;
    this.base = base;
    this.offset = offset;
  }

  @Override
  public String toString() {
    return "%s %s,[%s,0x%x]".formatted(super.toString(), this.src.name, this.base.name, this.offset);
  }
}
