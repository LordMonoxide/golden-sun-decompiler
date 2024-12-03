package org.goldensun.disassembler;

public enum InstructionSet {
  THUMB(2),
  ARM(4),
  ;

  public final int opSize;

  InstructionSet(final int opSize) {
    this.opSize = opSize;
  }
}
