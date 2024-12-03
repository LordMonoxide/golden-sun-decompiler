package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;

import java.util.Set;

public class BlState extends OpState {
  public final int offset;

  public BlState(final int address, final OpType opType, final int offset) {
    super(address, opType);
    this.offset = offset;
  }

  @Override
  public void getReferents(final DisassemblerConfig config, final Set<Integer> referents) {
    if(config.codeContains(this.getDest())) {
      // bl used as local jump treated as a regular jump
      referents.add(this.getDest());
    } else {
      // bl to another function will return
      super.getReferents(config, referents);
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
