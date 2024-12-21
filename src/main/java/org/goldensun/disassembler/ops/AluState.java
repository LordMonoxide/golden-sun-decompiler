package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.Register;

@FunctionalInterface
public interface AluState {
  OpState parse(final int address, final OpType opType, final Register dst, final Register src);
}
