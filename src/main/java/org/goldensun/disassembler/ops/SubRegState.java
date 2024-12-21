package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Map;
import java.util.Set;

public class SubRegState extends OpState {
  public final Register dst;
  public final Register src;
  public final Register operand;

  public SubRegState(final int address, final OpType opType, final Register dst, final Register src, final Register operand) {
    super(address, opType);
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
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant, final Set<OpState> dependencies) {
    if(hasDependant) {
      output.addLine(this, "%s = CPU.subT(%s, %s);".formatted(this.dst.fullName(), this.src.fullName(), this.operand.fullName()));
    } else {
      output.addLine(this, "%s = %s - %s;".formatted(this.dst.fullName(), this.src.fullName(), this.operand.fullName()));
    }
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(this.dst).add(RegisterUsage.READ);
    usage.get(this.src).add(RegisterUsage.READ);
  }

  @Override
  public String toString() {
    return "%s %s,%s,%s".formatted(super.toString(), this.dst.name, this.src.name, this.operand.name);
  }
}
