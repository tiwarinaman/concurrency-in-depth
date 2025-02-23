package fairThreads01.unfair;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class UnfairThreads {

    private static final int MAX_INT = 100000000;
    private static final int CONCURRENCY = 10;
    private static final AtomicInteger totalPrimeNumbers = new AtomicInteger(0);

    private static void checkPrime(int number) {
        if ((number & 1) == 0) {
            return;
        }
        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            if (number % i == 0) {
                return;
            }
        }
        totalPrimeNumbers.incrementAndGet();
    }

    private static void doBatch(String name, int nStart, int nEnd) {
        final long startTime = System.nanoTime();
        for (int i = nStart; i < nEnd; i++) {
            checkPrime(i);
        }
        final long duration = System.nanoTime() - startTime;
        System.out.printf("Thread %s [%d, %d) completed in %.2f seconds\n", name, nStart, nEnd, duration / 1e9);
    }

    public static void main(String[] args) {
        final long startTime = System.nanoTime();
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENCY);
        int nStart = 3;
        int batch = MAX_INT / CONCURRENCY;

        for (int i = 0; i < CONCURRENCY; i++) {
            final int start = nStart;
            final int end = nStart + batch;
            executor.execute(() -> doBatch(Thread.currentThread().getName(), start, end));
            nStart += batch;
        }

        int finalNStart = nStart;
        executor.execute(() -> doBatch(Thread.currentThread().getName(), finalNStart, MAX_INT));
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
