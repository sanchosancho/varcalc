package jetbrains.interview.varcalc.interpreter.vars;

import org.junit.Test;

public class DoubleTest extends NumericTest<java.lang.Double, Double> {

  public DoubleTest() {
    super(
      TestVars.RNG::nextDouble,
      Double::new,
      Double::value
    );
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

//  @Test
//  public void inconsistentTypes() {
//    final Double d = new Double(0.75);
//    final Integer i = new Integer(1);
//    d.add(i);
//    d.subtract(i);
//    d.multiply(i);
//    d.divide(i);
//    d.pow(i);
//    fail("Operations should not allow implicit type casting");
//  }
}