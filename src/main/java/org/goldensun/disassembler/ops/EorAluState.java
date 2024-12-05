package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

public class EorAluState extends OpState {
  public final Register dst;
  public final Register src;

  public EorAluState(final int address, final OpType opType, final Register dst, final Register src) {
    super(address, opType);
    this.dst = dst;
    this.src = src;
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
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    if(hasDependant) {
      output.addLine(this, "%1$s = CPU.eorT(%1$s, %2$s);".formatted(this.dst.fullName(), this.src.fullName()));
    } else {
      output.addLine(this, "%1$s = %1$s ^ %2$s;".formatted(this.dst.fullName(), this.src.fullName()));
    }
  }

  @Override
  public String toString() {
    return "%s %s,%s".formatted(super.toString(), this.dst.name, this.src.name);
  }
}
