package org.goldensun.disassembler;

public class SwitchConfig {
  public final int address;
  public final int entryCount;

  public SwitchConfig(final int address, final int entryCount) {
    this.address = address;
    this.entryCount = entryCount;
  }
}
