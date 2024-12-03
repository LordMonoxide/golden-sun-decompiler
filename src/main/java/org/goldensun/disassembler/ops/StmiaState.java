package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StmiaState extends OpState {
  public final Register[] registers;
  public final Register base;

  public StmiaState(final DisassemblyRange range, final int address, final OpType opType, final Register[] registers, final Register base) {
    super(range, address, opType);
    this.registers = registers;
    this.base = base;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    for(final Register register : this.registers) {
      output.addLine(this, "MEMORY.ref(4, %s).setu(%s);".formatted(this.base.fullName(), register.fullName()));
      output.addLine(this, "%s += 0x4;".formatted(this.base.fullName()));
    }
  }

  @Override
  public String toString() {
    return "%s %s!,(%s)".formatted(super.toString(), this.base.name, Arrays.stream(this.registers).map(r -> r.name).collect(Collectors.joining(",")));
  }
}
