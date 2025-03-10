package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Map;
import java.util.Set;

public class AddSpState extends OpState {
  public final int amount;

  public AddSpState(final int address, final OpType opType, final int amount) {
    super(address, opType);
    this.amount = amount;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant, final Set<OpState> dependencies) {
    output.addLine(this, "%s += 0x%x;".formatted(Register.R13_SP.fullName(), this.amount));
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {

  }

  @Override
  public int getStackDepthChange() {
    return -this.amount;
  }

  @Override
  public String toString() {
    return "%s sp,0x%x".formatted(super.toString(), this.amount);
  }
}
