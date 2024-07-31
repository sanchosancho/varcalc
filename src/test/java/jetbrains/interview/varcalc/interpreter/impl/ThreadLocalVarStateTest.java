package jetbrains.interview.varcalc.interpreter.impl;

import jetbrains.interview.varcalc.interpreter.vars.TestVars;
import jetbrains.interview.varcalc.interpreter.vars.Var;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ThreadLocalVarStateTest extends BaseVarStateTest {

  public ThreadLocalVarStateTest() {
    super(new ThreadLocalVarState());
  }

  @Test
  public void threadLocality() throws Exception {
    state.add("a", TestVars.randomInteger());
    assertNotNull(state.get("a"));

    final CompletableFuture<Var> asyncVar = CompletableFuture.supplyAsync(() -> state.get("a"));
    assertNull(asyncVar.get());

    CompletableFuture.runAsync(() -> {
      state.add("b", TestVars.randomInteger());
      assertNotNull(state.get("b"));
    });
    assertNull(state.get("b"));
  }
}
