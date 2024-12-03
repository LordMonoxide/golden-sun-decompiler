package org.goldensun.disassembler;

import org.goldensun.disassembler.ops.OpState;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Translator {
  public void translate(final DisassemblerConfig config, final Map<Integer, OpState> ops, final Map<OpState, List<OpState>> conditionDependencies) {
    final TranslatorOutput output = new TranslatorOutput();
    final Set<OpState> dependencies = new HashSet<>();
    conditionDependencies.values().forEach(dependencies::addAll);

    for(final OpState op : ops.values()) {
      op.translate(config, output, dependencies.contains(op));
    }

    System.out.println(String.join("\n", output.build()));
  }
}
