package org.goldensun.disassembler;

import org.goldensun.disassembler.ops.OpState;
import org.goldensun.disassembler.ops.OpTypes;

import java.util.LinkedHashMap;
import java.util.Map;

public class Disassembler {
  public Map<Integer, OpState> disassemble(final DisassemblerConfig config) {
    final Map<Integer, OpState> ops = new LinkedHashMap<>();

    for(final DisassemblyRange range : config.disassemblyRanges) {
      this.disassemble(config.data, ops, range);
    }

    return ops;
  }

  private void disassemble(final byte[] data, final Map<Integer, OpState> ops, final DisassemblyRange range) {
    if(range.instructionSet != InstructionSet.THUMB) {
      throw new RuntimeException("Only THUMB supported for now");
    }

    for(int address = range.start; address <= range.end; ) {
      final OpState op = OpTypes.parse(data, address);
      ops.put(address, op);
      address += op.opType.getSize();
    }
  }
}
