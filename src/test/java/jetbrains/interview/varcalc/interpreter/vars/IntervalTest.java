package jetbrains.interview.varcalc.interpreter.vars;

import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IntervalTest {

  @Test
  public void get() {
    final Interval interval = new Interval(1, 10);

    assertEquals(1, interval.begin());
    assertEquals(10, interval.end());
    for (int i = 1; i <= 10; i++) {
      assertEquals(i, TypeTraits.cast(interval.get(i - 1), Integer.class).value());
    }
  }

  @Test
  public void stream() {
    final Interval interval = new Interval(1, 10);

    final Stream<Numeric> stream = interval.stream();
    assertNotNull(stream);

    int val = 1;
    for (Numeric n : stream.toArray(Numeric[]::new)) {
      assertEquals(val, TypeTraits.cast(n, Integer.class).value());
      val++;
    }
  }

  @Test
  public void size() {
    final Interval interval = new Interval(1, 10);
    assertEquals(10, interval.size());
  }
}
