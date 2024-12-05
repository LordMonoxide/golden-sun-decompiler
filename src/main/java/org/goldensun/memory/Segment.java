package org.goldensun.memory;

import org.goldensun.Util;

public class Segment {
  private final int address;
  private final int length;
  private final int mask;
  private final byte[] data;

  public Segment(final int address, final int length) {
    this(address, length, 0xffff_ffff);
  }

  public Segment(final int address, final int length, final int mask) {
    this.address = address;
    this.length = length;
    this.mask = mask;
    this.data = new byte[length];
  }

  public int getAddress() {
    return this.address;
  }

  public int getLength() {
    return this.length;
  }

  public boolean accepts(final int address) {
    return address >= this.address && address < this.address + this.length;
  }

  public int get(final int offset, final int size) {
    return Util.get(this.data, offset & this.mask, size);
  }

  public void set(int offset, final int size, final int value) {
    offset &= this.mask;
    Util.set(this.data, offset, size, value);
  }

  public void setBytes(int offset, final byte[] data, int dataOffset, final int dataLength) {
    offset &= this.mask;
    dataOffset &= this.mask;
    System.arraycopy(data, dataOffset, this.data, offset, dataLength);
  }
}
