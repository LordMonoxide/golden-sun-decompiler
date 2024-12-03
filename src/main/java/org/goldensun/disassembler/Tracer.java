package org.goldensun.disassembler;

import org.goldensun.disassembler.ops.OpState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tracer {
  public ReferenceGraph trace(final DisassemblerConfig config, final Map<Integer, OpState> ops) {
    final Map<OpState, List<OpState>> references = new HashMap<>();
    final Set<Integer> addresses = new HashSet<>();

    for(final Map.Entry<Integer, OpState> entry : ops.entrySet()) {
      final OpState op = entry.getValue();

      addresses.clear();
      op.getReferents(config, addresses);
      references.computeIfAbsent(op, k -> new ArrayList<>());

      for(final int address : addresses) {
        if(!ops.containsKey(address)) {
          throw new RuntimeException("Unknown referent address 0x%x for op %s".formatted(address, op));
        }

        references.computeIfAbsent(ops.get(address), k -> new ArrayList<>()).add(op);
      }
    }

    return new ReferenceGraph(references);
  }
}
