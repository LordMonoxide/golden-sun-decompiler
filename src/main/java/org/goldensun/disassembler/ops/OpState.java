package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DisassemblerConfig;

import java.util.Set;

public class OpState {
  public final int address;
  public final OpType opType;

  public OpState(final int address, final OpType opType) {
    this.address = address;
    this.opType = opType;
  }

  public void run(final DisassemblerConfig config, final CpuState state) {
    throw new RuntimeException("0x%x: %s run not implemented".formatted(this.address, this.opType.name));
  }

  public void getReferents(final DisassemblerConfig config, final Set<Integer> referents) {
    referents.add(this.address + this.opType.getSize());
  }

  public boolean overflow() {
    return false;
  }

  public boolean carry() {
    return false;
  }

  public boolean zero() {
    return false;
  }

  public boolean negative() {
    return false;
  }

  @Override
  public String toString() {
    return "0x%x %s".formatted(this.address, this.opType);
  }
}