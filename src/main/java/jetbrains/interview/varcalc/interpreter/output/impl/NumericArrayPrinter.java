package jetbrains.interview.varcalc.interpreter.output.impl;

import jetbrains.interview.varcalc.interpreter.output.PrinterRegistry;
import jetbrains.interview.varcalc.interpreter.output.VarPrinter;
import jetbrains.interview.varcalc.interpreter.vars.Numeric;
import jetbrains.interview.varcalc.interpreter.vars.NumericArray;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class NumericArrayPrinter implements VarPrinter<NumericArray> {
  @Override
  public void print(OutputStream outputStream, NumericArray value) throws IOException {
    final Numeric[] array = value.array();

    outputStream.write("[ ".getBytes(StandardCharsets.UTF_8));
    PrinterRegistry.print(outputStream, array[0]);
    for (int i = 1; i < array.length; i++) {
      outputStream.write(", ".getBytes(StandardCharsets.UTF_8));
      PrinterRegistry.print(outputStream, array[i]);
    }
    outputStream.write(" ]".getBytes(StandardCharsets.UTF_8));
  }
}
