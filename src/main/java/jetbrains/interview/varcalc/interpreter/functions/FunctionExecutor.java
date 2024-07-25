package jetbrains.interview.varcalc.interpreter.functions;

import jetbrains.interview.varcalc.interpreter.vars.Numeric;
import jetbrains.interview.varcalc.interpreter.vars.Sequential;

import java.util.function.BinaryOperator;
import java.util.function.Function;

public interface FunctionExecutor {
  Sequential map(Sequential sequence, Function<Numeric, Numeric> mapper);

  Numeric reduce(Sequential sequence, Numeric identity, BinaryOperator<Numeric> reducer);
}
