package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Map;
import java.util.Set;

public class CmpHiState extends OpState {
  public final Register a;
  public final Register b;

  public CmpHiState(final int address, final OpType opType, final Register a, final Register b) {
    super(address, opType);
    this.a = a;
    this.b = b;
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
    if(!hasDependant) {
      output.addLabel(this.address, "//TODO no dependant found for cmp");
    }

    output.addLine(this, "CPU.cmpT(%s, %s);".formatted(this.a.fullName(), this.b.fullName()));
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(this.a).add(RegisterUsage.READ);
    usage.get(this.b).add(RegisterUsage.READ);
  }

  @Override
  public String toString() {
    return "%s %s,%s".formatted(super.toString(), this.a.name, this.b.name);
  }
}
