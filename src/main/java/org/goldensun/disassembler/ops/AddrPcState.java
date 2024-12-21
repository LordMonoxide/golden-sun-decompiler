package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Map;
import java.util.Set;

public class AddrPcState extends OpState {
  public final Register dst;
  public final int offset;

  public AddrPcState(final int address, final OpType opType, final Register dst, final int offset) {
    super(address, opType);
    this.dst = dst;
    this.offset = offset;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant, final Set<OpState> dependencies) {
    output.addLine(this, "%s = 0x%x;".formatted(this.dst.fullName(), (this.address + 0x4 & ~0x2) + this.offset));
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(this.dst).add(RegisterUsage.WRITE);
  }

  @Override
  public String toString() {
    return "%s %s,[pc,0x%x]".formatted(super.toString(), this.dst.name, this.offset);
  }
}
