package jetbrains.interview.varcalc.interpreter.impl;

import jetbrains.interview.varcalc.interpreter.VarState;
import jetbrains.interview.varcalc.interpreter.exceptions.ScriptExecutionException;
import jetbrains.interview.varcalc.interpreter.vars.Integer;
import jetbrains.interview.varcalc.interpreter.vars.TestVars;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class BaseVarStateTest {

  protected final VarState state;

  public BaseVarStateTest(VarState state) {
    this.state = state;
  }

  @Test
  public void getEmpty() {
    assertNull(state.get("name"));
  }

  @Test
  public void add() {
    final Integer var = TestVars.randomInteger();
    state.add("a", var);
    assertNotNull(state.get("a"));
    assertEquals(var, state.get("a"));
  }

  @Test
  public void remove() {
    state.add("a", TestVars.randomInteger());
    assertNotNull(state.get("a"));
    state.remove("a");
    assertNull(state.get("a"));
  }

  @Test
  public void size() {
    assertEquals(0, state.size());
    state.add("a", TestVars.randomInteger());
    assertEquals(1, state.size());
    state.add("b", TestVars.randomInteger());
    assertEquals(2, state.size());
  }

  @Test(expected = ScriptExecutionException.class)
  public void overwrite() {
    state.add("a", TestVars.randomInteger());
    state.add("a", TestVars.randomInteger());
  }
}