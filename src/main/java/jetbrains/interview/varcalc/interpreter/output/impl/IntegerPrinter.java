package jetbrains.interview.varcalc.interpreter.output.impl;

import jetbrains.interview.varcalc.interpreter.output.VarPrinter;
import jetbrains.interview.varcalc.interpreter.vars.Integer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class IntegerPrinter implements VarPrinter<Integer> {
  @Override
  public void print(OutputStream outputStream, Integer var) throws IOException {
    outputStream.write(java.lang.Integer.toString(var.value()).getBytes(StandardCharsets.UTF_8));
  }
}
