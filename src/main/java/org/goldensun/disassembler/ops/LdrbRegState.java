package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

public class LdrbRegState extends OpState {
  public final Register dst;
  public final Register base;
  public final Register offset;

  public LdrbRegState(final DisassemblyRange range, final int address, final OpType opType, final Register dst, final Register base, final Register offset) {
    super(range, address, opType);
    this.dst = dst;
    this.base = base;
    this.offset = offset;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    output.addLine(this, "%s = MEMORY.ref(1, %s + %s).getUnsigned();".formatted(this.dst.fullName(), this.base.fullName(), this.offset.fullName()));
  }

  @Override
  public String toString() {
    return "%s %s,[%s,%s]".formatted(super.toString(), this.dst, this.base, this.offset);
  }
}
