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
  static HashSet<Integer> mapOfRemoved = new HashSet<Integer>();
  static ConcurrentQueue<Integer> queue = new ConcurrentQueue<Integer>();

  static final int numThreads = 2;
  static final int numIterations = 50;
  static final int MAX_NUM = 5000;

  static boolean result = true;

  public static void main(String[] args) {
    boolean result;
    if (Verify(false) && Verify(true)) {
      System.out.println("Contains worked!");
    } else {
      System.out.println("Contains does not work.");
    }
    System.exit(0);
  }

  private static boolean Verify(boolean remove) {

    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(numThreads);
    counter = 0;

    for (int i = 0; i < numThreads - 1; i++) {
      tasks.add(Executors.callable(remove ? new RemoveTask() : new AddTask()));
    }
    tasks.add(Executors.callable(new ValidationClass(remove)));

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

  public static class RemoveTask implements Runnable {
    public RemoveTask() {
    }

    @Override
    public void run() {
      for (int i = 0; i < numIterations; i++) {
        double rng = Math.random();
        if (mapOfAdded.size() > 0) {
          int index = (int) Math.floor(rng * mapOfAdded.size());
          Integer value = (Integer) mapOfAdded.toArray()[index];

          queue.remove(value);

          addListLock.lock();
          mapOfAdded.remove(value);
          mapOfRemoved.add(value);
          addListLock.unlock();

          System.out.println("Removing " + value);
        }

        randomDelay(0.5f, 2.0f);
      }
    }
  }

  public static class AddTask implements Runnable {
    public AddTask() {
    }

    @Override
    public void run() {
      for (int i = 0; i < numIterations; i++) {
        double rng = Math.random();
        try {
          // Remap random variable, avoids recalculating it
          int numToAdd = (int) (rng * MAX_NUM);
          queue.add(numToAdd);
          addListLock.lock();
          mapOfAdded.add(numToAdd);
          addListLock.unlock();
          System.out.println("Adding " + numToAdd);
        } finally {
          // Unlock just in case there was an exception when adding to the map
        }
        randomDelay(0.5f, 2.0f);
      }
    }
  }

  static class ValidationClass implements Runnable {
    public boolean checkRemove = false;

    public ValidationClass(boolean remove)
    {
      checkRemove = remove;
    }

    @Override
    public void run() {
      for (int i = 0; i < numIterations; i++) {
          if(!checkRemove)
          {
            if (mapOfAdded.size() > 0) {
              addListLock.lock();
              int index = (int) Math.floor(Math.random() * mapOfAdded.size());
              Integer value = (Integer) mapOfAdded.toArray()[index];
              addListLock.unlock();
              if (queue.contains(value)) {
                result = result && true;
                System.out.println("Checking added " + value + " true");
              } else {
                result = false;
                System.out.println("Checking added " + value + " false");
              }
            }
          }else
          {
            if (mapOfRemoved.size() > 0) {
              addListLock.lock();
              int index = (int) Math.floor(Math.random() * mapOfRemoved.size());
              Integer value = (Integer) mapOfRemoved.toArray()[index];
              addListLock.unlock();
              if (!queue.contains(value)) {
                result = result && true;
                System.out.println("Checking removed " + value + " true");
              } else {
                result = false;
                System.out.println("Checking removed " + value + " false");
              }
            }
          }
        randomDelay(0.5f, 1.5f);
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
