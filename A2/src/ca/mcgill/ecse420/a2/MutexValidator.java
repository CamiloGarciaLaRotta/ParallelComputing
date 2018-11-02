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
import java.util.concurrent.locks.ReentrantLock;

public class MutexValidator {

  static int counter;
  static final int numThreads = 5;
  static final int numIterations = 100;

  public static void main(String[] args) {
    System.out.println("Filter lock implements mutual exclusivity: " + isValidMutex(FilterLock.class) + "\n");
    System.out.println("Bakery lock implements mutual exclusivity: " + isValidMutex(BakeryLock.class) + "\n");
    System.exit(0);
  }

  private static boolean isValidMutex(Object lockType) {

    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(numThreads);
    counter = 0;

    for (int i=0; i<numThreads; i++) {
      tasks.add(Executors.callable(new Task(lockType)));
    }

    try {
      // executor.invokeAll(tasks);
      List<Future<Object>> futures = executor.invokeAll(tasks);
      for(Future<Object> future : futures){
        System.out.print("Thread ended with status: ");
        try{
          future.get();
        }
        catch (CancellationException ce) {
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

    Lock lockUnderTest, rangeLock;

    public Task(Object lockType) {
      rangeLock = new ReentrantLock();
      if (lockType.equals(FilterLock.class)) {
        this.lockUnderTest = new FilterLock(numThreads);
      } else {
        this.lockUnderTest = new BakeryLock(numThreads);
      }
    }

    @Override
    public void run() {
      long t0, t1;
      for (int i=0; i<numIterations; i++) {

        randomDelay(0, 1);
        // uncomment rangeLock and comment lockUnderTest to "test" the official java lock
        // lockUnderTest.lock();
        rangeLock.lock();
        t0 = System.currentTimeMillis();
        counter++;
        randomDelay(0, 1);
        // lockUnderTest.unlock();
        rangeLock.unlock();
        t1 = System.currentTimeMillis();
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
