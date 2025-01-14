package org.goldensun.disassembler.ops;

import org.goldensun.FunctionInfo;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BlState extends OpState {
  public final int offset;

  public BlState(final int address, final OpType opType, final int offset) {
    super(address, opType);
    this.offset = offset;
  }

  @Override
  public void getReferents(final DisassemblerConfig config, final Set<Integer> referents) {
    // This bl is treated like a b
    if(config.blAsB.contains(this.address)) {
      referents.add(this.getDest());
      return;
    }

//    if(config.codeContains(this.getDest())) {
      // bl used as local jump treated as a regular jump
//      referents.add(this.getDest());
//    } else {
      // bl to another function will return
      super.getReferents(config, referents);
//    }
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant, final Set<OpState> dependencies) {
    // This bl is treated like a b
    if(config.blAsB.contains(this.address)) {
      output.addLine(this, "LAB_%07x;".formatted(this.getDest()));
      return;
    }

//    if(config.codeContains(this.getDest())) {
//      output.addLabel(this.getDest(), "//LAB_%07x".formatted(this.getDest()));
//      output.addLine(this, "LAB_%07x;".formatted(this.getDest()));
//    } else {
    final FunctionInfo destInfo = config.functions.get(this.getDest());
    final String destName;
    final String returnType;
    final String args;
    if(destInfo != null) {
      destName = destInfo.name;
      returnType = destInfo.returnType;
      args = IntStream.range(0, destInfo.params.length).mapToObj(i -> i < 4 ? "r" + i : "MEMORY.ref(4, CPU.sp().value + 0x%x).get()".formatted((i - 4) * 0x4)).collect(Collectors.joining(", "));
    } else {
      destName = "FUN_%07x".formatted(this.getDest());
      returnType = "";
      args = "";
    }

    output.addLine(this, "%s%s(%s);".formatted("void".equals(returnType) ? "" : Register.R0.fullName() + " = ", destName, args));
//    }
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {
    usage.get(Register.R0).add(RegisterUsage.WRITE);
  }

  public int getDest() {
    return this.address + 0x4 + this.offset;
  }

  @Override
  public String toString() {
    return "%s 0x%x".formatted(super.toString(), this.getDest());
  }
}
