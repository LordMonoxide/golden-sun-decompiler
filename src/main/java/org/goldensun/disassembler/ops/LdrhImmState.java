package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

public class LdrhImmState extends OpState {
  public final Register dst;
  public final Register base;
  public final int offset;

  public LdrhImmState(final DisassemblyRange range, final int address, final OpType opType, final Register dst, final Register base, final int offset) {
    super(range, address, opType);
    this.dst = dst;
    this.base = base;
    this.offset = offset;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    output.addLine(this, "%s = MEMORY.ref(2, %s + 0x%x).getUnsigned();".formatted(this.dst.fullName(), this.base.fullName(), this.offset));
  }

  @Override
  public String toString() {
    return "%s %s,[%s,0x%x]".formatted(super.toString(), this.dst.name, this.base.name, this.offset);
  }
}
