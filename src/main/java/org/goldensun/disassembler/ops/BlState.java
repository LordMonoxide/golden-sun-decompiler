package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Set;

public class BlState extends OpState {
  public final int offset;

  public BlState(final DisassemblyRange range, final int address, final OpType opType, final int offset) {
    super(range, address, opType);
    this.offset = offset;
  }

  @Override
  public void getReferents(final DisassemblerConfig config, final Set<Integer> referents) {
    if(config.codeContains(this.getDest())) {
      // bl used as local jump treated as a regular jump
      referents.add(this.getDest());
    } else {
      // bl to another function will return
      super.getReferents(config, referents);
    }
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    if(config.codeContains(this.getDest())) {
      output.addLabel(this.getDest(), "//LAB_%07x".formatted(this.range.baseAddr + this.getDest()));
      output.addLine(this, "LAB_%07x;".formatted(this.range.baseAddr + this.getDest()));
    } else {
      output.addLine(this, "%s = FUN_%07x();".formatted(Register.R0.fullName(), this.range.baseAddr + this.getDest()));
    }
  }

  public int getDest() {
    return this.address + 0x4 + this.offset;
  }

  @Override
  public String toString() {
    return "%s 0x%x".formatted(super.toString(), this.getDest());
  }
}
