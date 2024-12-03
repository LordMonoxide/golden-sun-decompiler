package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

public class AddRegState extends OpState {
  public final Register dst;
  public final Register src;
  public final Register operand;

  public AddRegState(final int address, final OpType opType, final Register dst, final Register src, final Register operand) {
    super(address, opType);
    this.dst = dst;
    this.src = src;
    this.operand = operand;
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
    return "%s %s,%s,%s".formatted(super.toString(), this.dst.name, this.src.name, this.operand.name);
  }
}