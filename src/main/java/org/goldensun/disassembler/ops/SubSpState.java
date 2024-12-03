package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;

public class SubSpState extends OpState {
  public final int amount;

  public SubSpState(final int address, final OpType opType, final int amount) {
    super(address, opType);
    this.amount = amount;
  }

  @Override
  public void run(final DisassemblerConfig config, final CpuState state) {
    state.stackDepth += this.amount;
  }

  @Override
  public String toString() {
    return "%s sp,0x%x".formatted(super.toString(), this.amount);
  }
}
