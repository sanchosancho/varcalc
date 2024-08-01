package jetbrains.interview.varcalc.interpreter;

import java.io.OutputStream;

/**
 * An interpreter that provides execution of scripts written in VarCalc grammar.
 * Detailed specification of grammar can be found in
 * <a href="file:src/main/antlr4/jetbrains/interview/varcalc/parser/VarCalc.g4">VarCalc.g4</a>.
 *
 * <p>Example script:
 * <br>{@code var n = 500}
 * <br>{@code var sequence = map({0, n}, i -> (-1)^i / (2.0 * i + 1))}
 * <br>{@code var pi = 4 * reduce(sequence, 0, x y -> x + y)}
 * <br>{@code print "pi = "}
 * <br>{@code out pi}
 */
public interface VarCalcInterpreter {
  /**
   * Runs provided script and writes output to stdout.
   * @param script input script, single or multi line
   * @return state after the execution
   * @throws jetbrains.interview.varcalc.interpreter.exceptions.ScriptExecutionException if execution of script
   *         encounters an error due to mismatching types, invalid declarations, failed computations etc.
   * @throws jetbrains.interview.varcalc.interpreter.exceptions.SyntaxErrorException if parsing of script fails due to invalid syntax.
   */
  @SuppressWarnings("UnusedReturnValue")
  default VarState run(String script) {
    return run(script, System.out);
  }

  /**
   * Runs provided script and writes output to stdout.
   * @param script input script, single or multi line
   * @param output {@link OutputStream} to write output to
   * @return state after the execution
   * @throws jetbrains.interview.varcalc.interpreter.exceptions.ScriptExecutionException if execution of script
   *         encounters an error due to mismatching types, invalid declarations, failed computations etc.
   * @throws jetbrains.interview.varcalc.interpreter.exceptions.SyntaxErrorException if parsing of script fails due to invalid syntax.
   */
  VarState run(String script, OutputStream output);

  /**
   * Returns current state of the interpreter.
   * @return current state
   */
  VarState state();
}
