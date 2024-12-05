package org.goldensun.disassembler.ops;

public class AddSp extends OpType {
  public AddSp() {
    super("ADD_SP");
  }

  @Override
  public OpState parse(final int address, final int op) {
    final int amount = (op & 0x7f) * 0x4;
    return new AddSpState(address, this, amount);
  }
}
