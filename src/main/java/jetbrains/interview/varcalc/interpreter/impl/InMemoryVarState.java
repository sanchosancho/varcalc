package jetbrains.interview.varcalc.interpreter.impl;

import jetbrains.interview.varcalc.interpreter.VarState;
import jetbrains.interview.varcalc.interpreter.exceptions.ScriptExecutionException;
import jetbrains.interview.varcalc.interpreter.vars.Var;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryVarState implements VarState {
  private final Map<String, Var> globalVars = new HashMap<>();
  private final ThreadLocal<Map<String, Var>> localVars = new ThreadLocal<>();

  @Nullable
  @Override
  public Var get(String name) {
    final Var var = globalVars.get(name);
    if (var == null) {
      return getThreadLocal(name);
    }
    return var;
  }

  @Override
  public void add(String name, Var var, Scope scope) {
    Objects.requireNonNull(var, "var");
    switch (scope) {
      case GLOBAL -> safePut(globalVars, name, var);
      case THREAD_LOCAL -> addThreadLocal(name, var);
    }
  }

  @Override
  public void remove(String name, Scope scope) {
    switch (scope) {
      case GLOBAL -> globalVars.remove(name);
      case THREAD_LOCAL -> removeThreadLocal(name);
    }
  }

  @Override
  public int size() {
    return globalVars.size();
  }

  private Var getThreadLocal(String name) {
    final Map<String, Var> vars = localVars.get();
    if (vars != null) {
      return vars.get(name);
    } else {
      return null;
    }
  }

  private void addThreadLocal(String name, Var var) {
    Map<String, Var> vars = localVars.get();
    if (vars == null) {
      vars = new HashMap<>();
      localVars.set(vars);
    }
    safePut(vars, name, var);
  }

  private void removeThreadLocal(String name) {
    final Map<String, Var> vars = localVars.get();
    if (vars != null) {
      vars.remove(name);
    }
  }

  private static void safePut(Map<String, Var> vars, String name, Var var) {
    if (vars.containsKey(name)) {
      throw new ScriptExecutionException("Var with name " + name + " already exists");
    }
    vars.put(name, var);
  }
}
