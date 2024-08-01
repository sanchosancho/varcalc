package jetbrains.interview.varcalc.interpreter.impl;

import jetbrains.interview.varcalc.interpreter.VarCalcInterpreter;
import jetbrains.interview.varcalc.interpreter.VarState;
import jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException;
import jetbrains.interview.varcalc.interpreter.exceptions.ScriptExecutionException;
import jetbrains.interview.varcalc.interpreter.exceptions.SyntaxErrorException;
import jetbrains.interview.varcalc.interpreter.functions.FunctionExecutor;
import jetbrains.interview.varcalc.interpreter.functions.impl.ParallelFunctionExecutor;
import jetbrains.interview.varcalc.interpreter.functions.impl.StreamBasedFunctionExecutor;
import jetbrains.interview.varcalc.interpreter.output.PrinterRegistry;
import jetbrains.interview.varcalc.interpreter.vars.Double;
import jetbrains.interview.varcalc.interpreter.vars.Integer;
import jetbrains.interview.varcalc.interpreter.vars.Interval;
import jetbrains.interview.varcalc.interpreter.vars.Numeric;
import jetbrains.interview.varcalc.interpreter.vars.Sequential;
import jetbrains.interview.varcalc.interpreter.vars.TypeTraits;
import jetbrains.interview.varcalc.interpreter.vars.Var;
import jetbrains.interview.varcalc.parser.VarCalcBaseVisitor;
import jetbrains.interview.varcalc.parser.VarCalcLexer;
import jetbrains.interview.varcalc.parser.VarCalcParser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BinaryOperator;

public class AntlrBasedInterpreter implements VarCalcInterpreter, AutoCloseable {
  private static final Logger LOG = LogManager.getLogger(AntlrBasedInterpreter.class);

  private final VarState state;
  private final FunctionExecutor functionExecutor;

  public AntlrBasedInterpreter(int numThreads) {
    this.state = new InMemoryVarState();
    this.functionExecutor = numThreads > 1
      ? new ParallelFunctionExecutor(numThreads)
      : new StreamBasedFunctionExecutor(false);
  }

  @Override
  public void close() throws Exception {
    functionExecutor.close();
  }

  @Override
  public VarState run(String script, OutputStream output) {
    final VarCalcLexer lexer = new VarCalcLexer(CharStreams.fromString(script));
    final VarCalcParser parser = new VarCalcParser(new CommonTokenStream(lexer));

    parser.removeErrorListeners();
    parser.addErrorListener(new ErrorListener());

    final ExecutionVisitor executionVisitor = new ExecutionVisitor(state, output);
    executionVisitor.visit(parser.script());

    return state;
  }

  @Override
  public VarState state() {
    return state;
  }

