package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PopState extends OpState {
  public final Register[] registers;

  public PopState(final int address, final OpType opType, final Register[] registers) {
    super(address, opType);
    this.registers = registers;
  }

  @Override
  public String toString() {
    return super.toString() + ' ' + Arrays.stream(this.registers).map(r -> r.name).collect(Collectors.joining(","));
  }
}
