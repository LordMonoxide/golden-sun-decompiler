package org.goldensun.disassembler;

public class DisassemblyRange {
  public final InstructionSet instructionSet;
  public final int start;
  public final int end;

  public DisassemblyRange(final InstructionSet instructionSet, final int start, final int end) {
    this.instructionSet = instructionSet;
    this.start = start;
    this.end = end;
  }
}
