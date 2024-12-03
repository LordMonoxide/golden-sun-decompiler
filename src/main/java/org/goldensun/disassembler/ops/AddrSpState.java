package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.values.Value;

public class AddrSpState extends OpState {
  public final Register dst;
  public final int offset;

  public AddrSpState(final int address, final OpType opType, final Register dst, final int offset) {
    super(address, opType);
    this.dst = dst;
    this.offset = offset;
  }

  @Override
  public void run(final DisassemblerConfig config, final CpuState state) {
    state.registerUsage.get(this.dst).add(RegisterUsage.WRITE);
    state.registerValues.put(this.dst, Value.address(Register.R13_SP, this.offset));
  }

  @Override
  public String toString() {
    return "%s %s,[sp,0x%x]".formatted(super.toString(), this.dst.name, this.offset);
  }
}