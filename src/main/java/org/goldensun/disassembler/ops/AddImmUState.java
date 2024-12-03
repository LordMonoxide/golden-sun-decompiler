package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

public class AddImmUState extends OpState {
  public final Register dst;
  public final int immediate;

  public AddImmUState(final DisassemblyRange range, final int address, final OpType opType, final Register dst, final int immediate) {
    super(range, address, opType);
    this.dst = dst;
    this.immediate = immediate;
  }

  @Override
  public boolean overflow() {
    return true;
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
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    if(hasDependant) {
      output.addLine(this, "%1$s = CPU.addT(%1$s, 0x%2$x);".formatted(this.dst.fullName(), this.immediate));
    } else {
      output.addLine(this, "%1$s = %1$s + 0x%2$x;".formatted(this.dst.fullName(), this.immediate));
    }
  }

  @Override
  public String toString() {
    return "%s %s,0x%x".formatted(super.toString(), this.dst.name, this.immediate);
  }
}
