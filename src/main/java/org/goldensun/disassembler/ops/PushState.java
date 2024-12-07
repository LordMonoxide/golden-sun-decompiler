package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;
import org.goldensun.disassembler.values.Value;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PushState extends OpState {
  public final Register[] registers;

  public PushState(final int address, final OpType opType, final Register[] registers) {
    super(address, opType);
    this.registers = registers;
  }

  @Override
  public void run(final DisassemblerConfig config, final CpuState state) {
    for(final Register register : this.registers) {
      state.registerUsage.get(register).add(RegisterUsage.STACK);
      state.stackValues.put(state.stackDepth, Value.register(register));
      state.stackDepth += 4;
    }
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    for(final Register register : this.registers) {
      output.addLine(this, "CPU.push(%s);".formatted(register.fullName()));
    }
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    for(final Register register : this.registers) {
      usage.get(register).add(RegisterUsage.READ);
    }
  }

  @Override
  public int getStackDepthChange() {
    return this.registers.length * 0x4;
  }

  @Override
  public String toString() {
    return super.toString() + ' ' + Arrays.stream(this.registers).map(r -> r.name).collect(Collectors.joining(","));
  }
}
