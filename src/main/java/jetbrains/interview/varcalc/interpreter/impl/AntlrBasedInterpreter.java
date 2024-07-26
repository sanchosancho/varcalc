package jetbrains.interview.varcalc.interpreter.impl;

import jetbrains.interview.varcalc.interpreter.VarCalcInterpreter;
import jetbrains.interview.varcalc.interpreter.VarState;
import jetbrains.interview.varcalc.interpreter.exceptions.InvalidTypeException;
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
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.BinaryOperator;

public class AntlrBasedInterpreter extends VarCalcBaseVisitor<Var> implements VarCalcInterpreter, AutoCloseable {
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

    final ExecutionVisitor executionVisitor = new ExecutionVisitor(output);
    executionVisitor.visit(parser.script());

    return state;
  }

  @Override
  public VarState state() {
    return state;
  }

  class ExecutionVisitor extends VarCalcBaseVisitor<Var> {
    private final OutputStream output;

    public ExecutionVisitor(OutputStream output) {
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
        output.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
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
      return TypeTraits.cast(visit(ctx.expr()), Numeric.class).negate();
    }

    @Override
    public Var visitGroup(VarCalcParser.GroupContext ctx) {
      return visit(ctx.expr());
    }

    @Override
    public Double visitNumber(VarCalcParser.NumberContext ctx) {
      // todo: catch NumberFormatException
      return new Double(java.lang.Double.parseDouble(ctx.NUMBER().getText()));
    }

    @Override
    public Integer visitInteger(VarCalcParser.IntegerContext ctx) {
      return new Integer(java.lang.Integer.parseInt(ctx.INTEGER().getText()));
    }

    @Override
    public Sequential visitSequence(VarCalcParser.SequenceContext ctx) {
      return new Interval(
        TypeTraits.cast(visit(ctx.begin), Integer.class).value(),
        TypeTraits.cast(visit(ctx.end), Integer.class).value()
      );
    }

    @Override
    public Var visitLambda(VarCalcParser.LambdaContext ctx) {
      return super.visitLambda(ctx);
    }

    @Override
    public Var visitFunctionCall(VarCalcParser.FunctionCallContext ctx) {
      final String functionName = ctx.functionName().getText();
      if ("map".equals(functionName)) {
        final VarCalcParser.LambdaContext lambda = ctx.getRuleContext(VarCalcParser.LambdaContext.class, 0);
        final String lambdaArgName = lambda.ID(0).getText();

        return functionExecutor.map(
          TypeTraits.cast(visit(ctx.expr(0)), Sequential.class),
          numeric -> {
            state.add(lambdaArgName, numeric, VarState.Scope.THREAD_LOCAL);
            try {
              return TypeTraits.cast(visit(lambda.expr()), Numeric.class);
            } finally {
              state.remove(lambdaArgName, VarState.Scope.THREAD_LOCAL);
            }
          }
        );
      } else if ("reduce".equals(functionName)) {
        final VarCalcParser.LambdaContext lambda = ctx.getRuleContext(VarCalcParser.LambdaContext.class, 0);
        final String left = lambda.ID(0).getText();
        final String right = lambda.ID(1).getText();

        return functionExecutor.reduce(
          TypeTraits.cast(visit(ctx.expr(0)), Sequential.class),
          TypeTraits.cast(visit(ctx.expr(1)), Numeric.class),
          (n1, n2) -> {
            state.add(left, n1, VarState.Scope.THREAD_LOCAL);
            state.add(right, n2, VarState.Scope.THREAD_LOCAL);
            try {
              return TypeTraits.cast(visit(lambda.expr()), Numeric.class);
            } finally {
              state.remove(left, VarState.Scope.THREAD_LOCAL);
              state.remove(right, VarState.Scope.THREAD_LOCAL);
            }
          }
        );
      } else {
        throw new InvalidTypeException("Undefined function call: " + functionName);
      }
    }

    @Override
    public Var visitId(VarCalcParser.IdContext ctx) {
      final String varId = ctx.getText();
      final Var var = state.get(varId);
      if (var == null) {
        throw new IllegalArgumentException("Undefined variable: " + varId);
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
        default -> throw new InvalidTypeException("Unknown operation type " + opType);
      };
    }

    private Numeric performBinaryArithmeticOp(
      VarCalcParser.ExprContext left,
      VarCalcParser.ExprContext right,
      BinaryOperator<Numeric> op
    ) {
      return TypeTraits.performBinaryOperation(
        TypeTraits.cast(visit(left), Numeric.class),
        TypeTraits.cast(visit(right), Numeric.class),
        op
      );
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
  }
}
