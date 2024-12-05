package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;
import org.goldensun.disassembler.values.Value;

public class LdrPcState extends OpState {
  public final Register dst;
  public final int offset;

  public LdrPcState(final int address, final OpType opType, final Register dst, final int offset) {
    super(address, opType);
    this.dst = dst;
    this.offset = offset;
  }

  public int getAddress() {
    return this.address + 0x4 + this.offset * 0x4 & ~0x2;
  }

  @Override
  public void run(final DisassemblerConfig config, final CpuState state) {
    final int address = this.getAddress();
    state.registerUsage.get(this.dst).add(RegisterUsage.WRITE);
    state.registerValues.put(this.dst, Value.constant(config.memory.get(address, 4)));
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    output.addLine(this, "%s = 0x%x;".formatted(this.dst.fullName(), config.memory.get(this.getAddress(), 4)));
  }

  @Override
  public String toString() {
    return "%s %s,[0x%x]".formatted(super.toString(), this.dst.name, this.getAddress());
  }
}
