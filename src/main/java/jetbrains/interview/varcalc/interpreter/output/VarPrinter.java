package jetbrains.interview.varcalc.interpreter.output;

import jetbrains.interview.varcalc.interpreter.vars.Var;

import java.io.IOException;
import java.io.OutputStream;

public interface VarPrinter<T extends Var> {
  void print(OutputStream outputStream, T value) throws IOException;
}
