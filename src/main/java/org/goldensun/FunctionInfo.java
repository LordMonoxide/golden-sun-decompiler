package org.goldensun;

public class FunctionInfo {
  public final int address;
  public final String name;
  public final String returnType;
  public final ParamInfo[] params;

  public FunctionInfo(final int address, final String name, final String returnType, final ParamInfo... params) {
    this.address = address;
    this.name = name;
    this.returnType = returnType;
    this.params = params;
  }
}
