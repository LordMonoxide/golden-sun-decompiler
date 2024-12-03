package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.values.Value;

public class MovHiState extends OpState {
  public final Register dst;
  public final Register src;

  public MovHiState(final int address, final OpType opType, final Register dst, final Register src) {
    super(address, opType);
    this.dst = dst;
    this.src = src;
  }

  @Override
  public void run(final DisassemblerConfig config, final CpuState state) {
    state.registerUsage.get(this.dst).add(RegisterUsage.WRITE);
    state.registerUsage.get(this.dst).add(RegisterUsage.READ);
    state.registerValues.put(this.dst, Value.register(this.src));
  }

  @Override
  public String toString() {
    return "%s %s,%s".formatted(super.toString(), this.dst.name, this.src.name);
  }
}
