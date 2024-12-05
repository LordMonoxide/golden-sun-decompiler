package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;

import java.util.Map;
import java.util.Set;

public class LsrAluState extends OpState {
  public final Register dst;
  public final Register src;

  public LsrAluState(final int address, final OpType opType, final Register dst, final Register src) {
    super(address, opType);
    this.dst = dst;
    this.src = src;
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
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(this.dst).add(RegisterUsage.WRITE);
    usage.get(this.dst).add(RegisterUsage.READ);
    usage.get(this.src).add(RegisterUsage.READ);
  }

  @Override
  public String toString() {
    return "%s %s,%s".formatted(super.toString(), this.dst.name, this.src.name);
  }
}
