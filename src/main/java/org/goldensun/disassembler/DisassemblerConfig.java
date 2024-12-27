package org.goldensun.disassembler;

import org.goldensun.FunctionInfo;
import org.goldensun.memory.Memory;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DisassemblerConfig {
  public final List<SwitchConfig> switches = new ArrayList<>();
  public final Map<Integer, FunctionInfo> functions;
  /** Overrides for decompiled function names */
  public final Map<Integer, String> functionNameOverrides;
  public final Map<Integer, List<String>> docs;
  /** Add `ignoreExtraParams = true` to @Method annotation for each address in this list */
  public final Set<Integer> ignoreParams;

  public final Memory memory;
  public final PrintWriter writer;

  public int address;

  private DisassemblerConfig(final Memory memory, final PrintWriter writer, final Map<Integer, FunctionInfo> functions, final Map<Integer, String> functionNameOverrides, final Map<Integer, List<String>> docs, final Set<Integer> ignoreParams) {
    this.memory = memory;
    this.writer = writer;
    this.functions = functions;
    this.functionNameOverrides = functionNameOverrides;
    this.docs = docs;
    this.ignoreParams = ignoreParams;
  }

  public DisassemblerConfig(final Memory memory, final PrintWriter writer) {
    this(memory, writer, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashSet<>());
  }

  public DisassemblerConfig(final DisassemblerConfig other) {
    this(other.memory, other.writer, other.functions, other.functionNameOverrides, other.docs, other.ignoreParams);
    this.switches.addAll(other.switches);
  }
}
