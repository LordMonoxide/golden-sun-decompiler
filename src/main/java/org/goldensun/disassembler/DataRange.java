package org.goldensun.disassembler;

public class DataRange {
  public final int baseAddr;
  public final int start;
  public final int end;

  public DataRange(final int baseAddr, final int start, final int end) {
    this.baseAddr = baseAddr;
    this.start = start - baseAddr;
    this.end = end - baseAddr;
  }
}
