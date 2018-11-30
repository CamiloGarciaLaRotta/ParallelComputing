package ca.mcgill.ecse420.a3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContainsValidator {

  static int counter;
  static Lock addListLock = new ReentrantLock();
  static HashSet<Integer> mapOfAdded = new HashSet<Integer>();
  static ConcurrentQueue<Integer> queue = new ConcurrentQueue<Integer>();

  static final int numThreads = 2;
  static final int numIterations = 50;
  static final int MAX_NUM = 5000;

  static boolean result = true;

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
    tasks.add(Executors.callable(new ValidationClass()));

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

    return result;
  }

  public static class ModifierTask implements Runnable {
    static final float  ADD_THRESHOLD = 0.65f;
    public ModifierTask() {
    }

    @Override
    public void run() {
      for (int i = 0; i < numIterations; i++) {
        addListLock.lock();
        double rng = Math.random();
        try {
          if (rng < ADD_THRESHOLD) {
            // Remap random variable, avoids recalculating it
            rng = rng / ADD_THRESHOLD;
            int numToAdd = (int) (rng * MAX_NUM);

            mapOfAdded.add(numToAdd);
            queue.add(numToAdd);

            System.out.println("Adding " + numToAdd);

          } else if (mapOfAdded.size() > 0) {
            // Remap random variable, avoids recalculating it
            rng = (rng - ADD_THRESHOLD) / ADD_THRESHOLD;

            int index = (int) Math.floor(rng * mapOfAdded.size());
            Integer value = (Integer) mapOfAdded.toArray()[index];

            mapOfAdded.remove(value);
            queue.remove(value);

            System.out.println("Removing " + value);
          }
        } finally {
          addListLock.unlock();
        }
        randomDelay(0.f, 2.0f);
      }
    }
  }

  static class ValidationClass implements Runnable {

    @Override
    public void run() {
      for (int i = 0; i < numIterations * 0.5; i++) {
        addListLock.lock();
        try {
          if (mapOfAdded.size() > 0) {
            int index = (int) Math.floor(Math.random() * mapOfAdded.size());
            Integer value = (Integer) mapOfAdded.toArray()[index];
            if (queue.contains(value)) {
              result = result && true;
              System.out.println("Checking ..." + value + " true");
            } else {
              result = false;
              System.out.println("Checking ..." + value + " false");
            }
          }
        } finally {
          addListLock.unlock();
        }
        randomDelay(1.0f, 1.5f);
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
