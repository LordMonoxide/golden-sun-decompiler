package org.goldensun.disassembler.ops;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goldensun.Util;
import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.SwitchConfig;
import org.goldensun.disassembler.TranslatorOutput;
import org.goldensun.disassembler.values.Value;

import java.util.Map;
import java.util.Set;

public class MovHiState extends OpState {
  private static final Logger LOGGER = LogManager.getFormatterLogger(MovHiState.class);

  public final Register dst;
  public final Register src;

  public MovHiState(final int address, final OpType opType, final Register dst, final Register src) {
    super(address, opType);
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
    if(this.dst == Register.R15_PC) {
      for(final SwitchConfig switchConfig : config.switches) {
        if(Util.roundUp(this.address + 0x2, 0x4) == switchConfig.address) {
          LOGGER.info("Binding %s to switch 0x%x", this, switchConfig.address);

          for(int i = 0; i < switchConfig.entryCount; i++) {
            referents.add(config.memory.get(switchConfig.address + i * 0x4, 4));
          }
        }
      }

      return;
    }

    super.getReferents(config, referents);
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant, final Set<OpState> dependencies) {
    final String srcValue;
    if(this.src == Register.R15_PC) {
      srcValue = "0x%07x".formatted(this.address + 0x4);
    } else {
      srcValue = "%s".formatted(this.src.fullName());
    }

    if(this.dst != Register.R15_PC) {
      output.addLine(this, "%s = %s;".formatted(this.dst.fullName(), srcValue));
    } else if(this.src == Register.R14_LR) {
      output.addLine(this, "return %s;".formatted(Register.R0.fullName()));
    } else {
      output.addLabel(this.address, "//TODO PC SET 0x%x".formatted(this.address));
      output.addLine(this, "%s = %s;".formatted(this.dst.fullName(), srcValue));

      for(final SwitchConfig switchConfig : config.switches) {
        if(Util.roundUp(this.address + 0x2, 0x4) == switchConfig.address) {
          for(int i = 0; i < switchConfig.entryCount; i++) {
            final int destAddr = config.memory.get(switchConfig.address + i * 0x4, 4);
            output.addLabel(destAddr, "//case %d: // switch %07x".formatted(i, switchConfig.address));
            output.addLabel(destAddr, "//LAB_%07x".formatted(destAddr));
          }
        }
      }
    }
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(this.dst).add(RegisterUsage.WRITE);
    usage.get(this.src).add(RegisterUsage.READ);
  }

  @Override
  public String toString() {
    return "%s %s,%s".formatted(super.toString(), this.dst.name, this.src.name);
  }
}
