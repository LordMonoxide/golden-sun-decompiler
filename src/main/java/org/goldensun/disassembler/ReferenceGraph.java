package org.goldensun.disassembler;

import org.goldensun.disassembler.ops.OpState;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class ReferenceGraph {
  /** List of ops that reference the key */
  private final Map<OpState, List<OpState>> references;

  public ReferenceGraph(final Map<OpState, List<OpState>> references) {
    this.references = references;
  }

  public Set<OpState> ops() {
    return this.references.keySet();
  }

  public Stream<OpState> stream() {
    return this.references.keySet().stream();
  }

  public OpState last() {
    return this.stream().max(Comparator.comparingInt(OpState::getAddress)).get();
  }

  /** Note: stack depth is relative from the first op visited */
  public void backtrack(final BiFunction<OpState, Integer, FlowControl> visitor) {
    this.backtrack(this.last(), visitor);
  }

  /** Note: stack depth is relative from the first op visited */
  public void backtrack(final OpState op, final BiFunction<OpState, Integer, FlowControl> visitor) {
    this.backtrack(op, visitor, new HashSet<>(), 0);
  }

  public void put(final OpState op, final List<OpState> references) {
    this.references.put(op, references);
  }

  public void remove(final OpState op) {
    this.references.values().stream()
      .filter(opStates -> opStates.contains(op))
      .forEach(e -> {
        // Remove references to op and replace them with ops that the removed op references
        e.remove(op);
        e.addAll(this.references.get(op));
      });

    this.references.remove(op);
  }

  public List<OpState> getReferences(final OpState op) {
    return this.references.get(op);
  }

  /** TODO can probably run a lot of this recursion as a simple loop to reduce stack depth */
  private FlowControl backtrack(final OpState op, final BiFunction<OpState, Integer, FlowControl> visitor, final Set<OpState> visited, int stackDepth) {
    if(visited.contains(op)) {
      return FlowControl.CONTINUE;
    }

    stackDepth -= op.getStackDepthChange();

    visited.add(op);
    final FlowControl flow = visitor.apply(op, stackDepth);

    if(flow != FlowControl.CONTINUE) {
      return FlowControl.TERMINATE_BRANCH;
    }

    for(final OpState referent : this.references.get(op)) {
      final FlowControl branchFlow = this.backtrack(referent, visitor, visited, stackDepth);

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
