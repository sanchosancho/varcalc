package jetbrains.interview.varcalc.interpreter.vars;

import java.util.Arrays;
import java.util.Random;

public class TestVars {
  public static final Random RNG = new Random();

  public static Integer randomInteger() {
    return new Integer(RNG.nextInt());
  }

  public static Integer randomInteger(int bound) {
    return new Integer(RNG.nextInt(bound));
  }

  public static Double randomDouble() {
    return new Double(RNG.nextDouble());
  }

  public static Interval randomInterval() {
    final int begin = RNG.nextInt();
    return new Interval(begin, begin + RNG.nextInt(100));
  }

  public static NumericArray randomIntegerArray() {
    return randomIntegerArray(RNG.nextInt(100));
  }

  public static NumericArray randomIntegerArray(int size) {
    final Numeric[] array = new Numeric[size];
    Arrays.setAll(array, __ -> new Integer(RNG.nextInt()));
    return new NumericArray(array);
  }
}
