package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StmiaState extends OpState {
  public final Register[] registers;
  public final Register base;

  public StmiaState(final int address, final OpType opType, final Register[] registers, final Register base) {
    super(address, opType);
    this.registers = registers;
    this.base = base;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant, final Set<OpState> dependencies) {
    for(final Register register : this.registers) {
      output.addLine(this, "MEMORY.ref(4, %s).setu(%s);".formatted(this.base.fullName(), register.fullName()));
      output.addLine(this, "%s += 0x4;".formatted(this.base.fullName()));
    }
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    for(final Register register : this.registers) {
      usage.get(register).add(RegisterUsage.READ);
    }

    usage.get(this.base).add(RegisterUsage.READ);
  }

  @Override
  public String toString() {
    return "%s %s!,(%s)".formatted(super.toString(), this.base.name, Arrays.stream(this.registers).map(r -> r.name).collect(Collectors.joining(",")));
  }
}
