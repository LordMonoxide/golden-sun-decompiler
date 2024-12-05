package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Map;
import java.util.Set;

public class StrhRegState extends OpState {
  public final Register src;
  public final Register base;
  public final Register offset;

  public StrhRegState(final int address, final OpType opType, final Register src, final Register base, final Register offset) {
    super(address, opType);
    this.src = src;
    this.base = base;
    this.offset = offset;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    output.addLine(this, "MEMORY.ref(2, %s + %s).setu(%s);".formatted(this.base.fullName(), this.offset.fullName(), this.src.fullName()));
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(this.src).add(RegisterUsage.READ);
    usage.get(this.base).add(RegisterUsage.READ);
    usage.get(this.offset).add(RegisterUsage.READ);
  }

  @Override
  public String toString() {
    return "%s %s,[%s,%s]".formatted(super.toString(), this.src.name, this.base.name, this.offset.name);
  }
}
