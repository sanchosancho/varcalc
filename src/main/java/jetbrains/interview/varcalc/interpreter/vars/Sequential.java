package jetbrains.interview.varcalc.interpreter.vars;

import java.util.stream.Stream;

/**
 * A variable that contains sequence of {@link Integer} or {@link Double} values. Provides methods to access and iterate over elements.
 * @see Interval
 * @see NumericArray
 */
public interface Sequential extends Var {
  /**
   * Returns sequence element at provided position.
   * @param i index of the element
   * @return element
   */
  Numeric get(int i);

  /**
   * Returns {@link Stream} of elemnts
   * @return stream
   */
  Stream<Numeric> stream();

  /**
   * Returns number of elements in sequence
   * @return number of elements
   */
  int size();
}
