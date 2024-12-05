package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Map;
import java.util.Set;

public class CmpState extends OpState {
  public final Register src;
  public final int immediate;

  public CmpState(final int address, final OpType opType, final Register src, final int immediate) {
    super(address, opType);
    this.src = src;
    this.immediate = immediate;
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

    output.addLine(this, "CPU.cmpT(%1$s, 0x%2$x);".formatted(this.src.fullName(), this.immediate));
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(this.src).add(RegisterUsage.READ);
  }

  @Override
  public String toString() {
    return "%s %s,0x%x".formatted(super.toString(), this.src.name, this.immediate);
  }
}
