package jetbrains.interview.varcalc.interpreter.functions.impl;

import jetbrains.interview.varcalc.interpreter.exceptions.ScriptExecutionException;
import jetbrains.interview.varcalc.interpreter.functions.FunctionExecutor;
import jetbrains.interview.varcalc.interpreter.vars.Numeric;
import jetbrains.interview.varcalc.interpreter.vars.NumericArray;
import jetbrains.interview.varcalc.interpreter.vars.Sequential;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class ParallelFunctionExecutor implements FunctionExecutor {
  private final int numThreads;
  private final ExecutorService executorService;

  public ParallelFunctionExecutor(int numThreads) {
    this.numThreads = numThreads;
    this.executorService = Executors.newFixedThreadPool(numThreads);
  }

  @Override
  public void close() {
    executorService.shutdown();
  }

  @Override
  public Sequential map(Sequential sequence, Function<Numeric, Numeric> mapper) {
    final int size = sequence.size();
    final Numeric[] result = new Numeric[size];

    final var futures = submitTasks(size, (begin, end) -> {
      for (int j = begin; j < end; j++) {
        result[j] = mapper.apply(sequence.get(j));
      }
      return 0;
    });

    awaitTaskResults(futures);
    return new NumericArray(result);
  }

  @Override
  public Numeric reduce(Sequential sequence, Numeric identity, BinaryOperator<Numeric> reducer) {
    final var futures = submitTasks(sequence.size(), (begin, end) -> {
      Numeric accum = sequence.get(begin);
      for (int j = begin + 1; j < end; j++) {
        final Numeric u = sequence.get(j);
        if (u == null) {
          break;
        }
        accum = reducer.apply(accum, u);
      }
      return accum;
    });

    return awaitTaskResults(futures).stream().reduce(identity, reducer);
  }

  private <T> List<Future<T>> submitTasks(int size, BiFunction<Integer, Integer, T> subTask) {
    final int partSize = size / numThreads + Math.min(size % numThreads, 1);
    final List<Future<T>> futures = new ArrayList<>(numThreads);

    for (int i = 0; i < size; i += partSize) {
      final int begin = i;
      final int end = Math.min(i + partSize, size);
      futures.add(executorService.submit(() -> subTask.apply(begin, end)));
    }
    return futures;
  }

  private <T> List<T> awaitTaskResults(List<Future<T>> futures) {
    try {
      final List<T> results = new ArrayList<>(futures.size());
      for (Future<T> future : futures) {
        results.add(future.get());
      }
      return results;
    } catch (ExecutionException e) {
      throw new ScriptExecutionException("Function call failed", e.getCause(), 0);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ScriptExecutionException("Script execution was interrupted");
    }
  }
}
