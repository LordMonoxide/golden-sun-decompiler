package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Map;
import java.util.Set;

public class StrhImmState extends OpState {
  public final Register src;
  public final Register base;
  public final int immediate;

  public StrhImmState(final int address, final OpType opType, final Register src, final Register base, final int immediate) {
    super(address, opType);
    this.src = src;
    this.base = base;
    this.immediate = immediate;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    output.addLine(this, "MEMORY.ref(2, %s + 0x%x).setu(%s);".formatted(this.base.fullName(), this.immediate, this.src.fullName()));
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(this.src).add(RegisterUsage.READ);
    usage.get(this.base).add(RegisterUsage.READ);
  }

  @Override
  public String toString() {
    return "%s %s,[%s,0x%x]".formatted(super.toString(), this.src.name, this.base.name, this.immediate);
  }
}
