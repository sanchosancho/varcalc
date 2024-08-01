package jetbrains.interview.varcalc.interpreter.output;

import jetbrains.interview.varcalc.interpreter.output.impl.DoublePrinter;
import jetbrains.interview.varcalc.interpreter.output.impl.IntegerPrinter;
import jetbrains.interview.varcalc.interpreter.output.impl.IntervalPrinter;
import jetbrains.interview.varcalc.interpreter.output.impl.NumericArrayPrinter;
import jetbrains.interview.varcalc.interpreter.vars.Double;
import jetbrains.interview.varcalc.interpreter.vars.Integer;
import jetbrains.interview.varcalc.interpreter.vars.Interval;
import jetbrains.interview.varcalc.interpreter.vars.NumericArray;
import jetbrains.interview.varcalc.interpreter.vars.Var;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class PrinterRegistry {
  private static final Map<Class<? extends Var>, VarPrinter<? extends Var>> PRINTERS = new HashMap<>();

  static {
    // default printers
    register(Double.class, new DoublePrinter());
    register(Integer.class, new IntegerPrinter());
    register(Interval.class, new IntervalPrinter());
    register(NumericArray.class, new NumericArrayPrinter());
  }

  public static <T extends Var> void register(Class<T> type, VarPrinter<T> printer) {
    PRINTERS.put(type, printer);
  }

  @Nullable
  public static <T extends Var> VarPrinter<T> find(Class<T> type) {
    final VarPrinter<? extends Var> printer = PRINTERS.get(type);
    //noinspection unchecked
    return (VarPrinter<T>)printer;
  }

  public static <T extends Var> void print(OutputStream outputStream, T var) throws IOException {
    //noinspection unchecked
    final VarPrinter<T> printer = find((Class<T>)var.getClass());
    if (printer != null) {
      printer.print(outputStream, var);
    }
  }
}
