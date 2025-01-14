package org.goldensun.disassembler.ops;

import org.goldensun.Util;
import org.goldensun.disassembler.DisassemblerConfig;
import org.goldensun.disassembler.Register;
import org.goldensun.disassembler.RegisterUsage;
import org.goldensun.disassembler.TranslatorOutput;

import java.util.Map;
import java.util.Set;

public class ConditionalBranchState extends OpState {
  private final String condition;
  private final String conditionFormat;
  private final String hint;
  public final int offset;

  public ConditionalBranchState(final int address, final OpType opType, final String condition, final String conditionFormat, final String hint, final int offset) {
    super(address, opType);
    this.condition = condition;
    this.conditionFormat = conditionFormat;
    this.hint = hint;
    this.offset = offset;
  }

  @Override
  public void getReferents(final DisassemblerConfig config, final Set<Integer> referents) {
    super.getReferents(config, referents);

//    if(config.codeContains(this.getDest())) {
      referents.add(this.getDest());
//    }
  }

  @Override
  public void translate(final DisassemblerConfig config, final TranslatorOutput output, final boolean hasDependant, final Set<OpState> dependencies) {
    if(dependencies.size() == 1) {
      for(final OpState op : dependencies) {
        if(this.conditionFormat.isEmpty()) {
          throw new RuntimeException(this + " condition translation not implemented");
        }

        if(op instanceof final CmpState cmp) {
          output.addLine(this, ("if(" + this.conditionFormat + ") {").formatted(cmp.src.fullName(), Util.signedHex(cmp.immediate)));
        } else if(op instanceof final CmpAluState cmp) {
          output.addLine(this, ("if(" + this.conditionFormat + ") {").formatted(cmp.a.fullName(), cmp.b.fullName()));
        } else if(op instanceof final CmpHiState cmp) {
          output.addLine(this, ("if(" + this.conditionFormat + ") {").formatted(cmp.a.fullName(), cmp.b.fullName()));
        } else if(op instanceof final LsrState lsr) {
          output.addLine(this, ("if(" + this.conditionFormat + ") {").formatted(lsr.dst.fullName(), 0));
        } else if(op instanceof final CmnAluState cmn) {
          output.addLine(this, ("if(" + this.conditionFormat + ") {").formatted("%s + %s".formatted(cmn.a.fullName(), cmn.b.fullName()), 0));
        } else {
          throw new RuntimeException(op + " condition translation not implemented");
        }
      }
    } else {
      output.addLine(this, "if(" + this.replaceConditions() + ") { // " + this.hint);
    }

//    if(config.codeContains(this.getDest())) {
      output.addLabel(this.getDest(), "//LAB_%07x".formatted(this.getDest()));
      output.addLine(this, "  LAB_%07x;".formatted(this.getDest()));
//    } else {
//      output.addLabel(this.address, "//TODO branch");
//      output.addLine(this, "  %s = FUN_%07x();".formatted(Register.R0.fullName(), this.getDest()));
//    }

    output.addLine(this, "}");
  }

  private String replaceConditions() {
    return this.condition
      .replace("C", "CPU.cpsr().getCarry()")
      .replace("V", "CPU.cpsr().getOverflow()")
      .replace("Z", "CPU.cpsr().getZero()")
      .replace("N", "CPU.cpsr().getNegative()")
      ;
  }

  @Override
  public void getRegisterUsage(final Map<Register, Set<RegisterUsage>> usage) {

  }

  public int getDest() {
    return this.address + 0x4 + this.offset;
  }

  @Override
  public String toString() {
    return "%s 0x%x".formatted(super.toString(), this.getDest());
  }
}
