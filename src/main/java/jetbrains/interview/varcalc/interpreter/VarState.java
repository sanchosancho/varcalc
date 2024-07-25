package jetbrains.interview.varcalc.interpreter;

import jetbrains.interview.varcalc.interpreter.vars.Var;

import javax.annotation.Nullable;

public interface VarState {
  enum Scope {
    GLOBAL,
    THREAD_LOCAL
  }

  @Nullable
  Var get(String name);

  default void add(String name, Var value) {
    add(name, value, Scope.GLOBAL);
  }

  void add(String name, Var value, Scope scope);

  default void remove(String name) {
    remove(name, Scope.GLOBAL);
  }

  void remove(String name, Scope scope);

  int size();
}
