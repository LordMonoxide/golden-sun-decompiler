package org.goldensun.disassembler;

import org.goldensun.Util;

import java.util.ArrayList;
import java.util.List;

public class DisassemblerConfig {
  public final List<DisassemblyRange> disassemblyRanges = new ArrayList<>();
  public final List<DataRange> dataRanges = new ArrayList<>();
  public final List<SwitchConfig> switches = new ArrayList<>();

  public byte[] data;

  public boolean codeContains(final int address) {
    for(final DisassemblyRange range : this.disassemblyRanges) {
      if(range.contains(address)) {
        return true;
      }
    }

    return false;
  }

  public boolean dataContains(final int address) {
    for(final DataRange range : this.dataRanges) {
      if(range.contains(address)) {
        return true;
      }
    }

    return false;
  }

  public int read(final int offset, final int size) {
    return Util.get(this.data, offset, size);
  }
}
