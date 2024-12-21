package org.goldensun.disassembler.ops;

import static org.goldensun.Util.sign;

public class ConditionalBranch extends OpType {
  private final boolean readsOverflow;
  private final boolean readsCarry;
  private final boolean readsZero;
  private final boolean readsNegative;
  private final String condition;
  private final String conditionFormat;
  private final String hint;

  public ConditionalBranch(final String name, final boolean readsOverflow, final boolean readsCarry, final boolean readsZero, final boolean readsNegative, final String condition, final String conditionFormat, final String hint) {
    super(name);
    this.readsOverflow = readsOverflow;
    this.readsCarry = readsCarry;
    this.readsZero = readsZero;
    this.readsNegative = readsNegative;
    this.condition = condition;
    this.conditionFormat = conditionFormat;
    this.hint = hint;
  }

  @Override
  public boolean readsOverflow() {
    return this.readsOverflow;
  }

  @Override
  public boolean readsCarry() {
    return this.readsCarry;
  }

  @Override
  public boolean readsZero() {
    return this.readsZero;
  }

  @Override
  public boolean readsNegative() {
    return this.readsNegative;
  }

  @Override
  public OpState parse(final int address, final int op) {
    final int offset = sign(op & 0xff, 8) * 0x2;
    return new ConditionalBranchState(address, this, this.condition, this.conditionFormat, this.hint, offset);
  }
}
