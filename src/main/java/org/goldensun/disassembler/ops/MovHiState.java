package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;
import org.goldensun.disassembler.values.Value;

import java.util.Set;

public class MovHiState extends OpState {
  public final Register dst;
  public final Register src;

  public MovHiState(final DisassemblyRange range, final int address, final OpType opType, final Register dst, final Register src) {
    super(range, address, opType);
    this.dst = dst;
    this.src = src;
  }

  @Override
  public void run(final DisassemblerConfig config, final CpuState state) {
    state.registerUsage.get(this.dst).add(RegisterUsage.WRITE);
    state.registerUsage.get(this.dst).add(RegisterUsage.READ);
    state.registerValues.put(this.dst, Value.register(this.src));
  }

  @Override
  public void getReferents(final DisassemblerConfig config, final Set<Integer> referents) {
    //TODO switch statements
    if(this.dst != Register.R15_PC) {
      super.getReferents(config, referents);
    }
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    final String srcValue;
    if(this.src == Register.R15_PC) {
      srcValue = "0x%07x".formatted(this.range.baseAddr + this.address + 0x4);
    } else {
      srcValue = "%s".formatted(this.src.fullName());
    }

    if(this.dst != Register.R15_PC) {
      output.addLine(this, "%s = %s;".formatted(this.dst.fullName(), srcValue));
    } else if(this.src == Register.R14_LR) {
      output.addLine(this, "return %s;".formatted(Register.R0.fullName()));
    } else {
      output.addLabel(this.address, "//TODO PC SET 0x%x".formatted(this.range.baseAddr + this.address));
      output.addLine(this, "%s = %s;".formatted(this.dst.fullName(), srcValue));
    }
  }

  @Override
  public String toString() {
    return "%s %s,%s".formatted(super.toString(), this.dst.name, this.src.name);
  }
}
