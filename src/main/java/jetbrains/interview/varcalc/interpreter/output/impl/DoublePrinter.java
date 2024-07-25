package jetbrains.interview.varcalc.interpreter.output.impl;

import jetbrains.interview.varcalc.interpreter.output.VarPrinter;
import jetbrains.interview.varcalc.interpreter.vars.Double;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DoublePrinter implements VarPrinter<Double> {
  @Override
  public void print(OutputStream outputStream, Double var) throws IOException {
    outputStream.write(java.lang.Double.toString(var.value()).getBytes(StandardCharsets.UTF_8));
  }
}
