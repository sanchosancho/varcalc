package jetbrains.interview.varcalc.interpreter.functions.impl;

import jetbrains.interview.varcalc.interpreter.functions.FunctionExecutor;
import jetbrains.interview.varcalc.interpreter.vars.Double;
import jetbrains.interview.varcalc.interpreter.vars.Integer;
import jetbrains.interview.varcalc.interpreter.vars.Interval;
import jetbrains.interview.varcalc.interpreter.vars.Numeric;
import jetbrains.interview.varcalc.interpreter.vars.NumericArray;
import jetbrains.interview.varcalc.interpreter.vars.Sequential;
import jetbrains.interview.varcalc.interpreter.vars.TestVars;
import jetbrains.interview.varcalc.interpreter.vars.TypeTraits;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class FunctionExecutorTest {
  private final FunctionExecutor executor;

  public FunctionExecutorTest(FunctionExecutor executor) {
    this.executor = executor;
  }

  @Test
  public void mapInterval() {
    final Interval interval = TestVars.randomInterval();
    testMapIntegerSeq(interval);
  }

  @Test
  public void mapNumericArray() {
    final NumericArray array = TestVars.randomIntegerArray();
    testMapIntegerSeq(array);
  }

  @Test
  public void mapIntervalToDouble() {
    final Interval interval = TestVars.randomInterval();
    testMapDoubleSeq(interval);
  }

  @Test
  public void reduceInterval() {
    final Interval interval = TestVars.randomInterval();
    testReduce(interval);
  }

  @Test
  public void reduceIntegerArray() {
    final NumericArray array = TestVars.randomIntegerArray();
    testReduce(array);
  }

  private void testMapIntegerSeq(Sequential sequence) {
    final Integer delta = TestVars.randomInteger(10);
    final Sequential mapped = executor.map(sequence, n -> n.add(delta));
    final int mappedSize = mapped.size();

    assertTrue(mappedSize > 0);
    assertEquals(sequence.size(), mappedSize);
    for (int i = 0; i < mappedSize; i++) {
      final int src = TypeTraits.cast(sequence.get(i), Integer.class).value();
      final int dst = TypeTraits.cast(mapped.get(i), Integer.class).value();
      assertEquals(src + delta.value(), dst);
    }
  }

  private void testMapDoubleSeq(Sequential sequence) {
    final Double delta = TestVars.randomDouble();
    final Sequential mapped = executor.map(sequence, n -> TypeTraits.promoteToDouble(n).add(delta));
    final int mappedSize = mapped.size();

    assertTrue(mappedSize > 0);
    assertEquals(sequence.size(), mappedSize);
    for (int i = 0; i < mappedSize; i++) {
      final double src = TypeTraits.cast(sequence.get(i), Integer.class).value();
      final double dst = TypeTraits.cast(mapped.get(i), Double.class).value();
      assertEquals(src + delta.value(), dst, 0.0);
    }
  }

  private void testReduce(Sequential sequence) {
    final Numeric result = executor.reduce(sequence, new Integer(0), Numeric::add);

    int reference = 0;
    for (int i = 0; i < sequence.size(); i++) {
      reference += TypeTraits.cast(sequence.get(i), Integer.class).value();
    }

    assertEquals(reference, TypeTraits.cast(result, Integer.class).value());
  }
}
