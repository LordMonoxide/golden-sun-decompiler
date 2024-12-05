package org.goldensun;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DecompReader {
  public Map<Integer, FunctionInfo> loadMethods(final Path root) throws IOException {
    StaticJavaParser.getParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
    final Path src = root.resolve("src/main/java/org/goldensun");

    final Map<Integer, FunctionInfo> functions = new HashMap<>();

    final PathMatcher glob = FileSystems.getDefault().getPathMatcher("glob:**GoldenSun*.java");
    try(final Stream<Path> fileStream = Files.walk(src).filter(glob::matches)) {
      final List<Path> files = fileStream.toList();

      for(final Path file : files) {
        this.loadMethods(file, functions);
      }
    }

    return functions;
  }

  private void loadMethods(final Path file, final Map<Integer, FunctionInfo> functions) throws IOException {
    final CompilationUnit cls = StaticJavaParser.parse(file);
    cls.accept(new VoidVisitorAdapter<>() {
      @Override
      public void visit(final MethodDeclaration n, final Object arg) {
        n.getAnnotationByName("Method").ifPresent(annotation -> {
          final String addressStr = annotation.findAll(IntegerLiteralExpr.class).getFirst().getValue();

          final int address;
          if(addressStr.startsWith("0x")) {
            address = Integer.parseUnsignedInt(addressStr.substring(2), 16);
          } else {
            address = Integer.parseUnsignedInt(addressStr);
          }

          final String name = n.getNameAsString();
          final String returnType = n.getTypeAsString();
          final ParamInfo[] params = new ParamInfo[n.getParameters().size()];

          for(int i = 0; i < n.getParameters().size(); i++) {
            final Parameter param = n.getParameters().get(i);
            params[i] = new ParamInfo(param.getNameAsString(), param.getTypeAsString());
          }

          functions.put(address, new FunctionInfo(address, name, returnType, params));
        });
      }
    }, null);
  }
}
