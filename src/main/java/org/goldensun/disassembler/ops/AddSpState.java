package org.goldensun.disassembler.ops;

public class AddSpState extends OpState {
  public final int amount;

  public AddSpState(final int address, final OpType opType, final int amount) {
    super(address, opType);
    this.amount = amount;
  }

  @Override
  public String toString() {
    return "%s sp,0x%x".formatted(super.toString(), this.amount);
  }
}
