package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

public class AddHiState extends OpState {
  public final Register dst;
  public final Register src;

  public AddHiState(final DisassemblyRange range, final int address, final OpType opType, final Register dst, final Register src) {
    super(range, address, opType);
    this.dst = dst;
    this.src = src;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    if(this.src == Register.R15_PC) {
      output.addLine(this, "%s += %s + 0x4;".formatted(this.dst.fullName(), this.src.fullName()));
      return;
    }

    if(this.dst == Register.R15_PC) {
      throw new RuntimeException("PC add not implemented @ 0x%x".formatted(this.address));
    }

    output.addLine(this, "%1$s = %1$s + %2$s;".formatted(this.dst.fullName(), this.src.fullName()));
  }

  @Override
  public String toString() {
    return "%s %s,%s".formatted(super.toString(), this.dst.name, this.src.name);
  }
}
