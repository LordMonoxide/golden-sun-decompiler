package org.goldensun.disassembler;

public enum OperatorBinary {
  AND("&"),
  EOR("^"),
  LSL("<<"),
  LSR(">>>"),
  ASR(">>"),
  ADC("+c"),
  SBC("-c"),
  ROR("ror"),
  ORR("|"),
  MUL("*"),
  BIC("~&"),
  ;

  public final String operator;

  OperatorBinary(final String operator) {
    this.operator = operator;
  }
}