  static class ErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(
      Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line,
      int charPositionInLine,
      String msg,
      RecognitionException e
    ) {
      throw new SyntaxErrorException("Invalid syntax – " + msg, charPositionInLine);
    }
  }

  class ExecutionVisitor extends VarCalcBaseVisitor<Var> {
    private final VarState state;
    private final OutputStream output;

    public ExecutionVisitor(VarState state, OutputStream output) {
      this.state = state;
      this.output = output;
    }

    @Override
    public Var visitVarDecl(VarCalcParser.VarDeclContext ctx) {
      final String name = ctx.ID().getText();
      final Var var = this.visit(ctx.expr());

      state.add(name, var);
      return var;
    }

    @Override
    public Var visitPrintExpr(VarCalcParser.PrintExprContext ctx) {
      final Var var = this.visit(ctx.expr());
      try {
        PrinterRegistry.print(output, var);
        output.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
      } catch (IOException e) {
        LOG.error("Failed to write output: {}", e.getMessage(), e);
      }
      return super.visitPrintExpr(ctx);
    }

    @Override
    public Var visitPrintString(VarCalcParser.PrintStringContext ctx) {
      try {
        final String str = trimQuotes(ctx.QUOTED_STRING().getText());
        output.write(str.getBytes(StandardCharsets.UTF_8));
      } catch (IOException e) {
        LOG.error("Failed to write output: {}", e.getMessage(), e);
      }
      return super.visitPrintString(ctx);
    }

    @Override
    public Var visitPow(VarCalcParser.PowContext ctx) {
      return performBinaryArithmeticOp(ctx.base, ctx.exp, ctx.op.getType());
    }

    @Override
    public Var visitMul(VarCalcParser.MulContext ctx) {
      return performBinaryArithmeticOp(ctx.left, ctx.right, ctx.op.getType());
    }

    @Override
    public Var visitSum(VarCalcParser.SumContext ctx) {
      return performBinaryArithmeticOp(ctx.left, ctx.right, ctx.op.getType());
    }

    @Override
    public Var visitNegation(VarCalcParser.NegationContext ctx) {
      return readValue(ctx.expr(), Numeric.class).negate();
    }

    @Override
    public Var visitGroup(VarCalcParser.GroupContext ctx) {
      return visit(ctx.expr());
    }

    @Override
    public Double visitNumber(VarCalcParser.NumberContext ctx) {
      final String textValue = ctx.NUMBER().getText();
      try {
        return new Double(java.lang.Double.parseDouble(textValue));
      } catch (NumberFormatException e) {
        throw new ScriptExecutionException(
          "Can not parse Double value from " + textValue,
          ctx.getStart().getCharPositionInLine()
        );
      }
    }

    @Override
    public Integer visitInteger(VarCalcParser.IntegerContext ctx) {
      final String textValue = ctx.INTEGER().getText();
      try {
        return new Integer(java.lang.Integer.parseInt(textValue));
      } catch (NumberFormatException e) {
        throw new ScriptExecutionException(
          "Can not parse Integer value from " + textValue,
          ctx.getStart().getCharPositionInLine()
        );
      }
    }

    @Override
    public Sequential visitSequence(VarCalcParser.SequenceContext ctx) {
      try {
        return new Interval(
          readValue(ctx.begin, Integer.class).value(),
          readValue(ctx.end, Integer.class).value()
        );
      } catch (InvalidTypeException e) {
        throw new ScriptExecutionException("Can not construct Interval", e, ctx.getStart().getCharPositionInLine());
      }
    }

    public Numeric visitLambda(VarCalcParser.LambdaContext ctx, Var... vars) {
      final List<String> args = ctx.ID().stream().map(TerminalNode::getText).toList();
      for (int i = 0; i < args.size(); i++) {
        state.add(args.get(i), vars[i]);
      }
      try {
        return readValue(ctx.expr(), Numeric.class);
      } finally {
        args.forEach(state::remove);
      }
    }

    @Override
    public Var visitMap(VarCalcParser.MapContext ctx) {
      final VarCalcParser.LambdaContext lambda = ctx.lambda();
      final Sequential sequence = readValue(ctx.seq, Sequential.class);
      final ExecutionVisitor lambdaExecutor = new ExecutionVisitor(new ThreadLocalVarState(), output);

      return functionExecutor.map(sequence, numeric -> lambdaExecutor.visitLambda(lambda, numeric));
    }

    @Override
    public Var visitReduce(VarCalcParser.ReduceContext ctx) {
      final VarCalcParser.LambdaContext lambda = ctx.lambda();
      final Sequential sequence = readValue(ctx.seq, Sequential.class);
      final Numeric identity = readValue(ctx.identity, Numeric.class);
      final ExecutionVisitor lambdaVisitor = new ExecutionVisitor(new ThreadLocalVarState(), output);

      return functionExecutor.reduce(sequence, identity, (n1, n2) -> lambdaVisitor.visitLambda(lambda, n1, n2));
    }

    @Override
    public Var visitId(VarCalcParser.IdContext ctx) {
      final String varId = ctx.getText();
      final Var var = state.get(varId);
      if (var == null) {
        throw new ScriptExecutionException("Undefined variable: " + varId, ctx.getStart().getCharPositionInLine());
      }
      return var;
    }

    private Var performBinaryArithmeticOp(
      VarCalcParser.ExprContext left,
      VarCalcParser.ExprContext right,
      int opType
    ) {
      return switch (opType) {
        case VarCalcParser.POWER          -> performBinaryArithmeticOp(left, right, Numeric::pow);
        case VarCalcParser.MULTIPLICATION -> performBinaryArithmeticOp(left, right, Numeric::multiply);
        case VarCalcParser.DIVISION       -> performBinaryArithmeticOp(left, right, Numeric::divide);
        case VarCalcParser.PLUS           -> performBinaryArithmeticOp(left, right, Numeric::add);
        case VarCalcParser.MINUS          -> performBinaryArithmeticOp(left, right, Numeric::subtract);
        default -> throw new ScriptExecutionException("Unknown operation type " + opType, right.getStart().getCharPositionInLine());
      };
    }

    private Numeric performBinaryArithmeticOp(
      VarCalcParser.ExprContext left,
      VarCalcParser.ExprContext right,
      BinaryOperator<Numeric> op
    ) {
      try {
        return TypeTraits.performBinaryOperation(
          readValue(left, Numeric.class),
          readValue(right, Numeric.class),
          op
        );
      } catch (ArithmeticException e) {
        throw new ScriptExecutionException("Arithmetic error: " + e.getMessage(), e, left.getStart().getCharPositionInLine());
      }
    }

    private static String trimQuotes(String str) {
      if (str == null) {
        return "";
      } else if (str.length() == 0) {
        return str;
      } else {
        return str.substring(
          str.charAt(0) == '"' ? 1 : 0,
          str.charAt(str.length() - 1) == '"' ? str.length() - 1 : str.length()
        );
      }
    }

    private <T extends Var> T readValue(ParserRuleContext ctx, Class<T> cls) {
      try {
        return TypeTraits.cast(visit(ctx), cls);
      } catch (InvalidTypeException e) {
        throw new ScriptExecutionException("Incompatible type", e, ctx.getStart().getCharPositionInLine());
      }
    }
  }
}
