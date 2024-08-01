package jetbrains.interview.varcalc.interpreter.impl;

import jetbrains.interview.varcalc.interpreter.VarState;
import jetbrains.interview.varcalc.interpreter.exceptions.ScriptExecutionException;
import jetbrains.interview.varcalc.interpreter.vars.Var;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class InMemoryVarState implements VarState {
  private final Map<String, Var> vars = new HashMap<>();

  @Nullable
  @Override
  public Var get(String name) {
    return vars.get(name);
  }

  @Override
  public void add(String name, Var var) {
    safePut(vars, name, var);
  }

  @Override
  public void remove(String name) {
    vars.remove(name);
  }

  @Override
  public int size() {
    return vars.size();
  }

  private static void safePut(Map<String, Var> vars, String name, Var var) {
    if (vars.containsKey(name)) {
      throw new ScriptExecutionException("Var with name '" + name + "' already exists", 0);
    }
    vars.put(name, var);
  }
}
