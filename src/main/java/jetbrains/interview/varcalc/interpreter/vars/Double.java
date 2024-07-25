package jetbrains.interview.varcalc.interpreter.vars;

public record Double(double value) implements Numeric {
  @Override
  public Double add(Numeric right) {
    TypeTraits.assertSameType(this, right);
    return new Double(value + TypeTraits.extractDoubleValue(right));
  }

  @Override
  public Double subtract(Numeric right) {
    TypeTraits.assertSameType(this, right);
    return new Double(value - TypeTraits.extractDoubleValue(right));
  }

  @Override
  public Double multiply(Numeric right) {
    TypeTraits.assertSameType(this, right);
    return new Double(value * TypeTraits.extractDoubleValue(right));
  }

  @Override
  public Double divide(Numeric right) {
    TypeTraits.assertSameType(this, right);
    return new Double(value / TypeTraits.extractDoubleValue(right));
  }

  @Override
  public Double pow(Numeric n) {
    TypeTraits.assertSameType(this, n);
    return new Double(Math.pow(value, TypeTraits.extractDoubleValue(n)));
  }

  @Override
  public Double negate() {
    return new Double(-value);
  }
}
