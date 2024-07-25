package jetbrains.interview.varcalc.interpreter.impl;

import jetbrains.interview.varcalc.interpreter.VarState;
import jetbrains.interview.varcalc.interpreter.exceptions.ScriptExecutionException;
import jetbrains.interview.varcalc.interpreter.vars.Integer;
import jetbrains.interview.varcalc.interpreter.vars.TestVars;
import jetbrains.interview.varcalc.interpreter.vars.Var;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

public class InMemoryVarStateTest {

  private final InMemoryVarState state = new InMemoryVarState();

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

  @Test
  public void localScope() throws Exception {
    state.add("a", TestVars.randomInteger(), VarState.Scope.THREAD_LOCAL);
    assertNotNull(state.get("a"));
    state.remove("a");
    assertNotNull(state.get("a"));

    final CompletableFuture<Var> asyncVar = CompletableFuture.supplyAsync(() -> state.get("a"));
    assertNull(asyncVar.get());
  }
}