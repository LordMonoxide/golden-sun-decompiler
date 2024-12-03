package org.goldensun.disassembler;

public class DisassemblyRange {
  public final InstructionSet instructionSet;
  public final int baseAddr;
  public final int start;
  public final int end;

  public DisassemblyRange(final InstructionSet instructionSet, final int baseAddr, final int start, final int end) {
    this.instructionSet = instructionSet;
    this.baseAddr = baseAddr;
    this.start = start - baseAddr;
    this.end = end - baseAddr;
  }

  public boolean contains(int address) {
    return address >= this.start && address < this.end + this.instructionSet.opSize;
  }
}