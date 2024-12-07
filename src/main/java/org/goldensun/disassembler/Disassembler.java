package org.goldensun.disassembler;

import org.goldensun.disassembler.ops.OpState;
import org.goldensun.disassembler.ops.OpTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Disassembler {
  public Map<Integer, OpState> disassemble(final DisassemblerConfig config) {
    final Map<Integer, OpState> ops = new HashMap<>();
    this.disassembleBranch(config, ops, config.address);
    return ops;
  }

  private void disassembleBranch(final DisassemblerConfig config, final Map<Integer, OpState> ops, final int address) {
    if(ops.containsKey(address)) {
      return;
    }

    final OpState op = OpTypes.parse(config.memory, address);
    ops.put(address, op);

    final Set<Integer> referents = new HashSet<>();
    op.getReferents(config, referents);

    for(final int referent : referents) {
      this.disassembleBranch(config, ops, referent);
    }
  }
}
