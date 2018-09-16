package ca.mcgill.ecse420.a1;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Deadlock {
    public static void main(String[] args) {

        final Object soapDispenser = new Object();
        final Object spongeDispenser = new Object();

        class WashWindowsTask implements Runnable {
            @Override
            public void run() {

                randomDelay(0, 2);
                synchronized(soapDispenser) {
                    randomDelay(0, 2);
                    System.out.println("WINDOW WASHER:\tObtaining soap");

                    synchronized(spongeDispenser) {
                        randomDelay(0, 2);
                        System.out.println("WINDOW WASHER:\tObtaining sponge");
                        System.out.println("WINDOW WASHER:\tWashing windows");
                    }
                    System.out.println("WINDOW WASHER:\tReleasing sponge");
                }
                System.out.println("WINDOW WASHER:\tReleasing soap");
            }

            private void randomDelay(float min, float max){
                int random = (int)(max * Math.random() + min);
                try {
                    Thread.sleep(random * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        class WashFloorTask implements Runnable {

            @Override
            public void run() {

                randomDelay(0, 2);
                synchronized(spongeDispenser) {
                    randomDelay(0, 2);
                    System.out.println("FLOOR WASHER:\tObtaining sponge");

                    synchronized(soapDispenser) {
                        randomDelay(0, 2);
                        System.out.println("FLOOR WASHER:\tObtaining soap");
                        System.out.println("FLOOR WASHER:\tWashing floor");
                    }
                    System.out.println("FLOOR WASHER:\tReleasing soap");
                }
                System.out.println("FLOOR WASHER:\tReleasing sponge");
            }

            private void randomDelay(float min, float max){
                int random = (int)(max * Math.random() + min);
                try {
                    Thread.sleep(random * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);
        ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(2);
        tasks.add(Executors.callable(new WashWindowsTask()));
        tasks.add(Executors.callable(new WashFloorTask()));

        while (true) {
            try {
                executor.invokeAll(tasks);
            } catch (InterruptedException e) {
                executor.shutdown();
                handleShutdown();
            }
        }
    }

    private static void handleShutdown() {
        System.out.println("Stopped deadlock simulation");
        System.exit(0);
    }
}

