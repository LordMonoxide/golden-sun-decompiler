package org.goldensun.disassembler.ops;

public class OpType {
  public final String name;

  public OpType(final String name) {
    this.name = name;
  }

  public int getSize() {
    return 2;
  }

  public boolean readsOverflow() {
    return false;
  }

  public boolean readsCarry() {
    return false;
  }

  public boolean readsZero() {
    return false;
  }

  public boolean readsNegative() {
    return false;
  }

  public boolean readsConditions() {
    return this.readsOverflow() || this.readsCarry() || this.readsZero() || this.readsNegative();
  }

  public OpState parse(final int address, final int op) {
    throw new RuntimeException("0x%x: %s parsing not implemented".formatted(address, this.name));
  }

  @Override
  public String toString() {
    return this.name;
  }
}
