package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.OperatorBinary;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;
import org.goldensun.disassembler.values.Value;

public class LslState extends OpState {
  public final Register dst;
  public final Register src;
  public final int amount;

  public LslState(final int address, final OpType opType, final Register dst, final Register src, final int amount) {
    super(address, opType);
    this.dst = dst;
    this.src = src;
    this.amount = amount;
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
  public void run(final DisassemblerConfig config, final CpuState state) {
    state.registerUsage.get(this.dst).add(RegisterUsage.WRITE);
    state.registerUsage.get(this.src).add(RegisterUsage.READ);
    state.registerValues.put(this.dst, Value.aluBinary(this.src, this.amount, OperatorBinary.LSL));
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    if(hasDependant) {
      output.addLine(this, "%1$s = CPU.lslT(%2$s, %3$d);".formatted(this.dst.fullName(), this.src.fullName(), this.amount));
    } else {
      output.addLine(this, "%1$s = %2$s << %3$d;".formatted(this.dst.fullName(), this.src.fullName(), this.amount));
    }
  }

  @Override
  public String toString() {
    return "%s %s,%s,0x%x".formatted(super.toString(), this.dst.name, this.src.name, this.amount);
  }
}
