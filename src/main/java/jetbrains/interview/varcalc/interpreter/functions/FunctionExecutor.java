package jetbrains.interview.varcalc.interpreter.functions;

import jetbrains.interview.varcalc.interpreter.vars.Numeric;
import jetbrains.interview.varcalc.interpreter.vars.Sequential;

import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Executes map and reduce operations. Implementations might utilize multiple threads to parallelize computations.
 */
public interface FunctionExecutor extends AutoCloseable {
  /**
   * Maps a sequence to a new sequence by applying provided function to each element.
   * @param sequence source sequence
   * @param mapper mapping function
   * @return new sequence of the same size
   * @throws jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException if incompatible types occur during computations
   */
  Sequential map(Sequential sequence, Function<Numeric, Numeric> mapper);

  /**
   * Reduces a sequence to a single numeric value by applying binary operator to pairs of elements.
   * Implementations expect binary operator to be associative.
   * @param sequence source sequence
   * @param identity the identity value
   * @param reducer associative binary operator to combine elements
   * @return result of reduction
   * @throws jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException if incompatible types occur during computations
   */
  Numeric reduce(Sequential sequence, Numeric identity, BinaryOperator<Numeric> reducer);
}
