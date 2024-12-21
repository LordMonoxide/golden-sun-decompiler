package org.goldensun.memory;

import java.util.ArrayList;
import java.util.List;

public class Memory {
  private final List<Segment> segments = new ArrayList<>();

  public <T extends Segment> T addSegment(final T segment) {
    this.segments.add(segment);
    return segment;
  }

  private Segment getSegment(final int address) {
    for(final Segment segment : this.segments) {
      if(segment.accepts(address)) {
        return segment;
      }
    }

    throw new IllegalAddressException("There is no memory segment at " + Integer.toHexString(address) + ')');
  }

  public int get(final int address, final int size) {
//    this.checkAlignment(address, size);

    final Segment segment = this.getSegment(address);
    return segment.get(address - segment.getAddress(), size);
  }

  public void set(final int address, final int size, final int data) {
//    this.checkAlignment(address, size);

    final Segment segment = this.getSegment(address);
    final int addr = address - segment.getAddress();
    segment.set(addr, size, data);
  }

  public byte[] getBytes(final int address, final int size) {
    final Segment segment = this.getSegment(address);
    return segment.getBytes(address - segment.getAddress(), size);
  }

  public void getBytes(final int address, final byte[] dest, final int offset, final int size) {
    final Segment segment = this.getSegment(address);
    segment.getBytes(address - segment.getAddress(), dest, offset, size);
  }

  public void setBytes(final int address, final byte[] data) {
    this.setBytes(address, data, 0, data.length);
  }

  public void setBytes(final int address, final byte[] data, final int offset, final int size) {
    final Segment segment = this.getSegment(address);
    segment.setBytes(address - segment.getAddress(), data, offset, size);
  }

  private void checkAlignment(final int address, final int size) {
    if((address & size - 1) != 0) {
      throw new MisalignedAccessException("Misaligned memory access at address " + Integer.toHexString(address) + " for size " + size);
    }
  }
}
