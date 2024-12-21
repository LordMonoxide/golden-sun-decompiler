package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Map;
import java.util.Set;

public class SwiState extends OpState {
  public final int index;

  public SwiState(final int address, final OpType opType, final int index) {
    super(address, opType);
    this.index = index;
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant, final Set<OpState> dependencies) {
    final String name = switch(this.index) {
      case 0x00 -> "SoftReset()";
      case 0x01 -> "RegisterRamReset(r0)";
      case 0x02 -> "Halt()";
      case 0x03 -> "Stop()";
      case 0x04 -> "IntrWait(r0, r1)";
      case 0x05 -> "VblankIntrWait()";
      case 0x06 -> "r0 = Div(r0, r1)";
      case 0x07 -> "r0 = DivArm(r0, r1)";
      case 0x08 -> "r0 = Sqrt(r0)";
      case 0x09 -> "r0 = ArcTan(r0)";
      case 0x0a -> "r0 = ArcTan2(r0, r1)";
      case 0x0b -> "CpuSet(r0, r1, r2)";
      case 0x0c -> "CpuFastSet(r0, r1, r2)";
      case 0x0d -> "r0 = GetBiosChecksum()";
      case 0x0e -> "r0 = BgAffineSet(r0, r1, r2)";
      case 0x0f -> "r0 = ObjAffineSet(r0, r1, r2, r3)";
      case 0x10 -> "BitUnpack(r0, r1, r2)";
      case 0x11 -> "LZ77UncompressReadNormalWrite8bit(r0, r1)";
      case 0x12 -> "LZ77UncompressReadNormalWrite16bit(r0, r1)";
      case 0x13 -> "HuffUncompressReadNormal(r0, r1)";
      case 0x14 -> "RlUncompressReadNormalWrite8bit(r0, r1)";
      case 0x15 -> "RlUncompressReadNormalWrite16bit(r0, r1)";
      case 0x16 -> "Diff8bitUnfilterWrite8bit(r0, r1)";
      case 0x17 -> "Diff8bitUnfilterWrite16bit(r0, r1)";
      case 0x18 -> "Diff16bitUnfilter(r0, r1)";
      case 0x19 -> "SoundBias(r0, r1)";
      case 0x1a -> "SoundDriverInit(r0)";
      case 0x1b -> "SoundDriverMode(r0)";
      case 0x1c -> "SoundDriverMain(r0)";
      case 0x1d -> "SoundDriverVsync()";
      case 0x1e -> "SoundChannelClear()";
      case 0x1f -> "r0 = MidiKey2Freq(r0, r1, r2)";
      default -> throw new IllegalStateException("Unknown SWI " + this.index);
    };

    output.addLine(this, "%s;".formatted(name));
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
  }

  @Override
  public String toString() {
    return "%s %s".formatted(super.toString(), this.index);
  }
}
