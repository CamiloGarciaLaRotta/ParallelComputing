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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContainsValidator {

  static int counter;
  static Lock addListLock = new ReentrantLock(); 

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
      tasks.add(Executors.callable(new ModifierTask(false)));
    }
    tasks.add(Executors.callable(new ModifierTask(true)));

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
    static Lock _lock = new ReentrantLock();
    static HashSet<Integer> mapOfAdded = new HashSet<Integer>();
    boolean isValidator = false;
    static ConcurrentQueue<Integer> queue = new ConcurrentQueue<Integer>();

    public ModifierTask(boolean validator) {
      isValidator = validator;
      
    }

    @Override
    public void run() {
      for (int i = 0; i < numIterations; i++) {
        _lock.lock();
        try {
          if (isValidator && mapOfAdded.size() > 0) {
            Object[] numsAdded = mapOfAdded.toArray();
            int index = (int)Math.floor(Math.random() * numsAdded.length);
            Integer value = (Integer) numsAdded[index];
            boolean localResult = queue.contains(value);
            result = result && localResult;
            System.out.println("Checking ..." + localResult);
          } else {
            double rng = Math.random();
            if (Math.random() < 0.5 || mapOfAdded.size() == 0) {
              int numToAdd = (int) (rng * MAX_NUM);
              System.out.println("Adding " + numToAdd);

              if (!mapOfAdded.contains(new Integer(numToAdd))) {
                mapOfAdded.add(numToAdd);
                queue.add(numToAdd);
              }
            } else {
              System.out.println("trying to remove");
              Object[] numsAdded = mapOfAdded.toArray();
              int index = (int)Math.floor(rng * numsAdded.length);
              Integer value = (Integer) numsAdded[index];
              System.out.println("Removing " + value);

              mapOfAdded.remove(value);
              queue.remove(value);
            }
          }

        } finally {
          _lock.unlock();
        }
        randomDelay(0, 2.0f);
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

}
