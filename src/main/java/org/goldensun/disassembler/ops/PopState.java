package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PopState extends OpState {
  public final Register[] registers;

  public PopState(final int address, final OpType opType, final Register[] registers) {
    super(address, opType);
    this.registers = registers;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    for(final Register r : this.registers) {
      output.addLine(this, "%s = CPU.pop();".formatted(r.fullName()));
    }
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    for(final Register register : this.registers) {
      usage.get(register).add(RegisterUsage.WRITE);
    }
  }

  @Override
  public int getStackDepthChange() {
    return -this.registers.length * 0x4;
  }

  @Override
  public String toString() {
    return super.toString() + ' ' + Arrays.stream(this.registers).map(r -> r.name).collect(Collectors.joining(","));
  }
}
