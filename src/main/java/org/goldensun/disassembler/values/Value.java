package org.goldensun.disassembler.values;

import org.goldensun.disassembler.OperatorBinary;
import org.goldensun.disassembler.OperatorUnary;
import org.goldensun.disassembler.Register;

public class Value {
  private Value() { }

  /** Register has a constant value */
  public static Value constant(final int value) {
    return new ConstantValue(value);
  }

  /** Register is set to the value of another register */
  public static Value register(final Register register) {
    return new RegisterValue(register);
  }

  /** Register is set to the value at a memory address */
  public static Value memory(final int size, final int address, final boolean signed) {
    return new MemoryAbsoluteValue(size, address, signed);
  }

  /** Register is set to the value at a memory address */
  public static Value memory(final int size, final Register base, final int offset, final boolean signed) {
    return new MemoryRelativeValue(size, base, offset, signed);
  }

  /** Register is set to a memory address */
  public static Value address(final int address) {
    return new AddressAbsoluteValue(address);
  }

  /** Register is set to a memory address */
  public static Value address(final Register base, final int offset) {
    return new AddressRelativeValue(base, offset);
  }

  public static Value aluUnary(final Register src, final OperatorUnary operator) {
    return new AluUnaryValue(src, operator);
  }

  public static Value aluBinary(final Register src, final int amount, final OperatorBinary operator) {
    return new AluBinaryValue(src, amount, operator);
  }

  public static class ConstantValue extends Value {
    public final int value;

    private ConstantValue(final int value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "IMM[0x%x]".formatted(this.value);
    }
  }

  public static class RegisterValue extends Value {
    public final Register register;

    private RegisterValue(final Register register) {
      this.register = register;
    }

    @Override
    public String toString() {
      return "COPY[%s]".formatted(this.register.name);
    }
  }

  public static class MemoryAbsoluteValue extends Value {
    public final int size;
    public final int address;
    public final boolean signed;

    private MemoryAbsoluteValue(final int size, final int address, final boolean signed) {
      this.size = size;
      this.address = address;
      this.signed = signed;
    }

    @Override
    public String toString() {
      return "MEM[%d, 0x%x]".formatted(this.size, this.address) + (this.signed ? "s" : "");
    }
  }

  public static class MemoryRelativeValue extends Value {
    public final int size;
    public final Register base;
    public final int offset;
    public final boolean signed;

    private MemoryRelativeValue(final int size, final Register base, final int offset, final boolean signed) {
      this.size = size;
      this.base = base;
      this.offset = offset;
      this.signed = signed;
    }

    @Override
    public String toString() {
      return "MEM[%d, %s + 0x%x]".formatted(this.size, this.base.name, this.offset) + (this.signed ? "s" : "");
    }
  }

  public static class AddressAbsoluteValue extends Value {
    public final int address;

    private AddressAbsoluteValue(final int address) {
      this.address = address;
    }

    @Override
    public String toString() {
      return "PTR[0x%x]".formatted(this.address);
    }
  }

  public static class AddressRelativeValue extends Value {
    public final Register base;

    public final int offset;

    private AddressRelativeValue(final Register base, final int offset) {
      this.base = base;
      this.offset = offset;
    }

    @Override
    public String toString() {
      return "PTR[%s + 0x%x]".formatted(this.base.name, this.offset);
    }
  }

  public static class AluUnaryValue extends Value {
    public final Register src;
    public final OperatorUnary operator;

    public AluUnaryValue(final Register src, final OperatorUnary operator) {
      this.src = src;
      this.operator = operator;
    }

    @Override
    public String toString() {
      return "ALU[%s%s]".formatted(this.operator.operator, this.src.name);
    }
  }

  public static class AluBinaryValue extends Value {
    public final Register src;
    public final int amount;
    public final OperatorBinary operator;

    public AluBinaryValue(final Register src, final int amount, final OperatorBinary operator) {
      this.src = src;
      this.amount = amount;
      this.operator = operator;
    }

    @Override
    public String toString() {
      return "ALU[%s %s 0x%x]".formatted(this.src.name, this.operator.operator, this.amount);
    }
  }
}
