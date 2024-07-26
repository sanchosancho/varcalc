package jetbrains.interview.varcalc.interpreter.functions.impl;

public class ParallelFunctionExecutorTest extends FunctionExecutorTest {
  public ParallelFunctionExecutorTest() {
    super(new ParallelFunctionExecutor(4));
  }
}
