package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

public class LsrState extends OpState {
  public final Register dst;
  public final Register src;
  public final int amount;

  public LsrState(final int address, final OpType opType, final Register dst, final Register src, final int amount) {
    super(address, opType);
    this.dst = dst;
    this.src = src;
    this.amount = amount;
  }

  @Override
  public boolean carry() {
    return true;
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
      output.addLine(this, "%1$s = CPU.lsrT(%2$s, %3$d);".formatted(this.dst.fullName(), this.src.fullName(), this.amount));
    } else {
      output.addLine(this, "%1$s = %2$s >>> %3$d;".formatted(this.dst.fullName(), this.src.fullName(), this.amount));
    }
  }

  @Override
  public String toString() {
    return "%s %s,%s,0x%x".formatted(super.toString(), this.dst.name, this.src.name, this.amount);
  }
}
