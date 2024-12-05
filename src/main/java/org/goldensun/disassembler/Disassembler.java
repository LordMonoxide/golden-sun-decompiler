package org.goldensun.disassembler;

import org.goldensun.disassembler.ops.OpState;
import org.goldensun.disassembler.ops.OpTypes;
import org.goldensun.memory.Memory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Disassembler {
  public Map<Integer, OpState> disassemble(final DisassemblerConfig config) {
    final Map<Integer, OpState> ops = new HashMap<>();

//    for(final DisassemblyRange range : config.disassemblyRanges) {
//      this.disassemble(config.memory, ops, range);
//    }

    this.disassembleBranch(config, ops, config.disassemblyRanges.get(0).start);
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

  private void disassemble(final Memory memory, final Map<Integer, OpState> ops, final DisassemblyRange range) {
    if(range.instructionSet != InstructionSet.THUMB) {
      throw new RuntimeException("Only THUMB supported for now");
    }

    for(int address = range.start; address <= range.end; ) {
      final OpState op = OpTypes.parse(memory, address);
      ops.put(address, op);
      address += op.opType.getSize();
    }
  }
}
