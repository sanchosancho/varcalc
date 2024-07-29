package jetbrains.interview.varcalc.interpreter.vars;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntegerTest extends NumericTest<java.lang.Integer, Integer> {

  public IntegerTest() {
    super(
      TestVars.RNG::nextInt,
      Integer::new,
      Integer::value
    );
  }

  @Test
  public void value() {
    final Integer integer = new Integer(10);
    assertEquals(10, integer.value());
  }

  @Test
  public void add() {
    testBinaryOp(java.lang.Integer::sum, Integer::add);
  }

  @Test
  public void subtract() {
    testBinaryOp((a, b) -> a - b, Integer::subtract);
  }

  @Test
  public void multiply() {
    testBinaryOp((a, b) -> a * b, Integer::multiply);
  }

  @Test
  public void divide() {
    testBinaryOp((a, b) -> a / b, Integer::divide);
  }

  @Test
  public void pow() {
    testBinaryOp((a, b) -> (int)Math.pow(a, b), Integer::pow);
  }

  @Test
  public void negate() {
    testUnaryOp(a -> -a, Integer::negate);
  }
}