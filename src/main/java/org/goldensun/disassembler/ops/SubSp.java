package org.goldensun.disassembler.ops;

public class SubSp extends OpType {
  public SubSp() {
    super("SUB_SP");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final int amount = (op & 0x7f) * 0x4;
    return new SubSpState(address, this, amount);
  }
}
