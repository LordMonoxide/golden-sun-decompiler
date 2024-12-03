package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class AddImmState extends OpState {
  public final Register dst;
  public final Register src;
  public final int immediate;

  public AddImmState(final int address, final OpType opType, final Register dst, final Register src, final int immediate) {
    super(address, opType);
    this.dst = dst;
    this.src = src;
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
    return "%s %s,%s,0x%x".formatted(super.toString(), this.dst.name, this.src.name, this.immediate);
  }
}
