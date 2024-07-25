package jetbrains.interview.varcalc.interpreter.vars;

import java.util.function.BinaryOperator;
import java.util.function.Function;

public class FunctionCalls {
  public static Sequential map(Sequential sequence, Function<Numeric, Numeric> mapper) {
    return new NumericArray(sequence.stream().map(mapper).toArray(Numeric[]::new));
  }

  public static Numeric reduce(Sequential sequence, Numeric identity, BinaryOperator<Numeric> reducer) {
    return sequence.stream().reduce(identity, reducer);
  }
}
