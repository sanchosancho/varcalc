package jetbrains.interview.varcalc.interpreter;

import java.io.OutputStream;

public interface VarCalcInterpreter {
  default VarState run(String script) {
    return run(script, System.out);
  }

  VarState run(String script, OutputStream output);

  VarState state();
}
