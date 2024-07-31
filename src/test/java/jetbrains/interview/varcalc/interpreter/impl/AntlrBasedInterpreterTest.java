package jetbrains.interview.varcalc.interpreter.impl;

import jetbrains.interview.varcalc.interpreter.VarState;
import jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException;
import jetbrains.interview.varcalc.interpreter.exceptions.ScriptExecutionException;
import jetbrains.interview.varcalc.interpreter.vars.Double;
import jetbrains.interview.varcalc.interpreter.vars.Integer;
import jetbrains.interview.varcalc.interpreter.vars.Interval;
import jetbrains.interview.varcalc.interpreter.vars.TypeTraits;
import jetbrains.interview.varcalc.interpreter.vars.Var;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class AntlrBasedInterpreterTest {

  private AntlrBasedInterpreter interpreter;

  @Before
  public void setUp() {
    interpreter = new AntlrBasedInterpreter(1);
  }

  @After
  public void tearDown() throws Exception {
    interpreter.close();
  }

  @Test
  public void integerDeclaration() {
    interpreter.run("var a = 1");
    assertEquals(1, assertAndGetVar(interpreter.state(), "a", Integer.class).value());
  }

  @Test
  public void doubleDeclaration() {
    interpreter.run("var a = 0.5");
    assertEquals(0.5, assertAndGetVar(interpreter.state(), "a", Double.class).value(), 0.0);
  }

  @Test
  public void intervalDeclaration() {
    interpreter.run("var a = {1, 3}");
    final Interval interval = assertAndGetVar(interpreter.state(), "a", Interval.class);
    assertEquals(1, interval.begin());
    assertEquals(3, interval.end());
  }

  @Test(expected = InvalidTypeException.class)
  public void intervalDeclarationInvalid() {
    interpreter.run("var a = {3, 1}");
  }

  @Test(expected = ScriptExecutionException.class)
  public void cannotOverwriteVar() {
    interpreter.run(
      """
      var a = 2 + 2
      var a = 3
      """
    );
  }

  @Test(expected = ScriptExecutionException.class)
  public void cannotCallUndefinedVar() {
    interpreter.run(
      """
      var a = b
      """
    );
  }

  @Test
  public void outputOperator() {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    interpreter.run(
      """
      var a = 2 + 2
      out a
      """,
      output
    );
    assertEquals("4\n", output.toString(StandardCharsets.UTF_8));
  }

  @Test
  public void printOperator() {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    interpreter.run(
      """
      var a = 2 + 2
      print "a = "
      out a
      """,
      output
    );
    assertEquals("a = 4\n", output.toString(StandardCharsets.UTF_8));
  }

  @Test
  public void addOperator() {
    interpreter.run(
      """
      var a = 1
      var b = 2
      var c = a + b
      """
    );
    assertEquals(3, assertAndGetVar(interpreter.state(), "c", Integer.class).value());
  }

  @Test
  public void subtractOperator() {
    interpreter.run(
      """
      var a = 1
      var b = 2
      var c = a - b
      """
    );
    assertEquals(-1, assertAndGetVar(interpreter.state(), "c", Integer.class).value());
  }

  @Test
  public void multiplyOperator() {
    interpreter.run(
      """
      var a = 1
      var b = 2
      var c = a * b
      """
    );
    assertEquals(2, assertAndGetVar(interpreter.state(), "c", Integer.class).value());
  }

  @Test
  public void divideOperator() {
    interpreter.run(
      """
      var a = 3
      var b = 2
      var c = a / b
      """
    );
    assertEquals(1, assertAndGetVar(interpreter.state(), "c", Integer.class).value());
  }

  @Test
  public void powOperator() {
    interpreter.run(
      """
      var a = 3
      var b = 2
      var c = a ^ b
      """
    );
    assertEquals(9, assertAndGetVar(interpreter.state(), "c", Integer.class).value());
  }

  @Test
  public void addOperatorInt2Double() {
    interpreter.run(
      """
      var a = 1
      var b = 2.5
      var c = a + b
      """
    );
    assertEquals(3.5, assertAndGetVar(interpreter.state(), "c", Double.class).value(), 0.0);
  }

  @Test
  public void subtractOperatorInt2Double() {
    interpreter.run(
      """
      var a = 1
      var b = 2.5
      var c = a - b
      """
    );
    assertEquals(-1.5, assertAndGetVar(interpreter.state(), "c", Double.class).value(), 0.0);
  }

  @Test
  public void multiplyOperatorInt2Double() {
    interpreter.run(
      """
      var a = 1.5
      var b = 2
      var c = a * b
      """
    );
    assertEquals(3.0, assertAndGetVar(interpreter.state(), "c", Double.class).value(), 0.0);
  }

  @Test
  public void divideOperatorInt2Double() {
    interpreter.run(
      """
      var a = 3.0
      var b = 2
      var c = a / b
      """
    );
    assertEquals(1.5, assertAndGetVar(interpreter.state(), "c", Double.class).value(), 0.0);
  }

  @Test
  public void powOperatorInt2Double() {
    interpreter.run(
      """
      var a = 1.5
      var b = 2
      var c = a ^ b
      """
    );
    assertEquals(2.25, assertAndGetVar(interpreter.state(), "c", Double.class).value(), 0.0);
  }

  @Test
  public void negateOperator() {
    interpreter.run(
      """
      var a = 1
      var c = -a
      """
    );
    assertEquals(-1, assertAndGetVar(interpreter.state(), "c", Integer.class).value());
  }

  @Test(expected = InvalidTypeException.class)
  public void addOperatorSequence() {
    interpreter.run("var a = {1, 3} + 5");
  }

  @Test(expected = InvalidTypeException.class)
  public void subtractOperatorSequence() {
    interpreter.run("var a = {1, 3} - 5");
  }

  @Test(expected = InvalidTypeException.class)
  public void multiplyOperatorSequence() {
    interpreter.run("var a = {1, 3} * 5");
  }

  @Test(expected = InvalidTypeException.class)
  public void divideOperatorSequence() {
    interpreter.run("var a = {1, 3} / 5");
  }

  @Test(expected = InvalidTypeException.class)
  public void powOperatorSequence() {
    interpreter.run("var a = {1, 3} ^ 5");
  }

  @Test(expected = InvalidTypeException.class)
  public void negateOperatorSequence() {
    interpreter.run("var a = -{1, 3}");
  }

  @Test
  public void operatorPrecedence() {
    interpreter.run(
      """
      var a = 2.0 ^ 2.0 + 4.0 * 2.0 - 4.0 / 2.0
      var b = 2.0 / 2.0 ^ 4.0 + 2.0 * 4.0 - 2.0
      var c = 2.0 - 2.0 / 4.0 ^ 2.0 + 4.0 * 2.0
      var d = 2.0 * 2.0 - 4.0 / 2.0 ^ 4.0 + 2.0
      var e = 2.0 + 2.0 * 4.0 - 2.0 / 4.0 ^ 2.0
      """
    );
    assertEquals(10.0, assertAndGetVar(interpreter.state(), "a", Double.class).value(), 0.0);
    assertEquals(6.125, assertAndGetVar(interpreter.state(), "b", Double.class).value(), 0.0);
    assertEquals(9.875, assertAndGetVar(interpreter.state(), "c", Double.class).value(), 0.0);
    assertEquals(5.75, assertAndGetVar(interpreter.state(), "d", Double.class).value(), 0.0);
    assertEquals(9.875, assertAndGetVar(interpreter.state(), "e", Double.class).value(), 0.0);
  }

  @Test
  public void operatorGrouping() {
    interpreter.run(
      """
      var a = 1.0 + 2.0 * 3.0
      var b = (1.0 + 2.0) * 3.0
      var c = 1.0 / 2.0 + 3.0
      var d = 1.0 / (2.0 + 3.0)
      var e = 1.0 + 2.0 ^ 3.0
      var f = (1.0 + 2.0) ^ 3.0
      """
    );
    assertEquals(7.0, assertAndGetVar(interpreter.state(), "a", Double.class).value(), 0.0);
    assertEquals(9.0, assertAndGetVar(interpreter.state(), "b", Double.class).value(), 0.0);
    assertEquals(3.5, assertAndGetVar(interpreter.state(), "c", Double.class).value(), 0.0);
    assertEquals(0.2, assertAndGetVar(interpreter.state(), "d", Double.class).value(), 0.0);
    assertEquals(9.0, assertAndGetVar(interpreter.state(), "e", Double.class).value(), 0.0);
    assertEquals(27.0, assertAndGetVar(interpreter.state(), "f", Double.class).value(), 0.0);
  }

  @Test(expected = ScriptExecutionException.class)
  public void divisionByZeroInt() {
    interpreter.run("var a = 1 / 0");
  }

  @Test
  public void divisionByZeroDouble() {
    interpreter.run("var a = 1.0 / 0.0");
    assertTrue(java.lang.Double.isInfinite(assertAndGetVar(interpreter.state(), "a", Double.class).value()));
  }

  @Test
  public void mapReduce() {
    interpreter.run(
      """
      var a = map({1, 5}, x -> x * 2)
      var b = reduce(a, 10, x y -> x + y)
      """
    );
    assertEquals(40, assertAndGetVar(interpreter.state(), "b", Integer.class).value());
  }

  @Test
  public void mapReduceNested() {
    interpreter.run(
      """
      var a = map({1, 5}, x -> x * reduce(map({x, x * 2}, y -> y - 1), 0, u w -> u + w))
      var b = reduce(a, 0, x y -> x + y)
      """
    );
    assertEquals(350, assertAndGetVar(interpreter.state(), "b", Integer.class).value());
  }

  @Test(expected = InvalidTypeException.class)
  public void mapLambdaCannotCreateSequence() {
    interpreter.run(
      """
      var a = map({1, 5}, x -> {x, x * 2})
      """
    );
  }

  @Test(expected = ScriptExecutionException.class)
  public void mapLambdaCannotAccessGlobalVars() {
    interpreter.run(
      """
      var a = 2
      var b = map({1, 5}, x -> {x, x * a})
      """
    );
  }

  private static <T extends Var> T assertAndGetVar(VarState state, String name, Class<T> cls) {
    assertNotNull(state);

    final Var var = state.get(name);
    assertNotNull(var);
    return TypeTraits.cast(var, cls);
  }
}