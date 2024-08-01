package jetbrains.interview.varcalc.cli;

import jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException;
import jetbrains.interview.varcalc.interpreter.exceptions.ScriptExecutionException;
import jetbrains.interview.varcalc.interpreter.impl.AntlrBasedInterpreter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "varcalc", mixinStandardHelpOptions = true, version = "0.1")
public class VarCalcCli implements Callable<Integer> {
  private static final Logger LOG = LogManager.getLogger(VarCalcCli.class);

  @CommandLine.Option(names = {"-f", "--file"}, description = "Path to file with script. If not set, stdin will be used to read script line by line.")
  private String scriptPath;

  @CommandLine.Option(names = {"-o", "--output"}, description = "Path to write script output.")
  private String outputPath;

  @CommandLine.Option(
    names = {"-n", "--num-threads"},
    description = "Maximum number of threads to use for parallel MapReduce computations. Value of 1 forces single threaded computation.",
    defaultValue = "1"
  )
  private int numThreads;

  private static OutputStream getOutputStream(String outputPath) {
    if (outputPath != null) {
      try {
        return new FileOutputStream(outputPath);
      } catch (FileNotFoundException e) {
        CliLogger.get().error("Can not write to {} : {}", outputPath, e.getMessage(), e);
        CliLogger.get().error("Stdout will be used instead");
      }
    }
    return null;
  }

  @Override
  public Integer call() throws Exception {
    if (numThreads <= 0 || numThreads > Runtime.getRuntime().availableProcessors() * 2) {
      CliLogger.get().error("Invalid number of threads, must be in range [1, {}).", Runtime.getRuntime().availableProcessors() * 2);
      return 10;
    }

    CliLogger.get().info("Starting varcalc interpreter with {} threads.", numThreads);
    try (final AntlrBasedInterpreter interpreter = new AntlrBasedInterpreter(numThreads)) {
      final boolean isInteractive = scriptPath == null;
      int lineCounter = 1;

      try (
        final InputStream scriptStream = isInteractive ? null : new FileInputStream(scriptPath);
        final OutputStream output = getOutputStream(outputPath)
      ) {
        CliLogger.get().info("Reading script from {} ...", isInteractive ? "stdin" : scriptPath);
        final Scanner scanner = new Scanner(isInteractive ? System.in : scriptStream);
        while (scanner.hasNextLine()) {
          final String line = scanner.nextLine();
          LOG.debug("Running for line {} - '{}'", lineCounter, line);
          try {
            interpreter.run(line, output != null ? output : System.out);
          } catch (ScriptExecutionException e) {
            printErrorPosition(line, lineCounter, e);
            if (!isInteractive) {
              return 1;
            }
          } finally {
            lineCounter++;
          }
        }
      } catch (FileNotFoundException e) {
        CliLogger.get().error("Can not open input file: {}", e.getMessage(), e);
        return 10;
      } catch (Exception e) {
        CliLogger.get().error("Unexpected error during script execution: {}", e.getMessage(), e);
        return 1;
      }

      return 0;
    }
  }

  private static void printErrorPosition(String line, int lineNumber, ScriptExecutionException e) {
    CliLogger.get().error(">>> {}", line);
    CliLogger.get().error("    {}^", String.format("%1$" + e.charPos() + "s", ' '));
    CliLogger.get().error("At {}:{}: {}", lineNumber, e.charPos(), e.getMessage());

    final Throwable cause = e.getCause();
    if (cause instanceof InvalidTypeException invalidType) {
      CliLogger.get().error("Reason: {}", invalidType.getMessage());
    }
  }
}
