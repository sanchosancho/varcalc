package jetbrains.interview.varcalc.interpreter.vars;

import jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class TypeTraitsTest {

  private final Random random = new Random();

  @Test
  public void extractIntegerValue() {
    final Integer var = TestVars.randomInteger();
    assertEquals(var.value(), TypeTraits.extractIntegerValue(var));
  }

  @Test(expected = InvalidTypeException.class)
  public void extractIntegerValueFromDouble() {
    TypeTraits.extractIntegerValue(TestVars.randomDouble());
    fail("Double should not be downscaled to integer");
  }

  @Test
  public void extractDoubleValue() {
    final Double var = TestVars.randomDouble();
    assertEquals(var.value(), TypeTraits.extractDoubleValue(var), 0.0);
  }

  @Test
  public void extractDoubleValueFromInteger() {
    final Integer var = TestVars.randomInteger();
    assertEquals(var.value(), TypeTraits.extractDoubleValue(var), 0.0);
  }

  @Test
  public void isFloatingPoint() {
    assertTrue(TypeTraits.isFloatingPoint(TestVars.randomDouble()));
    assertFalse(TypeTraits.isFloatingPoint(TestVars.randomInteger()));
  }

  @Test
  public void promoteToDoubleFromDouble() {
    final Double var = TestVars.randomDouble();
    assertEquals(var, TypeTraits.promoteToDouble(var));
  }

  @Test
  public void promoteToDoubleFromInteger() {
    final Integer var = TestVars.randomInteger();
    assertEquals(var.value(), TypeTraits.promoteToDouble(var).value(), 0.0);
  }

  @Test
  public void castLegalCases() {
    TypeTraits.cast(TestVars.randomInteger(), Var.class);
    TypeTraits.cast(TestVars.randomInteger(), Numeric.class);
    TypeTraits.cast(TestVars.randomInteger(), Integer.class);
    TypeTraits.cast(TestVars.randomDouble(), Var.class);
    TypeTraits.cast(TestVars.randomDouble(), Numeric.class);
    TypeTraits.cast(TestVars.randomDouble(), Double.class);
    TypeTraits.cast(TestVars.randomInterval(), Sequential.class);
    TypeTraits.cast(TestVars.randomInterval(), Interval.class);
    TypeTraits.cast(TestVars.randomIntegerArray(3), Sequential.class);
    TypeTraits.cast(TestVars.randomIntegerArray(3), NumericArray.class);
  }

  @Test(expected = InvalidTypeException.class)
  public void castInt2Dbl() {
    TypeTraits.cast(TestVars.randomInteger(), Double.class);
  }

  @Test(expected = InvalidTypeException.class)
  public void castInt2Seq() {
    TypeTraits.cast(TestVars.randomInteger(), Sequential.class);
  }

  @Test(expected = InvalidTypeException.class)
  public void castInt2Interval() {
    TypeTraits.cast(TestVars.randomInteger(), Interval.class);
  }

  @Test(expected = InvalidTypeException.class)
  public void castInt2NumericArray() {
    TypeTraits.cast(TestVars.randomInteger(), NumericArray.class);
  }

  @Test(expected = InvalidTypeException.class)
  public void castDbl2Int() {
    TypeTraits.cast(TestVars.randomDouble(), Integer.class);
  }

  @Test(expected = InvalidTypeException.class)
  public void castDbl2Seq() {
    TypeTraits.cast(TestVars.randomDouble(), Sequential.class);
  }

  @Test(expected = InvalidTypeException.class)
  public void castDbl2Interval() {
    TypeTraits.cast(TestVars.randomDouble(), Interval.class);
  }

  @Test(expected = InvalidTypeException.class)
  public void castDbl2NumericArray() {
    TypeTraits.cast(TestVars.randomDouble(), NumericArray.class);
  }
}