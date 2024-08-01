package jetbrains.interview.varcalc.interpreter.exceptions;

public class ScriptExecutionException extends RuntimeException {
  private final int charPos;

  public ScriptExecutionException(String message) {
    this(message, 0);
  }

  public ScriptExecutionException(String message, int charPos) {
    super(message);
    this.charPos = charPos;
  }

  public ScriptExecutionException(String message, Throwable cause, int charPos) {
    super(message, cause);
    this.charPos = charPos;
  }

  public int charPos() {
    return charPos;
  }
}
