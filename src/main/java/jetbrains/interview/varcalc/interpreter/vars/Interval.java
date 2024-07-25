package jetbrains.interview.varcalc.interpreter.vars;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Interval(int begin, int end) implements Sequential {
  @Override
  public Numeric get(int i) {
    return new Integer(begin + i);
  }

  @Override
  public Stream<Numeric> stream() {
    return IntStream.rangeClosed(begin, end)
      .parallel()
      .mapToObj(Integer::new)
      .map(Numeric.class::cast);
  }

  @Override
  public int size() {
    return end - begin + 1;
  }
}
