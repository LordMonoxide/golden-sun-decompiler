package org.goldensun.disassembler;

import java.util.List;

public enum Register {
  R0("r0"),
  R1("r1"),
  R2("r2"),
  R3("r3"),
  R4("r4"),
  R5("r5"),
  R6("r6"),
  R7("r7"),
  R8("r8"),
  R9("r9"),
  R10("r10"),
  R11("r11"),
  R12("r12"),
  R13_SP("sp"),
  R14_LR("lr"),
  R15_PC("pc"),
  CPSR("cpsr"),
  SPSR("spsr"),
  ;

  public final String name;

  Register(final String name) {
    this.name = name;
  }

  public String fullName() {
    if(this.ordinal() < R8.ordinal()) {
      return this.name;
    }

    return "CPU." + this.name + "().value";
  }

  public static void unpack(final List<Register> registers, final int packed) {
    for(final Register r : values()) {
      if((packed & 0x1 << r.ordinal()) != 0) {
        registers.add(r);
      }
    }
  }
}
