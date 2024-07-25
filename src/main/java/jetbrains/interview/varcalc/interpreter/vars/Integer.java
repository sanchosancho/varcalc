package jetbrains.interview.varcalc.interpreter.vars;

public record Integer(int value) implements Numeric {
  @Override
  public Integer add(Numeric right) {
    TypeTraits.assertSameType(this, right);
    return new Integer(value + TypeTraits.extractIntegerValue(right));
  }

  @Override
  public Integer subtract(Numeric right) {
    TypeTraits.assertSameType(this, right);
    return new Integer(value - TypeTraits.extractIntegerValue(right));
  }

  @Override
  public Integer multiply(Numeric right) {
    TypeTraits.assertSameType(this, right);
    return new Integer(value * TypeTraits.extractIntegerValue(right));
  }

  @Override
  public Integer divide(Numeric right) {
    TypeTraits.assertSameType(this, right);
    return new Integer(value / TypeTraits.extractIntegerValue(right));
  }

  @Override
  public Integer pow(Numeric n) {
    TypeTraits.assertSameType(this, n);
    return new Integer((int)Math.pow(value, TypeTraits.extractIntegerValue(n)));
  }

  @Override
  public Integer negate() {
    return new Integer(-value);
  }
}
