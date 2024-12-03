package org.goldensun;

public final class Util {
  private Util() { }

  public static int sign(final int value, final int numberOfBits) {
    if((value & 1 << numberOfBits - 1) != 0) {
      return value | -(1 << numberOfBits);
    }

    return value;
  }

  public static String signedHex(final int value) {
    final String prefix = value < 0 ? "-0x" : "0x";
    return prefix + Integer.toHexString(Math.abs(value));
  }

  public static int get(final byte[] data, final int offset, final int size) {
    int value = 0;

    for(int i = 0; i < size; i++) {
      value |= (data[offset + i] & 0xff) << i * 8;
    }

    return value;
  }
}
