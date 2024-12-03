package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;

import java.util.Set;

public class ConditionalBranchState extends OpState {
  public final int offset;

  public ConditionalBranchState(final int address, final OpType opType, final int offset) {
    super(address, opType);
    this.offset = offset;
  }

  @Override
  public void getReferents(final DisassemblerConfig config, final Set<Integer> referents) {
    super.getReferents(config, referents);

    if(config.codeContains(this.getDest())) {
      referents.add(this.getDest());
    }
  }

  public int getDest() {
    return this.address + 0x4 + this.offset;
  }

  @Override
  public String toString() {
    return "%s 0x%x".formatted(super.toString(), this.getDest());
  }
}
