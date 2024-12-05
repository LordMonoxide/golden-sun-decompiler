package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Map;
import java.util.Set;

public class SubImmState extends OpState {
  public final Register dst;
  public final Register src;
  public final int immediate;

  public SubImmState(final int address, final OpType opType, final Register dst, final Register src, final int immediate) {
    super(address, opType);
    this.dst = dst;
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
    if(hasDependant) {
      output.addLine(this, "%s = CPU.subT(%s, 0x%x);".formatted(this.dst.fullName(), this.src.fullName(), this.immediate));
    } else {
      output.addLine(this, "%s = %s - 0x%x;".formatted(this.dst.fullName(), this.src.fullName(), this.immediate));
    }
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(this.dst).add(RegisterUsage.READ);
    usage.get(this.src).add(RegisterUsage.READ);
  }

  @Override
  public String toString() {
    return "%s %s,%s,0x%x".formatted(super.toString(), this.dst.name, this.src.name, this.immediate);
  }
}
