package ca.mcgill.ecse420.a3;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BakeryLock implements Lock {

  static private int numThreads;
  static public boolean[] flag;
  static public int[] label;
  static private Lock lock;

  public BakeryLock(int numThr) {
    numThreads = numThr;
    flag = new boolean[numThreads];
    label = new int[numThreads];
    lock = new ReentrantLock();
  }

  @Override
  public void lock() {
    int me = (int) Thread.currentThread().getId() % numThreads;

    flag[me] = true;

    lock.lock();
    label[me] = getMax(label) + 1;
    lock.unlock();

    for (int k = 0; k < numThreads; k++) {
      while (k != me && flag[k] && ((label[k] < label[me]) || ((label[k] == label[me]) && k < me))) {
      }
    }
  }

  private int getMax(int[] arr) {

    int max = Integer.MIN_VALUE;
    for (int i = 0; i < arr.length; i++) {
      if (arr[i] > max) {
        max = arr[i];
      }
    }

    return max;
  }

  @Override
  public void unlock() {
    int me = (int) Thread.currentThread().getId() % numThreads;
    flag[me] = false;
  }

  // the following methods are not relevant to the scope of this question.
  // they exists simply to satisfy the Lock interface.

  @Override
  public void lockInterruptibly() throws InterruptedException {

  }

  @Override
  public boolean tryLock() {
    return false;
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    return false;
  }

  @Override
  public Condition newCondition() {
    return null;
  }
}
