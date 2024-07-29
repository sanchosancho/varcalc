package jetbrains.interview.varcalc.cli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CliLogger {
  private static final Logger LOG = LogManager.getLogger("cliLogger");

  public static Logger get() {
    return LOG;
  }
}
