package jetbrains.interview.varcalc.interpreter.exceptions;

public class SyntaxErrorException extends ScriptExecutionException {
  public SyntaxErrorException(String message, int charPos) {
    super(message, charPos);
  }
}
