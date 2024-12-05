package org.goldensun.disassembler;

import org.goldensun.memory.Memory;

import java.util.ArrayList;
import java.util.List;

public class DisassemblerConfig {
  public final List<DisassemblyRange> disassemblyRanges = new ArrayList<>();
  public final List<SwitchConfig> switches = new ArrayList<>();

  public final Memory memory;

  public DisassemblerConfig(final Memory memory) {
    this.memory = memory;
  }

  public boolean codeContains(final int address) {
    for(final DisassemblyRange range : this.disassemblyRanges) {
      if(range.contains(address)) {
        return true;
      }
    }

    return false;
  }
}
