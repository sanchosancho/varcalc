package jetbrains.interview.varcalc.interpreter.vars;

/**
 * A variable that contains numeric value: integer number or double precision floating point number. Provides standard arithmetic operations.
 * @see Integer
 * @see Double
 */
public interface Numeric extends Var {
  /**
   * Adds another numeric value to this variable and returns new value.
   * @param right right operand of sum
   * @return result of sum
   * @throws jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException if right operand type differs
   */
  Numeric add(Numeric right);

  /**
   * Subtracts another numeric value from this variable and returns new value.
   * @param right right operand of subraction
   * @return result of subtraction
   * @throws jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException if right operand type differs
   */
  Numeric subtract(Numeric right);

  /**
   * Multiplies this variable by another numeric value and returns new value.
   * @param right right operand of multiplication
   * @return result of multiplication
   * @throws jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException if right operand type differs
   */
  Numeric multiply(Numeric right);

  /**
   * Divides this variable by another numeric value and returns new value.
   * @param right right operand of division
   * @return result of division
   * @throws jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException if right operand type differs
   */
  Numeric divide(Numeric right);

  /**
   * Raises this variable to the power of provided value.
   * @param n exponent
   * @return result
   * @throws jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException if right operand type differs
   */
  Numeric pow(Numeric n);

  /**
   * Negates this variable and returns new value
   * @return negated value
   */
  Numeric negate();
}
