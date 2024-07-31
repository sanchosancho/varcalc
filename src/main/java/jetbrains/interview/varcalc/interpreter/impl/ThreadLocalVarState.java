package jetbrains.interview.varcalc.interpreter.impl;

import jetbrains.interview.varcalc.interpreter.VarState;
import jetbrains.interview.varcalc.interpreter.vars.Var;

import javax.annotation.Nullable;

public class ThreadLocalVarState implements VarState {
  private final ThreadLocal<InMemoryVarState> delegate = new ThreadLocal<>();

  @Nullable
  @Override
  public Var get(String name) {
    return delegate.get() != null ? delegate.get().get(name) : null;
  }

  @Override
  public void add(String name, Var value) {
    createIfNeeded().add(name, value);
  }

  @Override
  public void remove(String name) {
    if (delegate.get() != null) {
      delegate.get().remove(name);
    }
  }

  @Override
  public int size() {
    return delegate.get() != null ? delegate.get().size() : 0;
  }

  private InMemoryVarState createIfNeeded() {
    InMemoryVarState state = delegate.get();
    if (state == null) {
      state = new InMemoryVarState();
      delegate.set(state);
    }
    return state;
  }
}
