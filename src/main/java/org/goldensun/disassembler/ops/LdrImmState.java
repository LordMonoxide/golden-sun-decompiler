package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.values.Value;

public class LdrImmState extends OpState {
  public final Register dst;
  public final Register base;
  public final int offset;

  public LdrImmState(final int address, final OpType opType, final Register dst, final Register base, final int offset) {
    super(address, opType);
    this.dst = dst;
    this.base = base;
    this.offset = offset;
  }

  @Override
  public void run(final DisassemblerConfig config, final CpuState state) {
    state.registerUsage.get(this.dst).add(RegisterUsage.WRITE);
    state.registerUsage.get(this.base).add(RegisterUsage.READ);
    state.registerValues.put(this.dst, Value.memory(4, this.base, this.offset, false));
  }

  @Override
  public String toString() {
    return "%s %s,[%s,0x%x]".formatted(super.toString(), this.dst.name, this.base.name, this.offset);
  }
}
