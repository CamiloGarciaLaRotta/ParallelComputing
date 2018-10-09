package ca.mcgill.ecse420.a1;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersFair {
    public static void main(String[] args) {
        int numberOfPhilosophers = 5;
        Lock[] chopsticks = new Lock[numberOfPhilosophers];

        ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);
        ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(numberOfPhilosophers);

        for (int i = 0; i < numberOfPhilosophers; i++) {
            chopsticks[i] = new ReentrantLock();
        }

        for (int i = 0; i < numberOfPhilosophers; i++) {
            Lock leftChopstick = chopsticks[i];
            Lock rightChoptsick = chopsticks[(i + 1) % chopsticks.length];

            // code for Q3.2
            if (i == numberOfPhilosophers - 1) {
                tasks.add(Executors.callable(new Philosopher(i, rightChoptsick, leftChopstick)));
            } else {
                tasks.add(Executors.callable(new Philosopher(i, leftChopstick, rightChoptsick)));
            }
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
            executor.shutdown();
            handleShutdown();
        }
    }

    public static class Philosopher implements Runnable {

        private int philosopherNumber;
        private Lock leftChopstick, rightChopstick;

        public Philosopher(int philosopherNumber, Lock leftChopstick, Lock rightChopstick) {
            this.philosopherNumber = philosopherNumber;
            this.leftChopstick = leftChopstick;
            this.rightChopstick = rightChopstick;
        }

        @Override
        public void run() {
            while (true) {
                System.out.println("Philo #" + philosopherNumber + " is thinking");
                randomDelay(0, 2);

                leftChopstick.lock();
                try {
                    System.out.println("Philo #" + philosopherNumber + " grabed left chopstick");
                    rightChopstick.lock();
                    try {
                        System.out.println("Philo #" + philosopherNumber + " grabed right chopstick");
                        System.out.println("Philo #" + philosopherNumber + " is eating");
                        randomDelay(0, 2);
                    } finally {
                        System.out.println("Philo #" + philosopherNumber + " released right chopstick");
                        rightChopstick.unlock();
                    }
                } finally {
                    System.out.println("Philo #" + philosopherNumber + " released left chopstick");
                    leftChopstick.unlock();
                }
            }
        }

        private void randomDelay(float min, float max) {
            int random = (int) (max * Math.random() + min);
            try {
                Thread.sleep(random * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                handleShutdown();
            }
        }
    }

    private static void handleShutdown() {
        System.out.println("Stopped philosophers simulation");
        System.exit(0);
    }
}