package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ConcurrentQueue<T> {
    Node head;

    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = head;
            Node curr = pred.next;
            while (curr.key <= key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            curr.lock();
            try {
                if (validate(pred, curr)) {
                    if (curr.key == key) {
                        return false;
                    } else {
                        Node node = new Node(item);
                        node.next = curr;
                        pred.next = node;
                        return true;
                    }
                }
            } finally {
                pred.unlock();
                curr.unlock();
            }
        }
    }

    public boolean remove(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = head;
            Node curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            curr.lock();
            try {
                if (validate(pred, curr)) {
                    if (curr.key == key) {
                        pred.next = curr.next;
                        return true;
                    } else {
                        return false;
                    }
                }
            } finally {
                pred.unlock();
                curr.unlock();
            }
        }
    }

    public boolean contains(T item) {
        int key = item.hashCode();
        while (true) {
            // Search for key
            Node pred = this.head;
            Node curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            try {
                pred.lock();
                curr.lock();
                // Validate that conditions still hold
                if (validate(pred, curr)) {
                    // Return whether it's contained
                    return (curr.key == key);
                }
            } finally {
                pred.unlock();
                curr.unlock();
            }
        }
    }

    private boolean validate(Node pred, Node curr) {
        Node node = head;
        while (node.key <= pred.key) {
            if (node == pred)
                return pred.next == curr;
            node = node.next;
        }
        return false;
    }

    private class Node {
        private Lock _lock = new ReentrantLock();
        T item;
        int key;
        volatile Node next;

        public Node(T item) {
            this.item = item;
            key = item.hashCode();
        }

        public void lock() {
            _lock.lock();
        }

        public void unlock() {
            _lock.unlock();
        }

    }
}