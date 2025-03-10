package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;
import org.goldensun.disassembler.values.Value;

import java.util.Map;
import java.util.Set;

public class LdrbImmState extends OpState {
  public final Register dst;
  public final Register base;
  public final int offset;

  public LdrbImmState(final int address, final OpType opType, final Register dst, final Register base, final int offset) {
    super(address, opType);
    this.dst = dst;
    this.base = base;
    this.offset = offset;
  }

  @Override
  public void run(final DisassemblerConfig config, final CpuState state) {
    state.registerUsage.get(this.dst).add(RegisterUsage.WRITE);
    state.registerUsage.get(this.base).add(RegisterUsage.READ);
    state.registerValues.put(this.dst, Value.memory(1, this.base, this.offset, false));
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant, final Set<OpState> dependencies) {
    output.addLine(this, "%s = MEMORY.ref(1, %s + 0x%x).getUnsigned();".formatted(this.dst.fullName(), this.base.fullName(), this.offset));
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(this.dst).add(RegisterUsage.WRITE);
    usage.get(this.base).add(RegisterUsage.READ);
  }

  @Override
  public String toString() {
    return "%s %s,[%s,0x%x]".formatted(super.toString(), this.dst.name, this.base.name, this.offset);
  }
}
