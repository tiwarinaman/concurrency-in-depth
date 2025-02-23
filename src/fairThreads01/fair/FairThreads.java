package fairThreads01.fair;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FairThreads {

    private static final int MAX_INT = 100000000;
    private static final int CONCURRENCY = 10;
    private static final AtomicInteger totalPrimeNumbers = new AtomicInteger(0);
    private static final AtomicInteger currentNum = new AtomicInteger(2);

    public static void checkPrime(int x) {
        if ((x & 1) == 0) {
            return;
        }
        for (int i = 3; i <= Math.sqrt(x); i += 2) {
            if (x % i == 0) {
                return;
            }
        }
        totalPrimeNumbers.incrementAndGet();
    }

    private static void doWork(String name) {
        final long startTime = System.nanoTime();
        while (true) {
            int number = currentNum.incrementAndGet();
            if (number > MAX_INT) {
                break;
            }
            checkPrime(number);
        }
        final long duration = System.nanoTime() - startTime;
        System.out.printf("Thread %s completed in %.2f seconds\n", name, duration / 1e9);
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENCY);

        for (int i = 0; i < CONCURRENCY; i++) {
            executor.execute(() -> doWork(Thread.currentThread().getName()));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1e9;
        System.out.println("Checking till " + MAX_INT + ", found " + (totalPrimeNumbers.get() + 1) +
                " prime numbers. Took " + duration + " seconds.");
    }
}
