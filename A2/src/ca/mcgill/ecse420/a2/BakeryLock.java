package ca.mcgill.ecse420.a2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

class BakeryLock implements Lock {

  private int numThreads;
  private boolean[] flag;
  private int[] label;


  public BakeryLock(int numThreads) {
    this.numThreads = numThreads;
    this.flag = new boolean[numThreads];
    this.label = new int[numThreads];
  }

  @Override
  public void lock() {
    int me = (int) Thread.currentThread().getId() % numThreads;
    flag[me] = true;
    label[me] = getMax(label) + 1;

    for (int k=0; k<numThreads; k++) {
      while (k != me && flag[k] && label[me] > label[k] && me > k) {}
    }
  }

  private int getMax(int[] arr) {

    int max = Integer.MIN_VALUE;
    for (int i=0; i<arr.length; i++) {
      if (arr[i] > max) { max = arr[i]; }
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
