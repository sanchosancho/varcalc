package jetbrains.interview.varcalc;

import jetbrains.interview.varcalc.interpreter.VarCalcInterpreter;
import jetbrains.interview.varcalc.interpreter.VarState;
import jetbrains.interview.varcalc.interpreter.functions.impl.ParallelFunctionExecutor;
import jetbrains.interview.varcalc.interpreter.impl.AntlrBasedInterpreter;
import jetbrains.interview.varcalc.interpreter.impl.InMemoryVarState;

public class VarCalcApp {
  public static void main(String[] argv) {

    final String script = """
      var n = 5000000
      var sequence = map({0, n}, i -> (-1)^i / (2.0 * i + 1))
      var pi = 4 * reduce(sequence, 0, x y -> x + y)
      print "pi = "
      out pi
      """;

//    final String script = """
//      var a = 10
//      var b = 20
//      var c = a + b
//      out c
//      var d = c + 10.5
//      out d
//      var e = -1 * 10 ^ 2
//      out e
//      out -e
//      out (2 + 2 - 4) ^ 4
//      var m = map({1, 5}, i -> i + 2)
//      out m
//      out reduce(m, 10, i j -> i + j)
//      print "hello world 101"
//      """;

    final ParallelFunctionExecutor functionExecutor = new ParallelFunctionExecutor(8);

    final VarCalcInterpreter interpreter = new AntlrBasedInterpreter(new InMemoryVarState(), functionExecutor);

    final long ts = System.currentTimeMillis();
    try {
      final VarState state = interpreter.run(script);
      System.out.println("Time taken: " + (System.currentTimeMillis() - ts));
      System.out.println("Number of values: " + state.size());
    } finally {
      functionExecutor.close();
    }
  }
}
