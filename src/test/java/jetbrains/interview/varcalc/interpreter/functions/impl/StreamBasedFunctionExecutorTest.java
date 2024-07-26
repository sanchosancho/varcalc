package jetbrains.interview.varcalc.interpreter.functions.impl;

public class StreamBasedFunctionExecutorTest extends FunctionExecutorTest {
  public StreamBasedFunctionExecutorTest() {
    super(new StreamBasedFunctionExecutor(false));
  }
}