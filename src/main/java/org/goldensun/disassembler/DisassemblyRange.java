package org.goldensun.disassembler;

public class DisassemblyRange {
  public final InstructionSet instructionSet;
  public final int baseAddr;
  public final int start;
  public final int end;

  public DisassemblyRange(final InstructionSet instructionSet, final int baseAddr, final int start, final int end) {
    this.instructionSet = instructionSet;
    this.baseAddr = baseAddr;
    this.start = start;
    this.end = end;
  }

  public boolean contains(final int address) {
    return address >= this.start && address < this.end + this.instructionSet.opSize;
  }
}
