package jetbrains.interview.varcalc.interpreter;

import jetbrains.interview.varcalc.interpreter.vars.Var;

import javax.annotation.Nullable;

/**
 * A storage for variables which were created during script execution.
 */
public interface VarState {
  /**
   * Returns a variable with provided name.
   * @param name name of variable
   * @return variable value or {@code null} if there is no variable with provided name
   */
  @Nullable
  Var get(String name);

  /**
   * Adds new variable with provided name to the state.
   * @param name variable name
   * @param value variable value
   * @throws jetbrains.interview.varcalc.interpreter.exceptions.ScriptExecutionException if a variable with provided name already exists in the state.
   */
  void add(String name, Var value);

  /**
   * Removes variable with provided name from the state. Does nothing if the variable does not exist.
   * @param name variable name
   */
  void remove(String name);

  /**
   * Number of variables in the state.
   * @return number of vars
   */
  int size();
}
