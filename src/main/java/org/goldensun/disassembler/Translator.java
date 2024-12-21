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
  public List<String> translate(final int address, final DisassemblerConfig config, final Set<OpState> ops, final Map<OpState, Set<OpState>> conditionDependencies) {
    final TranslatorOutput output = new TranslatorOutput();
    final Set<OpState> dependencies = new HashSet<>();
    conditionDependencies.values().forEach(dependencies::addAll);
    final Map<Register, Set<RegisterUsage>> registerUsage = new EnumMap<>(Register.class);

    for(final Register register : Register.values()) {
      registerUsage.put(register, EnumSet.noneOf(RegisterUsage.class));
    }

    for(final OpState op : ops) {
      op.translate(config, output, dependencies.contains(op), conditionDependencies.get(op));
      op.getRegisterUsage(registerUsage);
    }

    final List<String> out = new ArrayList<>();
    for(int i = Math.min(4, config.functions.get(address).params.length); i < 8; i++) {
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
