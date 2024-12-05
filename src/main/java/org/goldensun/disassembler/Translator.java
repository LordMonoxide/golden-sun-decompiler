package org.goldensun.disassembler;

import org.goldensun.disassembler.ops.OpState;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Translator {
  public List<String> translate(final DisassemblerConfig config, final Map<Integer, OpState> ops, final Map<OpState, List<OpState>> conditionDependencies) {
    final TranslatorOutput output = new TranslatorOutput();
    final Set<OpState> dependencies = new HashSet<>();
    conditionDependencies.values().forEach(dependencies::addAll);
    final Map<Register, Set<RegisterUsage>> registerUsage = new EnumMap<>(Register.class);

    for(final Register register : Register.values()) {
      registerUsage.put(register, EnumSet.noneOf(RegisterUsage.class));
    }

    for(final OpState op : ops.values()) {
      op.translate(config, output, dependencies.contains(op));
      op.getRegisterUsage(registerUsage);
    }

    final List<String> out = new ArrayList<>();
    for(int i = 0; i < 8; i++) {
      if(registerUsage.get(Register.values()[i]).contains(RegisterUsage.WRITE)) {
        out.add("int r%x;".formatted(i));
      }
    }

    if(!out.isEmpty()) {
      out.add("");
    }

    out.addAll(output.build());
    return out;
  }
}
