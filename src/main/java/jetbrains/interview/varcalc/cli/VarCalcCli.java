package jetbrains.interview.varcalc.cli;

import jetbrains.interview.varcalc.interpreter.impl.AntlrBasedInterpreter;
import picocli.CommandLine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "varcalc")
public class VarCalcCli implements Callable<Integer> {

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
        System.err.println("Can not write to " + outputPath + " : " + e.getMessage());
        System.err.println("Stdout will be used instead");
      }
    }
    return null;
  }

  @Override
  public Integer call() throws Exception {
    if (numThreads <= 0 || numThreads > Runtime.getRuntime().availableProcessors() * 2) {
      System.err.println("Invalid number of threads, must be in range [1, " + Runtime.getRuntime().availableProcessors() * 2 + ").");
      return 1;
    }

    try (final AntlrBasedInterpreter interpreter = new AntlrBasedInterpreter(numThreads)) {
      try (
        final InputStream input = scriptPath != null ? new FileInputStream(scriptPath) : null;
        final OutputStream output = getOutputStream(outputPath)
      ) {
        final Scanner scanner = input != null ? new Scanner(input) : new Scanner(System.in);
        while (scanner.hasNextLine()) {
          interpreter.run(scanner.nextLine(), output != null ? output : System.out);
        }
      } catch (FileNotFoundException e) {
        System.err.println("Not found input: " + e.getMessage());
        return 10;
      } catch (Exception e) {
        System.err.println("Unexpected error during script execution: " + e.getMessage());
        return 1;
      }

      return 0;
    }
  }
}
