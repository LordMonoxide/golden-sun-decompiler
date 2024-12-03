package org.goldensun.disassembler;

import org.goldensun.disassembler.ops.OpState;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class ReferenceGraph {
  private final Map<OpState, List<OpState>> references;

  public ReferenceGraph(final Map<OpState, List<OpState>> references) {
    this.references = references;
  }

  public Stream<OpState> stream() {
    return this.references.keySet().stream();
  }

  public void backtrack(final OpState op, final Function<OpState, FlowControl> visitor) {
    this.backtrack(op, visitor, new HashSet<>());
  }

  /** TODO can probably run a lot of this recursion as a simple loop to reduce stack depth */
  private FlowControl backtrack(final OpState op, final Function<OpState, FlowControl> visitor, final Set<OpState> visited) {
    if(visited.contains(op)) {
      return FlowControl.TERMINATE_BRANCH;
    }

    visited.add(op);
    final FlowControl flow = visitor.apply(op);

    if(flow != FlowControl.CONTINUE) {
      return FlowControl.TERMINATE_BRANCH;
    }

    for(final OpState referent : this.references.get(op)) {
      final FlowControl branchFlow = this.backtrack(referent, visitor, visited);

      if(branchFlow == FlowControl.TERMINATE_BRANCH) {
        break;
      }

      if(branchFlow == FlowControl.TERMINATE_ALL) {
        return FlowControl.TERMINATE_ALL;
      }
    }

    return FlowControl.CONTINUE;
  }
}
