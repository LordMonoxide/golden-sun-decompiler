package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;

public class LslAluState extends OpState {
  public final Register dst;
  public final Register src;

  public LslAluState(final DisassemblyRange range, final int address, final OpType opType, final Register dst, final Register src) {
    super(range, address, opType);
    this.dst = dst;
    this.src = src;
  }

  @Override
  public boolean carry() {
    return true;
  }

  @Override
  public boolean zero() {
    return true;
  }

  @Override
  public boolean negative() {
    return true;
  }

  @Override
  public String toString() {
    return "%s %s,%s".formatted(super.toString(), this.dst.name, this.src.name);
  }
}
