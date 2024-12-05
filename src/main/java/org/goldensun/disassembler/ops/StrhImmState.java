package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

public class StrhImmState extends OpState {
  public final Register dst;
  public final Register base;
  public final int immediate;

  public StrhImmState(final int address, final OpType opType, final Register dst, final Register base, final int immediate) {
    super(address, opType);
    this.dst = dst;
    this.base = base;
    this.immediate = immediate;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    output.addLine(this, "MEMORY.ref(2, %s + 0x%x).setu(%s);".formatted(this.base.fullName(), this.immediate, this.dst.fullName()));
  }

  @Override
  public String toString() {
    return "%s %s,[%s,0x%x]".formatted(super.toString(), this.dst.name, this.base.name, this.immediate);
  }
}
