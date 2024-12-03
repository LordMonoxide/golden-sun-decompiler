package org.goldensun.disassembler;

import java.util.ArrayList;
import java.util.List;

public class DisassemblerConfig {
  public final List<DisassemblyRange> disassemblyRanges = new ArrayList<>();
  public final List<DataRange> dataRanges = new ArrayList<>();
  public final List<Integer> switches = new ArrayList<>();

  public byte[] data;

  public boolean codeContains(final int address) {
    for(final DisassemblyRange range : this.disassemblyRanges) {
      if(range.contains(address)) {
        return true;
      }
    }

    return false;
  }
}
