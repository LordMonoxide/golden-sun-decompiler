package org.goldensun;

import org.goldensun.disassembler.ops.OpState;

import java.util.Map;
import java.util.Set;

public class TransformedOutput {
  public final int address;
  public final Set<OpState> ops;
  public final Map<OpState, Set<OpState>> conditionDependencies;

  public TransformedOutput(final int address, final Set<OpState> ops, final Map<OpState, Set<OpState>> conditionDependencies) {
    this.address = address;
    this.ops = ops;
    this.conditionDependencies = conditionDependencies;
  }
}
