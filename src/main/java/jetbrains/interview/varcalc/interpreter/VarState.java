package jetbrains.interview.varcalc.interpreter;

import jetbrains.interview.varcalc.interpreter.vars.Var;

import javax.annotation.Nullable;

public interface VarState {
  @Nullable
  Var get(String name);

  void add(String name, Var value);

  void remove(String name);

  int size();
}
