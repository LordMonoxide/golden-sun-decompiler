package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;
import org.goldensun.disassembler.values.Value;

import java.util.Map;
import java.util.Set;

public class StrSpState extends OpState {
  public final Register src;
  public final int offset;

  public StrSpState(final int address, final OpType opType, final Register src, final int offset) {
    super(address, opType);
    this.src = src;
    this.offset = offset;
  }

  @Override
  public void run(final DisassemblerConfig config, final CpuState state) {
    state.registerUsage.get(this.src).add(RegisterUsage.READ);
    state.stackValues.put(state.stackDepth - this.offset, Value.register(this.src));
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    output.addLine(this, "MEMORY.ref(4, %s + 0x%x).setu(%s);".formatted(Register.R13_SP.fullName(), this.offset, this.src.fullName()));
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(this.src).add(RegisterUsage.READ);
  }

  @Override
  public String toString() {
    return "%s %s,[sp,0x%x]".formatted(super.toString(), this.src.name, this.offset);
  }
}
