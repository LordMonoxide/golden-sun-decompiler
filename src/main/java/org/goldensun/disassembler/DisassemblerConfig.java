package org.goldensun.disassembler;

import org.goldensun.FunctionInfo;
import org.goldensun.memory.Memory;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisassemblerConfig {
  public final List<DisassemblyRange> disassemblyRanges = new ArrayList<>();
  public final List<SwitchConfig> switches = new ArrayList<>();
  public final Map<Integer, FunctionInfo> functions;
  /** Overrides for decompiled function names */
  public final Map<Integer, String> functionNameOverrides;
  public final Map<Integer, List<String>> docs;

  public final Memory memory;
  public final PrintWriter writer;

  private DisassemblerConfig(final Memory memory, final PrintWriter writer, final Map<Integer, FunctionInfo> functions, final Map<Integer, String> functionNameOverrides, final Map<Integer, List<String>> docs) {
    this.memory = memory;
    this.writer = writer;
    this.functions = functions;
    this.functionNameOverrides = functionNameOverrides;
    this.docs = docs;
  }

  public DisassemblerConfig(final Memory memory, final PrintWriter writer) {
    this(memory, writer, new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  public DisassemblerConfig(final DisassemblerConfig other) {
    this(other.memory, other.writer, other.functions, other.functionNameOverrides, other.docs);
  }
}
