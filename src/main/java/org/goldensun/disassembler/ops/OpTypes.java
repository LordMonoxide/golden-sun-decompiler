package org.goldensun.disassembler.ops;

import org.goldensun.memory.Memory;

public final class OpTypes {
  private OpTypes() { }

  // THUMB1 (shifted register)
  public static final OpType LSL = new Lsl();
  public static final OpType LSR = new Lsr();
  public static final OpType ASR = new Asr();
  private static final OpType[] THUMB1 = {LSL, LSR, ASR};

  // THUMB2 (add/sub)
  public static final OpType ADD_REG = new AddReg();
  public static final OpType SUB_REG = new SubReg();
  public static final OpType ADD_IMM = new AddImm();
  public static final OpType SUB_IMM = new SubImm();
  private static final OpType[] THUMB2 = {ADD_REG, SUB_REG, ADD_IMM, SUB_IMM};

  // THUMB3 (mov/cmp/add/sub unary)
  public static final OpType MOV = new Mov();
  public static final OpType CMP = new Cmp();
  public static final OpType ADD_IMM_U = new AddImmU();
  public static final OpType SUB_IMM_U = new SubImmU();
  private static final OpType[] THUMB3 = {MOV, CMP, ADD_IMM_U, SUB_IMM_U};

  // THUMB4 (ALU)
  public static final OpType AND_ALU = new Alu("AND_ALU", AndAluState::new);
  public static final OpType EOR_ALU = new Alu("EOR_ALU", EorAluState::new);
  public static final OpType LSL_ALU = new Alu("LSL_ALU", LslAluState::new);
  public static final OpType LSR_ALU = new Alu("LSR_ALU", LsrAluState::new);
  public static final OpType ASR_ALU = new Alu("ASR_ALU", AsrAluState::new);
  public static final OpType ADC_ALU = new OpType("ADC_ALU");
  public static final OpType SBC_ALU = new OpType("SBC_ALU");
  public static final OpType ROR_ALU = new Alu("ROR_ALU", RorAluState::new);
  public static final OpType TST_ALU = new OpType("TST_ALU");
  public static final OpType NEG_ALU = new Alu("NEG_ALU", NegAluState::new);
  public static final OpType CMP_ALU = new CmpAlu();
  public static final OpType CMN_ALU = new OpType("CMN_ALU");
  public static final OpType ORR_ALU = new Alu("ORR_ALU", OrrAluState::new);
  public static final OpType MUL_ALU = new Alu("MUL_ALU", MulAluState::new);
  public static final OpType BIC_ALU = new OpType("BIC_ALU");
  public static final OpType MVN_ALU = new Alu("MVN_ALU", MvnAluState::new);
  private static final OpType[] THUMB4 = {AND_ALU, EOR_ALU, LSL_ALU, LSR_ALU, ASR_ALU, ADC_ALU, SBC_ALU, ROR_ALU, TST_ALU, NEG_ALU, CMP_ALU, CMN_ALU, ORR_ALU, MUL_ALU, BIC_ALU, MVN_ALU};

  // THUMB5 (hi register add/cmp/mov/bx/blx)
  public static final OpType ADD_HI = new AddHi();
  public static final OpType CMP_HI = new CmpHi();
  public static final OpType MOV_HI = new MovHi();
  public static final OpType BX = new Bx();
  private static final OpType[] THUMB5 = {ADD_HI, CMP_HI, MOV_HI, BX};

  // THUMB6 (load PC-relative)
  public static final OpType LDRPC = new LdrPc();

  // THUMB7 (load/store with register offset)
  public static final OpType STR_REG = new StrReg();
  public static final OpType STRB_REG = new StrbReg();
  public static final OpType LDR_REG = new LdrReg();
  public static final OpType LDRB_REG = new LdrbReg();
  private static final OpType[] THUMB7 = {STR_REG, STRB_REG, LDR_REG, LDRB_REG};

