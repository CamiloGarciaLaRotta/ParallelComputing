package ca.mcgill.ecse420.a2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

public class MutexValidator {

  static int counter;
  static final int numThreads = 5;
  static final int numIterations = 100;

  public static void main(String[] args) {
    System.out.println("\nThreads: " + numThreads + "\tIterations for each thread: " + numIterations + "\n");
    System.out.println("Filter lock implements mutual exclusivity: " + isValidMutex(FilterLock.class) + "\n");
    System.out.println("Bakery lock implements mutual exclusivity: " + isValidMutex(BakeryLock.class) + "\n");
    System.exit(0);
  }

  private static boolean isValidMutex(Object lockType) {

    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(numThreads);
    counter = 0;

    for (int i = 0; i < numThreads; i++) {
      tasks.add(Executors.callable(new Task(lockType)));
    }

    try {
      List<Future<Object>> futures = executor.invokeAll(tasks);
      for (Future<Object> future : futures) {
        System.out.print("Thread ended with status: ");
        try {
          future.get();
        } catch (CancellationException ce) {
          System.out.println("cancelled");
          continue;
        } catch (ExecutionException ee) {
          ee.printStackTrace();
          System.out.println("exception");
          continue;
        } catch (InterruptedException ie) {
          System.out.println("interrupted");
          continue;
        }
        System.out.println("ok");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
      executor.shutdown();
      System.exit(0);
    }

    System.out.println("Final Counter value: " + counter);

    return (counter == numIterations * numThreads);
  }

  public static class Task implements Runnable {

    static Lock lockUnderTest;

    public Task(Object lockType) {
      if (lockType.equals(FilterLock.class)) {
        Task.lockUnderTest = new FilterLock(numThreads);
      } else {
        Task.lockUnderTest = new BakeryLock(numThreads);
      }
    }

    @Override
    public void run() {
      for (int i = 0; i < numIterations; i++) {
        randomDelay(0, 1);
        lockUnderTest.lock();
        counter++;
        randomDelay(0, 1);
        lockUnderTest.unlock();
      }
    }

    private void randomDelay(float min, float max) {
      int random = (int) (max * Math.random() + min);
      try {
        Thread.sleep(random * 1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
        System.exit(0);
      }
    }
  }
}
