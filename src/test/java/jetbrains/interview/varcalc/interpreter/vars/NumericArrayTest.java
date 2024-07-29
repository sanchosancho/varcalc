package jetbrains.interview.varcalc.interpreter.vars;

import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.*;

public class NumericArrayTest {

  @Test
  public void get() {
    final Numeric[] value = new Numeric[] {
      new Integer(1), new Integer(2), new Integer(3)
    };
    final NumericArray var = new NumericArray(value);

    assertArrayEquals(value, var.array());
    assertEquals(1, TypeTraits.cast(var.get(0), Integer.class).value());
    assertEquals(2, TypeTraits.cast(var.get(1), Integer.class).value());
    assertEquals(3, TypeTraits.cast(var.get(2), Integer.class).value());
  }

  @Test
  public void stream() {
    final NumericArray var = new NumericArray(new Numeric[] {
      new Integer(1), new Integer(2), new Integer(3)
    });

    final Stream<Numeric> stream = var.stream();
    assertNotNull(stream);

    final Numeric[] array = stream.toArray(Numeric[]::new);
    assertEquals(1, TypeTraits.cast(array[0], Integer.class).value());
    assertEquals(2, TypeTraits.cast(array[1], Integer.class).value());
    assertEquals(3, TypeTraits.cast(array[2], Integer.class).value());
  }

  @Test
  public void size() {
    final NumericArray var = new NumericArray(new Numeric[] {
      new Integer(1), new Integer(2), new Integer(3)
    });
    assertEquals(3, var.size());
  }
}