  // THUMB8 (load/store with register offset byte/short)
  public static final OpType STRH_REG = new StrhReg();
  public static final OpType LDSB_REG = new LdsbReg();
  public static final OpType LDRH_REG = new LdrhReg();
  public static final OpType LDSH_REG = new LdshReg();
  private static final OpType[] THUMB8 = {STRH_REG, LDSB_REG, LDRH_REG, LDSH_REG};

  // THUMB9 (load/store with immediate offset)
  public static final OpType STR_IMM = new StrImm();
  public static final OpType LDR_IMM = new LdrImm();
  public static final OpType STRB_IMM = new StrbImm();
  public static final OpType LDRB_IMM = new LdrbImm();
  private static final OpType[] THUMB9 = {STR_IMM, LDR_IMM, STRB_IMM, LDRB_IMM};

  // THUMB10 (load/store with immediate offset short)
  public static final OpType STRH_IMM = new StrhImm();
  public static final OpType LDRH_IMM = new LdrhImm();
  private static final OpType[] THUMB10 = {STRH_IMM, LDRH_IMM};

  // THUMB11 (load/store SP-relative)
  public static final OpType STR_SP = new StrSp();
  public static final OpType LDR_SP = new LdrSp();
  private static final OpType[] THUMB11 = {STR_SP, LDR_SP};

  // THUMB12 (relative address)
  public static final OpType ADDR_PC = new AddrPc();
  public static final OpType ADDR_SP = new AddrSp();
  private static final OpType[] THUMB12 = {ADDR_PC, ADDR_SP};

  // THUMB13 (add offset to stack pointer)
  public static final OpType ADD_SP = new AddSp();
  public static final OpType SUB_SP = new SubSp();
  private static final OpType[] THUMB13 = {ADD_SP, SUB_SP};

  // THUMB14 (push/pop)
  public static final OpType PUSH = new Push();
  public static final OpType POP = new Pop();
  private static final OpType[] THUMB14 = {PUSH, POP};

  // THUMB15 (multiple load/store)
  public static final OpType STMIA = new Stmia();
  public static final OpType LDMIA = new Ldmia();
  private static final OpType[] THUMB15 = {STMIA, LDMIA};

  // THUMB16 (conditional branch)
  public static final OpType BEQ = new ConditionalBranch("BEQ", false, false, true, false, "Z", "%s == %s", "==");
  public static final OpType BNE = new ConditionalBranch("BNE", false, false, true, false, "!Z", "%s != %s", "!=");
  public static final OpType BCS = new ConditionalBranch("BCS", false, true, false, false, "C", "(%s & 0xffff_ffffL) >= (%s & 0xffff_ffffL)", "unsigned >=");
  public static final OpType BCC = new ConditionalBranch("BCC", false, true, false, false, "!C", "(%s & 0xffff_ffffL) < (%s & 0xffff_ffffL)", "unsigned <");
  public static final OpType BMI = new ConditionalBranch("BMI", false, false, false, true, "N", "%s < 0", "negative");
  public static final OpType BPL = new ConditionalBranch("BPL", false, false, false, true, "!N", "%s >= 0", "positive or zero");
  public static final OpType BVS = new ConditionalBranch("BVS", true, false, false, false, "V", "", "overflow");
  public static final OpType BVC = new ConditionalBranch("BVC", true, false, false, false, "!V", "", "no overflow");
  public static final OpType BHI = new ConditionalBranch("BHI", false, true, true, false, "C && !Z", "(%s & 0xffff_ffffL) > (%s & 0xffff_ffffL)", "unsigned >");
  public static final OpType BLS = new ConditionalBranch("BLS", false, true, true, false, "!C || Z", "(%s & 0xffff_ffffL) <= (%s & 0xffff_ffffL)", "unsigned <=");
  public static final OpType BGE = new ConditionalBranch("BGE", true, false, false, true, "N == V", "%s >= %s", ">=");
  public static final OpType BLT = new ConditionalBranch("BLT", true, false, false, true, "N != V", "%s < %s", "<");
  public static final OpType BGT = new ConditionalBranch("BGT", true, false, true, true, "!Z && N == V", "%s > %s", ">");
  public static final OpType BLE = new ConditionalBranch("BLE", true, false, true, true, "Z || N != V", "%s <= %s", "<=");
  private static final OpType[] THUMB16 = {BEQ, BNE, BCS, BCC, BMI, BPL, BVS, BVC, BHI, BLS, BGE, BLT, BGT, BLE};

