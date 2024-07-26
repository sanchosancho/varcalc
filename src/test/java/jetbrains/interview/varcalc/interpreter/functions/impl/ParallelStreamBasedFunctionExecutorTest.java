package jetbrains.interview.varcalc.interpreter.functions.impl;

public class ParallelStreamBasedFunctionExecutorTest extends FunctionExecutorTest {
  public ParallelStreamBasedFunctionExecutorTest() {
    super(new StreamBasedFunctionExecutor(true));
  }
}
