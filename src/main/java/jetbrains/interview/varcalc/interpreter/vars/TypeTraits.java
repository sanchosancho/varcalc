package jetbrains.interview.varcalc.interpreter.vars;

import jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException;

import java.util.Objects;
import java.util.function.BinaryOperator;

public final class TypeTraits {
  private TypeTraits() {
  }

  public static void assertSameType(Numeric left, Var right) {
    final Class<? extends Numeric> leftClass = left.getClass();
    final Class<? extends Var> rightClass = right.getClass();
    if (!leftClass.equals(rightClass)) {
      throw new InvalidTypeException(
        String.format(
          "Expected exactly the same type, but got [%s, %s]",
          leftClass.getSimpleName(),
          rightClass.getSimpleName())
      );
    }
  }

  public static int extractIntegerValue(Numeric var) {
    Objects.requireNonNull(var, "var must be not null");
    final Class<? extends Numeric> cls = var.getClass();
    if (Double.class.equals(cls)) {
      throw new InvalidTypeException("Downscale from Double to Integer is forbidden");
    } else if (Integer.class.equals(cls)) {
      return ((Integer)var).value();
    } else {
      throw new InvalidTypeException(
        String.format("Expected %s type, got %s", Numeric.class.getSimpleName(), cls.getSimpleName())
      );
    }
  }

  public static double extractDoubleValue(Numeric var) {
    Objects.requireNonNull(var, "var must be not null");
    final Class<? extends Numeric> cls = var.getClass();
    if (Double.class.equals(cls)) {
      return ((Double)var).value();
    } else if (Integer.class.equals(cls)) {
      return ((Integer)var).value();
    } else {
      throw new InvalidTypeException(
        String.format("Expected %s type, got %s", Numeric.class.getSimpleName(), cls.getSimpleName())
      );
    }
  }

  public static boolean isFloatingPoint(Numeric var) {
    Objects.requireNonNull(var, "var must be not null");
    return Double.class.equals(var.getClass());
  }

  public static Double promoteToDouble(Var var) {
    final Class<? extends Var> cls = var.getClass();
    if (Double.class.equals(cls)) {
      return (Double)var;
    } else if (Integer.class.equals(cls)) {
      // promotion
      final int intValue = ((Integer) var).value();
      return new Double(intValue);
    } else {
      throw new InvalidTypeException(
        String.format("Expected %s type, got %s", Numeric.class.getSimpleName(), cls.getSimpleName())
      );
    }
  }

  public static <V extends Var> V cast(Var var, Class<V> castType) {
    Objects.requireNonNull(var, "var must be not null");
    final Class<? extends Var> cls = var.getClass();
    if (castType.isAssignableFrom(cls)) {
      return castType.cast(var);
    } else {
      throw new InvalidTypeException(
        String.format("Can not cast type %s to %s", cls.getSimpleName(), castType.getSimpleName())
      );
    }
  }

  public static Numeric performBinaryOperation(
    Numeric left,
    Numeric right,
    BinaryOperator<Numeric> arithmeticOp
  ) {
    if (left.getClass().equals(right.getClass())) {
      // no type promotions
      return arithmeticOp.apply(left, right);
    } else {
      // promote one of operands to Double since the other is Integer
      // assuming we have only 2 Numeric subtypes, otherwise we need more casting rules
      final boolean isLeftDouble = isFloatingPoint(left);
      return arithmeticOp.apply(
        isLeftDouble ? left : promoteToDouble(left),
        isLeftDouble ? promoteToDouble(right) : right
      );
    }
  }
}
