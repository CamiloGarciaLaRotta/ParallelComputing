package ca.mcgill.ecse420.a3;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContainsValidator {

  static int counter;
  static Lock addListLock = new ReentrantLock();
  static HashSet<Integer> mapOfAdded = new HashSet<>();

  static final int numThreads = 4;
  static final int numIterations = 50;
  static final int MAX_NUM = 5000;
  static boolean result = false;
  volatile static ConcurrentQueue<Integer> queue = new ConcurrentQueue<Integer>();

  public static void main(String[] args) {
    if (VerifyContains()) {
      System.out.println("Contains worked!");
    } else {
      System.out.println("Contains does not work.");
    }
    System.exit(0);
  }

  private static boolean VerifyContains() {

    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(numThreads);
    counter = 0;

    for (int i = 0; i < numThreads - 1; i++) {
      tasks.add(Executors.callable(new ModifierTask()));
    }
    tasks.add(Executors.callable(new Task()));

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
          System.out.println("exception " + ee.getCause());
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

    // Check queue state at the end
    // System.out.println("Final Counter value: " + counter);

    return result;
  }

  public static class ModifierTask implements Runnable {
    public ModifierTask() {

    }

    public void run() {
      for (int i = 0; i < numIterations; i++) {
        try {
          addListLock.lock();
          double rng = Math.random();

          if (Math.random() < 0.5) {
            int numToAdd = (int) (rng * MAX_NUM);
            System.out.println("Adding " + numToAdd);
            if (!mapOfAdded.contains(numToAdd)) {
              mapOfAdded.add(numToAdd);
              queue.add(numToAdd);
            }
          } else {
            Object[] numsAdded = mapOfAdded.toArray();
            System.out.println("Removing " + (Integer) numsAdded[(int) (rng * numsAdded.length)]);
            Integer numToRemove = (Integer) numsAdded[(int) (rng * numsAdded.length)];

            mapOfAdded.remove(numToRemove);
            queue.remove(numToRemove);
          }
        } finally {
          addListLock.unlock();
        }
        randomDelay(0, 2.0f);
      }
    }
  }

  public static class Task implements Runnable {

    static Lock lockUnderTest;

    public Task() {
      // Constructor
    }

    @Override
    public void run() {
      for (int i = 0; i < numIterations; i++) {
        try {
          addListLock.lock();
          Object[] numsAdded = mapOfAdded.toArray();
          result = result && queue.contains((Integer) numsAdded[(int) (Math.random() * numsAdded.length)]);
          System.out.println("Checking " + (Integer) numsAdded[(int) (Math.random() * numsAdded.length)]);

        } finally {
          addListLock.unlock();
        }

        randomDelay(0, 2.0f);
      }
    }
  }

  public static void randomDelay(float min, float max) {
    int random = (int) (max * Math.random() + min);
    try {
      Thread.sleep(random * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(0);
    }
  }
}
