package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

public class LdrSpState extends OpState {
  public final Register dst;
  public final int offset;

  public LdrSpState(final DisassemblyRange range, final int address, final OpType opType, final Register dst, final int offset) {
    super(range, address, opType);
    this.dst = dst;
    this.offset = offset;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    output.addLine(this, "%s = MEMORY.ref(4, %s + 0x%x).get();".formatted(this.dst.fullName(), Register.R13_SP.fullName(), this.offset));
  }

  @Override
  public String toString() {
    return "%s %s,[sp,0x%x]".formatted(super.toString(), this.dst.name, this.offset);
  }
}
