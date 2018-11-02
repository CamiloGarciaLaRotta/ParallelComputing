package ca.mcgill.ecse420.a2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

class FilterLock implements Lock {

  int numThreads;
  private int[] level, victim;

  public FilterLock(int numThreads) {
    this.numThreads = numThreads;
    this.level = new int[numThreads];
    this.victim = new int[numThreads];
  }

  @Override
  public void lock() {
    int me = (int) Thread.currentThread().getId() % numThreads;
    for (int L=1; L<numThreads; L++) {
      level[me] = L;
      victim[L] = me;

      for (int k=0; k<numThreads; k++) {
        while (k != me && level[k] >= level[me] && victim[L] == me) {}
      }
    }
  }

  @Override
  public void unlock() {
    int me = (int) Thread.currentThread().getId() % numThreads;
    level[me] = 0;
  }

  // the following methods are not relevant to the scope of this question.
  // they exists simply to satisfy the Lock interface.

  @Override
  public void lockInterruptibly() throws InterruptedException {
    return;
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
