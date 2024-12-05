package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

public class StrbImmState extends OpState {
  public final Register dst;
  public final Register base;
  public final int offset;

  public StrbImmState(final int address, final OpType opType, final Register dst, final Register base, final int offset) {
    super(address, opType);
    this.dst = dst;
    this.base = base;
    this.offset = offset;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    output.addLine(this, "MEMORY.ref(1, %s + 0x%x).setu(%s);".formatted(this.base.fullName(), this.offset, this.dst.fullName()));
  }

  @Override
  public String toString() {
    return "%s %s,[%s,0x%x]".formatted(super.toString(), this.dst.name, this.base.name, this.offset);
  }
}
