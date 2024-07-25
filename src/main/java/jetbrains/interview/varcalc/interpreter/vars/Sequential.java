package jetbrains.interview.varcalc.interpreter.vars;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public interface Sequential extends Var {
  Numeric get(int i);

  Stream<Numeric> stream();

  int size();

  default Sequential map(UnaryOperator<Numeric> mapper) {
    return new NumericArray(stream().parallel().map(mapper).toArray(Numeric[]::new));
  }

  default Numeric reduce(Numeric identity, BinaryOperator<Numeric> reducer) {
    return stream().parallel().reduce(identity, reducer);
  }
}
