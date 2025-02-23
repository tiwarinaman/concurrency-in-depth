package fairThreads01.simple;

public class SimpleThreads {

    private static final int MAX_INT = 100000000;
    private static int totalPrimeNumbers = 0;

    private static void checkPrime(int number) {
        if ((number & 1) == 0) {
            return;
        }
        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            if (number % i == 0) {
                return;
            }
        }
        totalPrimeNumbers++;
    }

    public static void main(String[] args) {

        final long startTime = System.nanoTime();

        for (int i = 3; i < MAX_INT; i++) {
            checkPrime(i);
        }

        final long endTime = System.nanoTime();
        final double duration = (endTime - startTime) / 1e9;

        System.out.println("Checking till " + MAX_INT + ", found " + (totalPrimeNumbers + 1) +
                " prime numbers. Took " + duration + " seconds.");
    }
}
