package jetbrains.interview.varcalc.interpreter.vars;

import jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Compact representation of a sequence defined by interval in form of [begin, end].
 * @param begin first element of interval, inclusively
 * @param end first element of interval, inclusively
 */
public record Interval(int begin, int end) implements Sequential {
  public Interval {
    if (begin > end) {
      throw new InvalidTypeException(String.format("Invalid interval bounds: [%d, %d]", begin, end));
    }
  }

  @Override
  public Numeric get(int i) {
    return new Integer(begin + i);
  }

  @Override
  public Stream<Numeric> stream() {
    return IntStream.rangeClosed(begin, end)
      .mapToObj(Integer::new)
      .map(Numeric.class::cast);
  }

  @Override
  public int size() {
    return end - begin + 1;
  }
}
