package org.goldensun.disassembler;

import org.goldensun.disassembler.ops.OpState;

import java.util.LinkedHashMap;
import java.util.Map;

public class Solver {
  public void solve(final DisassemblerConfig config, final Map<Integer, OpState> ops) {
    final Map<OpState, CpuState> cpuStates = new LinkedHashMap<>();
    CpuState currentCpuState = new CpuState();

    for(final OpState op : ops.values()) {
      op.run(config, currentCpuState);
      cpuStates.put(op, currentCpuState);
      currentCpuState = currentCpuState.clone();
    }
  }
}
