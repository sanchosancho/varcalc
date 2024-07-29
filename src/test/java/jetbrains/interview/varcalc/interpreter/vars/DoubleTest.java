package jetbrains.interview.varcalc.interpreter.vars;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DoubleTest extends NumericTest<java.lang.Double, Double> {

  public DoubleTest() {
    super(
      TestVars.RNG::nextDouble,
      Double::new,
      Double::value
    );
  }

  @Test
  public void value() {
    final Double integer = new Double(0.5);
    assertEquals(0.5, integer.value(), 0.0);
  }

  @Test
  public void add() {
    testBinaryOp(java.lang.Double::sum, Double::add);
  }

  @Test
  public void subtract() {
    testBinaryOp((a, b) -> a - b, Double::subtract);
  }

  @Test
  public void multiply() {
    testBinaryOp((a, b) -> a * b, Double::multiply);
  }

  @Test
  public void divide() {
    testBinaryOp((a, b) -> a / b, Double::divide);
  }

  @Test
  public void pow() {
    testBinaryOp(Math::pow, Double::pow);
  }

  @Test
  public void negate() {
    testUnaryOp(a -> -a, Double::negate);
  }
}