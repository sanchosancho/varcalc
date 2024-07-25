package jetbrains.interview.varcalc.interpreter.vars;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static org.junit.Assert.assertEquals;

public abstract class NumericTest<JavaType extends Number, VarType extends Numeric> {
  private final Supplier<JavaType> valueGenerator;
  private final Function<JavaType, VarType> varConstructor;
  private final Function<VarType, JavaType> varExtractor;

  public NumericTest(Supplier<JavaType> valueGenerator, Function<JavaType, VarType> varConstructor, Function<VarType, JavaType> varExtractor) {
    this.valueGenerator = valueGenerator;
    this.varConstructor = varConstructor;
    this.varExtractor = varExtractor;
  }

  protected void testBinaryOp(
    BinaryOperator<JavaType> referenceOperation,
    BinaryOperator<VarType> varOperation
  ) {
    final JavaType valLeft = valueGenerator.get();
    final JavaType valRight = valueGenerator.get();

    final VarType left = varConstructor.apply(valLeft);
    final VarType right = varConstructor.apply(valRight);

    assertEquals(referenceOperation.apply(valLeft, valRight), varExtractor.apply(varOperation.apply(left, right)));
  }

  protected void testUnaryOp(
    UnaryOperator<JavaType> referenceOperation,
    UnaryOperator<VarType> varOperation
  ) {
    final JavaType val = valueGenerator.get();
    final VarType var = varConstructor.apply(val);

    assertEquals(referenceOperation.apply(val), varExtractor.apply(varOperation.apply(var)));
  }
}
