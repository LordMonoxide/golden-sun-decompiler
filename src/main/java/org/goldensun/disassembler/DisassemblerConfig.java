package org.goldensun.disassembler;

import org.goldensun.memory.Memory;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisassemblerConfig {
  public final List<DisassemblyRange> disassemblyRanges = new ArrayList<>();
  public final List<SwitchConfig> switches = new ArrayList<>();
  public final Map<Integer, String> functionNames = new HashMap<>();

  public final Memory memory;
  public final PrintWriter writer;

  public DisassemblerConfig(final Memory memory, final PrintWriter writer) {
    this.memory = memory;
    this.writer = writer;
  }
}
