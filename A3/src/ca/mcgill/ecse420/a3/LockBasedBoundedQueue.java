package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedBoundedQueue {

  private Object[] objects;
  private int head_idx = 0, tail_idx = 0;
  private Lock head_lock = new ReentrantLock(), tail_lock = new ReentrantLock();
  private Condition notEmpty = head_lock.newCondition(), notFull  = tail_lock.newCondition();

  // constructor
  public LockBasedBoundedQueue(int capacity) {
    this.objects = new Object[capacity];
  }

  /**
  * Thread-safely enqueue an object.
  * Blocks if the queue is full.
  * @param item the object to enqueue
  */
  public void enqueue(Object item) {
    tail_lock.lock();
    try {
      // block until not full
      while (tail_idx - head_idx == objects.length) {
        try {
          notFull.await();
        }
        catch (Exception e) {}
      }

      // enqueue the object
      objects[tail_idx % objects.length] = item;
      tail_idx++;

      // signal if not empty
      if (tail_idx - head_idx == 1) {
        notEmpty.signal();
      }
    } finally {
      tail_lock.unlock();
    }
  }

  /**
  * Thread-safe dequeue the first item of the queue.
  * Blocks if the queue is empty.
  * @return the first item of the queue
  */
  public Object dequeue() {
    head_lock.lock();
    try {
      // lock until there is an object
      while (tail_idx - head_idx == 0) {
        try {
          notEmpty.await();
        } catch (Exception e) {}
      }

      // dequeue the object
      Object x = objects[head_idx % objects.length];
      head_idx++;

      // signal if not full
      if (tail_idx - head_idx == objects.length - 1) {
        notFull.signal();
      }

      return x;
    } finally {
      head_lock.unlock();
    }
  }
}
