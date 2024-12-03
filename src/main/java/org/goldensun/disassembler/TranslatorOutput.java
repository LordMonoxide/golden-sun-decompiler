package org.goldensun.disassembler;

import org.goldensun.disassembler.ops.OpState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TranslatorOutput {
  private final Map<OpState, List<String>> lines = new HashMap<>();
  private final Map<Integer, Set<String>> labels = new HashMap<>();
  private int lastAddress;

  public void addLine(final OpState op, final String line) {
    this.lines.computeIfAbsent(op, k -> new ArrayList<>()).add(line);
  }

  public void addLabel(final int address, final String label) {
    this.labels.computeIfAbsent(address, k -> new HashSet<>()).add(label);
  }

  public List<String> build() {
    final List<String> output = new ArrayList<>();
    final Map<Integer, Set<String>> labels = new HashMap<>(this.labels);
    this.lastAddress = 0;

    this.lines.entrySet().stream().sorted(Comparator.comparingInt(e -> e.getKey().address)).forEach(e -> {
      final var labelList = labels.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).filter(l -> l.getKey() > this.lastAddress && l.getKey() <= e.getKey().address).map(Map.Entry::getValue).toList();

      if(!labelList.isEmpty()) {
        output.add("");
        labelList.forEach(output::addAll);
      }

      output.addAll(e.getValue());
      this.lastAddress = e.getKey().address;
    });

    return output;
  }
}