  // THUMB17 (SWI)
  public static final OpType SWI = new Swi();

  // THUMB18 (unconditional branch)
  public static final OpType B = new B();

  // THUMB19 (long branch with link)
  public static final OpType BL = new Bl("BL");
  public static final OpType BLX = new Bl("BLX");
  private static final OpType[] THUMB19 = {BL, BLX};

  public static OpState parse(final Memory memory, final int offset) {
    final int op = memory.get(offset, 2);

    // THUMB2 (must be first)
    if((op & 0xf800) == 0x1800) {
      return THUMB2[op >>> 9 & 0x3].parse(offset, op);
    }

    // THUMB1
    if((op & 0xe000) == 0x0) {
      return THUMB1[op >>> 11 & 0x3].parse(offset, op);
    }

    // THUMB3
    if((op & 0xe000) == 0x2000) {
      return THUMB3[op >>> 11 & 0x3].parse(offset, op);
    }

    // THUMB4
    if((op & 0xfc00) == 0x4000) {
      return THUMB4[op >>> 6 & 0xf].parse(offset, op);
    }

    // THUMB5
    if((op & 0xfc00) == 0x4400) {
      return THUMB5[op >>> 8 & 0x3].parse(offset, op);
    }

    // THUMB6
    if((op & 0xf800) == 0x4800) {
      return LDRPC.parse(offset, op);
    }

    // THUMB7/8
    if((op & 0xf000) == 0x5000) {
      if((op & 0x200) == 0) {
        return THUMB7[op >>> 10 & 0x3].parse(offset, op);
      }

      return THUMB8[op >>> 10 & 0x3].parse(offset, op);
    }

    // THUMB9
    if((op & 0xe000) == 0x6000) {
      return THUMB9[op >>> 11 & 0x3].parse(offset, op);
    }

    // THUMB10
    if((op & 0xf000) == 0x8000) {
      return THUMB10[op >>> 11 & 0x1].parse(offset, op);
    }

    // THUMB11
    if((op & 0xf000) == 0x9000) {
      return THUMB11[op >>> 11 & 0x1].parse(offset, op);
    }

    // THUMB12
    if((op & 0xf000) == 0xa000) {
      return THUMB12[op >>> 11 & 0x1].parse(offset, op);
    }

    // THUMB13
    if((op & 0xff00) == 0xb000) {
      return THUMB13[op >>> 7 & 0x1].parse(offset, op);
    }

    // THUMB14
    if((op & 0xf600) == 0xb400) {
      return THUMB14[op >>> 11 & 0x1].parse(offset, op);
    }

    // THUMB15
    if((op & 0xf000) == 0xc000) {
      return THUMB15[op >>> 11 & 0x1].parse(offset, op);
    }

    // THUMB17
    if((op & 0xff00) == 0xdf00) {
      return SWI.parse(offset, op);
    }

    // THUMB16
    if((op & 0xf000) == 0xd000) {
      return THUMB16[op >>> 8 & 0xf].parse(offset, op);
    }

    // THUMB18
    if((op & 0xf800) == 0xe000) {
      return B.parse(offset, op);
    }

    // THUMB19
    if((op & 0xf000) == 0xf000) {
      final int op2 = memory.get(offset + 2, 2);
      return THUMB19[1 - (op2 >>> 12 & 0x1)].parse(offset, op2 << 16 | op);
    }

    throw new RuntimeException("Unknown op 0x%x @ 0x%x".formatted(op, offset));
  }
}
