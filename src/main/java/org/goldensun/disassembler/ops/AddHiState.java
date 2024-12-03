package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class AddHiState extends OpState {
  public final Register dst;
  public final Register src;

  public AddHiState(final int address, final OpType opType, final Register dst, final Register src) {
    super(address, opType);
    this.dst = dst;
    this.src = src;
  }

  @Override
  public String toString() {
    return "%s %s,%s".formatted(super.toString(), this.dst.name, this.src.name);
  }
}
