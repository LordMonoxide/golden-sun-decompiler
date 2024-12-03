package org.goldensun.disassembler.ops;

import org.goldensun.disassembler.CpuState;
import org.goldensun.disassembler.DataRange;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.DisassemblyRange;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;
import org.goldensun.disassembler.values.Value;

import static org.goldensun.Util.get;

public class LdrPcState extends OpState {
  public final Register dst;
  public final int offset;

  public LdrPcState(final DisassemblyRange range, final int address, final OpType opType, final Register dst, final int offset) {
    super(range, address, opType);
    this.dst = dst;
    this.offset = offset;
  }

  public int getAddress() {
    return this.address + 0x4 + this.offset * 0x4 & ~0x2;
  }

  @Override
  public void run(final DisassemblerConfig config, final CpuState state) {
    final int address = this.range.baseAddr + this.getAddress();

    state.registerUsage.get(this.dst).add(RegisterUsage.WRITE);

    for(final DataRange range : config.dataRanges) {
      if(address >= range.start && address < range.end + 4) {
        state.registerValues.put(this.dst, Value.constant(get(config.data, address, 4)));
        return;
      }
    }

    state.registerValues.put(this.dst, Value.memory(4, get(config.data, address, 4), false));
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant) {
    if(config.dataContains(this.getAddress())) {
      output.addLine(this, "%s = 0x%x;".formatted(this.dst.fullName(), config.read(this.getAddress(), 4)));
    } else {
      output.addLine(this, "%s = MEMORY.ref(4, 0x%07x).get();".formatted(this.dst.fullName(), this.range.baseAddr + this.getAddress()));
    }
  }

  @Override
  public String toString() {
    return "%s %s,[0x%x]".formatted(super.toString(), this.dst.name, this.getAddress());
  }
}
