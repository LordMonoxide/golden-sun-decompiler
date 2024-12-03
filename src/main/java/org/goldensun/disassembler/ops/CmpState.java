package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class CmpState extends OpState {
  public final Register dst;
  public final int immediate;

  public CmpState(final int address, final OpType opType, final Register dst, final int immediate) {
    super(address, opType);
    this.dst = dst;
    this.immediate = immediate;
  }

  @Override
  public boolean overflow() {
    return true;
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
    return "%s %s,0x%x".formatted(super.toString(), this.dst.name, this.immediate);
  }
}
