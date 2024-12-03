package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

public class AddRegState extends OpState {
  public final Register dst;
  public final Register src;
  public final Register operand;

  public AddRegState(final DisassemblyRange range, final int address, final OpType opType, final Register dst, final Register src, final Register operand) {
    super(range, address, opType);
    this.dst = dst;
    this.src = src;
    this.operand = operand;
  }

  @Override
  public boolean overflow() {
    return true;
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
      output.addLine(this, "%s = CPU.addT(%s, %s);".formatted(this.dst.fullName(), this.src.fullName(), this.operand.fullName()));
    } else {
      output.addLine(this, "%s = %s + %s;".formatted(this.dst.fullName(), this.src.fullName(), this.operand.fullName()));
    }
  }

  @Override
  public String toString() {
    return "%s %s,%s,%s".formatted(super.toString(), this.dst.name, this.src.name, this.operand.name);
  }
}
