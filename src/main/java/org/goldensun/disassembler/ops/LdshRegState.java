package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Map;
import java.util.Set;

public class LdshRegState extends OpState {
  public final Register dst;
  public final Register base;
  public final Register offset;

  public LdshRegState(final int address, final OpType opType, final Register dst, final Register base, final Register offset) {
    super(address, opType);
    this.dst = dst;
    this.base = base;
    this.offset = offset;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant, final Set<OpState> dependencies) {
    output.addLine(this, "%s = MEMORY.ref(2, %s + %s).get();".formatted(this.dst.fullName(), this.base.fullName(), this.offset.fullName()));
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(this.dst).add(RegisterUsage.WRITE);
    usage.get(this.base).add(RegisterUsage.READ);
    usage.get(this.offset).add(RegisterUsage.READ);
  }

  @Override
  public String toString() {
    return "%s %s,[%s,%s]".formatted(super.toString(), this.dst.name, this.base.name, this.offset.name);
  }
}
