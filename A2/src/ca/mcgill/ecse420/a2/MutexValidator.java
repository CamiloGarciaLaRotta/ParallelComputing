package ca.mcgill.ecse420.a2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MutexValidator {

  static final int numThreads = 10;
  static final int numIterations = 1000;
  static List<Range> ranges = new ArrayList<Range>();;
  public static void main(String[] args) {
    System.out.println("Filter lock implements mutual exclusivity: " + isValidMutex(FilterLock.class));
    System.out.println("Bakery lock implements mutual exclusivity: " + isValidMutex(BakeryLock.class));
  }

  private static boolean isValidMutex(Object lockType) {

    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(numThreads);

    for (int i = 0; i < numThreads; i++) {
      tasks.add(Executors.callable(new Task(lockType)));
    }

    try {
      executor.invokeAll(tasks);
    } catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(0);
    } finally {
      executor.shutdown();
    }

    for (int i=0; i<ranges.size(); i++) {
      for (int j=0; j<ranges.size(); j++) {
        if (i != j && Range.overlap(ranges.get(i), ranges.get(j))) {
          return false;
        }
      }
    }
    return true;
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
      for (int i=0; i<numIterations; i++) {

        randomDelay(0, 5);
        lockUnderTest.lock();
        long t0 = System.currentTimeMillis();
        randomDelay(0, 5);
        lockUnderTest.unlock();
        long t1 = System.currentTimeMillis();

        rangeLock.lock();
        ranges.add(new Range(t0, t1));
        rangeLock.unlock();
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
