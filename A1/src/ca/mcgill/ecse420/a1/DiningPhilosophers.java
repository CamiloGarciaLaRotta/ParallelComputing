package ca.mcgill.ecse420.a1;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {

	public static void main(String[] args) {
		int numberOfPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
		Object[] chopsticks = new Object[numberOfPhilosophers];

		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);
		ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(numberOfPhilosophers);

		for (int i=0; i<numberOfPhilosophers; i++) {
			Object leftChopstick = chopsticks[i];
            Object rightchoptsick = chopsticks[(i + 1) % chopsticks.length];
			tasks.add(Executors.callable(new Philosopher(i, leftChopstick, rightchoptsick)));
		}

		try {
			executor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
			handleShutdown();
		}
	}

	public static class Philosopher implements Runnable {

		private int philosopherNumber;
		private Object leftChopstick, rightChopstick;

		public Philosopher(int philosopherNumber, Object leftchopstick, Object rightChopstick) {
			this.philosopherNumber = philosopherNumber;
			this.leftChopstick = this.leftChopstick;
			this.rightChopstick = this.rightChopstick;
		}

		private void randomDelay(float min, float max){
			int random = (int)(max * Math.random() + min);
			try {
				Thread.sleep(random * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		@Override
		public void run() {
			while(true) {
				//think
				System.out.println("Philo #" + philosopherNumber + " is thinking");
				randomDelay(0, 5);

				//eat
				synchronized(leftChopstick) {
					System.out.println("Philo #" + philosopherNumber + " grabed left chopstick");
					synchronized(rightChopstick) {
						System.out.println("Philo #" + philosopherNumber + " grabed right chopstick");
						System.out.println("Philo #" + philosopherNumber + " is eating");
						randomDelay(0, 2);
					}
					System.out.println("Philo #" + philosopherNumber + " released right chopstick");
				}
				System.out.println("Philo #" + philosopherNumber + " released left chopstick");
			}
		}
	}

	private static void handleShutdown() {
        System.out.println("Stopped philosophers simulation");
        System.exit(0);
	}
}
