package org.goldensun.disassembler;

import org.goldensun.disassembler.values.Value;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CpuState {
  public final Map<Register, Set<RegisterUsage>> registerUsage = new EnumMap<>(Register.class);
  public final Map<Register, Value> registerValues = new EnumMap<>(Register.class);
  public final Map<Integer, Value> stackValues = new HashMap<>();
  public int stackDepth;

  public CpuState() {
    for(final Register register : Register.values()) {
      this.registerUsage.put(register, EnumSet.noneOf(RegisterUsage.class));
    }
  }

  public CpuState clone() {
    final CpuState newState = new CpuState();

    for(final Register register : Register.values()) {
      newState.registerUsage.get(register).addAll(this.registerUsage.get(register));
    }

    newState.registerValues.putAll(this.registerValues);
    newState.stackDepth = this.stackDepth;
    return newState;
  }
}
