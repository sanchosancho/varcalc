package jetbrains.interview.varcalc.interpreter.functions.impl;

import jetbrains.interview.varcalc.interpreter.functions.FunctionExecutor;
import jetbrains.interview.varcalc.interpreter.vars.Numeric;
import jetbrains.interview.varcalc.interpreter.vars.NumericArray;
import jetbrains.interview.varcalc.interpreter.vars.Sequential;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

public class StreamBasedFunctionExecutor implements FunctionExecutor {
  private final boolean useParallelStreams;

  public StreamBasedFunctionExecutor(boolean useParallelStreams) {
    this.useParallelStreams = useParallelStreams;
  }

  @Override
  public void close() {
  }

  @Override
  public Sequential map(Sequential sequence, Function<Numeric, Numeric> mapper) {
    return new NumericArray(getStream(sequence).map(mapper).toArray(Numeric[]::new));
  }

  @Override
  public Numeric reduce(Sequential sequence, Numeric identity, BinaryOperator<Numeric> reducer) {
    return getStream(sequence).reduce(identity, reducer);
  }

  private Stream<Numeric> getStream(Sequential sequence) {
    return useParallelStreams ? sequence.stream().parallel() : sequence.stream();
  }
}
