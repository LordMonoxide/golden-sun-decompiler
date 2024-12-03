package org.goldensun.disassembler;

public enum OperatorUnary {
  NEG("-"),
  MVN("~"),
  ;

  public final String operator;

  OperatorUnary(final String operator) {
    this.operator = operator;
  }
}
