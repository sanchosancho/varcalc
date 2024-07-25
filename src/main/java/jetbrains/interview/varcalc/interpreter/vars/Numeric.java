package jetbrains.interview.varcalc.interpreter.vars;

public interface Numeric extends Var {
  Numeric add(Numeric right);

  Numeric subtract(Numeric right);

  Numeric multiply(Numeric right);

  Numeric divide(Numeric right);

  Numeric pow(Numeric n);

  Numeric negate();
}
