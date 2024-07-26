package jetbrains.interview.varcalc;

import jetbrains.interview.varcalc.cli.VarCalcCli;
import picocli.CommandLine;

public class VarCalcApp {
  public static void main(String[] argv) {
    final int exitCode = new CommandLine(new VarCalcCli()).execute(argv);
    System.exit(exitCode);
  }
}
