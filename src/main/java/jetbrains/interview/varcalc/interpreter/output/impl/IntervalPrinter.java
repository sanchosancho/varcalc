package jetbrains.interview.varcalc.interpreter.output.impl;

import jetbrains.interview.varcalc.interpreter.output.VarPrinter;
import jetbrains.interview.varcalc.interpreter.vars.Interval;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class IntervalPrinter implements VarPrinter<Interval> {
  @Override
  public void print(OutputStream outputStream, Interval value) throws IOException {
    outputStream.write(String.format("{ %d, %d }", value.begin(), value.end()).getBytes(StandardCharsets.UTF_8));
  }
}
