package ca.mcgill.ecse420.a3;

import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeBoundedQueue {

    private Object[] objects;
    private AtomicInteger head = new AtomicInteger(0), tail = new AtomicInteger(0);
    private AtomicInteger tailCommit = new AtomicInteger(0);
    private AtomicInteger latentCapacity;

    // constructor
    public LockFreeBoundedQueue(int capacity) {
        this.objects = new Object[capacity];
        this.latentCapacity = new AtomicInteger(capacity);
    }

    /**
     * Thread-safe enqueue the object.
     * Blocks while queue is full.
     * @param item the item to enqueue
     */
    public void enqueue(Object item) {
        int lc = latentCapacity.get();

        // block until not full
        while (lc <= 0 || !latentCapacity.compareAndSet(lc, lc - 1)) {
          lc = latentCapacity.get();
        }

        // enqueue the object
        int t = tail.getAndIncrement();
        objects[t % objects.length] = item;

        // make tail refer to last node
        while (tailCommit.compareAndSet(t, t + 1)) { };
    }

    /**
     * Thread-safe dequeue the first object.
     * Blocks if queue is empty.
     * @return the first item of the queue
     */
    public Object dequeue() {

        int h = head.getAndIncrement();

        // block while empty
        while (h >= tailCommit.get()) {};

        // dequeue the object
        Object item = objects[h % objects.length];
        latentCapacity.incrementAndGet();

        return item;
    }
}

