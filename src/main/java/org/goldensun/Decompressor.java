package org.goldensun;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goldensun.memory.Memory;

public final class Decompressor {
  private Decompressor() { }

  private static final Logger LOGGER = LogManager.getFormatterLogger(Decompressor.class);

  /** NOTE: not always at this address (seen at 0x3002000, 0x3006000) */
  public static int decompress(final Memory memory, int src, int dst) {
    int r2;
    int r3;
    int r4;
    int r5;
    int r6;
    int r7;
    final int r9;

    LOGGER.info("Decompressing 0x%x to 0x%x", src, dst);

    r2 = memory.get(src, 1);
    src++;

    if(r2 != 0) {
      if(r2 != 1) {
        return 0x4;
      }

      //LAB_3002254
      r9 = dst;

      boolean skip = false;

      //LAB_3002258
      int lr = 0;
      while(true) {
        do {
          if(!skip) {
            lr = memory.get(src, 1);
            src++;

            final boolean cflag = (lr & 0x1 << 7) != 0;
            lr = 0x1000000 | lr << 25;
            if(cflag) {
              break;
            }
          }

          //LAB_3002268
          boolean cflag;
          do {
            if(!skip) {
              memory.set(dst, 1, memory.get(src, 1));
              src++;
              dst++;
            }

            skip = false;

            //LAB_3002270
            cflag = (lr & 0x1 << 31) != 0;
            lr <<= 1;
          } while(!cflag);
        } while(lr == 0);

        //LAB_300227c
        r3 = memory.get(src, 1);
        src++;
        r4 = memory.get(src, 1);
        src++;
        r2 = r3 & 0xf0;
        r2 = r4 | r2 << 4;
        r3 = r3 & 0xf;
        if(r3 == 0) {
          //LAB_30022b0
          if(r2 == 0) {
            return dst - r9;
          }

          r3 = memory.get(src, 1) + 0x10;
          src++;
        }

        //LAB_3002294
        do {
          memory.set(dst, 1, memory.get(dst - r2, 1));
          dst++;
          r3--;
        } while(r3 != 0);

        memory.set(dst, 1, memory.get(dst - r2, 1));
        dst++;

        // Hacky jump to LAB_3002270;
        skip = true;
      }
    }

    dst--;
    r9 = dst + 0x21;
    r2 = src & 0x1;
    r4 = r2 << 3;
    if(r2 != 0) {
      r2 = memory.get(src, 1);
      src++;
    }
    r4 -= 0x10;
    if(r4 < 0) {
      r3 = memory.get(src, 2);
      src += 0x2;
      r4 += 0x10;
      r2 |= r3 << r4;
    }

    //LAB_3002048
    while(true) {
      boolean flag = (r2 & 0x1) != 0;
      r2 >>>= 1;

      //LAB_3002030
      while(flag) {
        dst++;
        memory.set(dst, 1, r2);
        r2 >>>= 8;
        r4 -= 0x9;
        if(r4 < 0) {
          r3 = memory.get(src, 2);
          src += 0x2;
          r4 += 0x10;
          r2 |= r3 << r4;
        }

        flag = (r2 & 0x1) != 0;
        r2 >>>= 1;
      }

      flag = (r2 & 0x1) != 0;
      r2 >>>= 1;
      if(!flag) {
        //LAB_3002104
        r4 -= 0x2;
        r5 = 0x2;
      } else {
        flag = (r2 & 0x1) != 0;
        r2 >>>= 1;
        if(!flag) {
          //LAB_30020c8
          r4 -= 0x3;
          r5 = 0x3;
        } else {
          flag = (r2 & 0x1) != 0;
          r2 >>>= 1;
          if(!flag) {
            //LAB_30020d4
            r4 -= 0x4;
            r5 = 0x4;
            if(r4 < 0) {
              r3 = memory.get(src, 2);
              src += 0x2;
              r4 += 0x10;
              r2 |= r3 << r4;
            }
          } else {
            flag = (r2 & 0x1) != 0;
            r2 >>>= 1;
            if(!flag) {
              //LAB_30020e4
              r4 -= 0x5;
              r5 = 0x5;
              if(r4 < 0) {
                r3 = memory.get(src, 2);
                src += 0x2;
                r4 += 0x10;
                r2 |= r3 << r4;
              }
            } else {
              flag = (r2 & 0x1) != 0;
              r2 >>>= 1;
              if(!flag) {
                flag = (r2 & 0x1) != 0;
                r2 >>>= 1;
                if(!flag) {
                  //LAB_30020f4
                  r4 -= 0x7;
                  r5 = 0x6;
                } else {
                  r4 -= 0x7;
                  r5 = 0x7;
                }

                if(r4 < 0) {
                  r3 = memory.get(src, 2);
                  src += 0x2;
                  r4 += 0x10;
                  r2 |= r3 << r4;
                }
              } else {
                //LAB_3002090
                r5 = r2 & 0x3;
                r2 >>>= 2;
                if(r5 != 0) {
                  r5 += 0x7;
                  r4 -= 0x8;
                } else {
                  //LAB_30020ac
                  r5 = r2 & 0x7f;
                  if(r5 == 0) {
                    return dst - (r9 - 0x21);
                  }

                  r5 += 0xa;
                  r2 >>>= 7;
                  r4 -= 0xf;
                }

                if(r4 < 0) {
                  r3 = memory.get(src, 2);
                  src += 0x2;
                  r4 += 0x10;
                  r2 |= r3 << r4;
                }
              }
            }
          }
        }
      }

      //LAB_300210c
      flag = (r2 & 0x1) != 0;
      r2 >>>= 1;
      if(!flag) {
        //LAB_3002154
        r6 = dst - r9;
        if(r6 < 0 || r6 >= 0x800) {
          r6 = Integer.rotateRight(r2, 12);
          r2 = r2 >>> 12;
          r6 = r6 >>> 20;
          r6 = r6 + 0x20;
          r4 -= 0xd;
        } else {
          //LAB_3002178
          r7 = 11;
          final boolean cflag = (r6 & 0x1 << 10) != 0;
          r6 = r6 << 22;
          if(!cflag) {
            for(int i = 0; i < 11; i++) {
              r7 = r7 - 0x1;
              if((r6 & 0x1 << 31) != 0) {
                break;
              }
              r6 = r6 << 1;
            }
          }

          //LAB_3002200
          r6 = (r2 & (0x1 << r7) - 1) + 0x20;
          r2 = r2 >>> r7;
          r4 = r4 - 0x1;
          r4 -= r7;
        }
      } else {
        r6 = r2 & 0x1f;
        r4 -= 0x6;
        r2 = r2 >>> 5;
      }

      //LAB_3002120
      if(r4 < 0) {
        r3 = memory.get(src, 2);
        src += 0x2;
        r4 += 0x10;
        r2 |= r3 << r4;
      }
      if((r5 & 0x1) != 0) {
        r7 = memory.get(dst - r6, 1);
        dst++;
        memory.set(dst, 1, r7);
      }
      r5 = r5 >>> 1;

      //LAB_3002138
      do {
        r7 = memory.get(dst - r6, 1);
        dst++;
        memory.set(dst, 1, r7);

        r7 = memory.get(dst - r6, 1);
        dst++;
        memory.set(dst, 1, r7);

        r5--;
      } while(r5 != 0);
    }
  }
}
