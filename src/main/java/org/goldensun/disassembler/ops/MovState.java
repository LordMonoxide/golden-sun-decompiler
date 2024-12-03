package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.values.Value;

public class MovState extends OpState {
  public final Register dst;
  public final int imm;

  public MovState(final int address, final OpType opType, final Register dst, final int imm) {
    super(address, opType);
    this.dst = dst;
    this.imm = imm;
  }

  @Override
  public boolean zero() {
    return true;
  }

  @Override
  public boolean negative() {
    return true;
  }

  @Override
  public void run(final DisassemblerConfig config, final CpuState state) {
    state.registerUsage.get(this.dst).add(RegisterUsage.WRITE);
    state.registerValues.put(this.dst, Value.constant(this.imm));
  }

  @Override
  public String toString() {
    return "%s %s,0x%x".formatted(super.toString(), this.dst.name, this.imm);
  }
}
