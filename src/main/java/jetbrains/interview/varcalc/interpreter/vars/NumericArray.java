package jetbrains.interview.varcalc.interpreter.vars;

import java.util.Arrays;
import java.util.stream.Stream;

public record NumericArray(Numeric[] array) implements Sequential {
  @Override
  public Numeric get(int i) {
    return array[i];
  }

  @Override
  public Stream<Numeric> stream() {
    return Arrays.stream(array);
  }

  @Override
  public int size() {
    return array.length;
  }
}
