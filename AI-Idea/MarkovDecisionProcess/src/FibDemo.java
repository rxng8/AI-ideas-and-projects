public class FibDemo {

    public static final int MAX = 90;
    public static boolean[] computed = new boolean[MAX];
    public static long[] result = new long[MAX];

    public static long fibDP(int n) {
        // Compute and store value if not already stored
        if (!computed[n]) {
            if (n <= 2)
                result[n] = 1;
            else
                result[n] = fibDP(n - 1) + fibDP(n - 2);
            computed[n] = true;
        }
        
        // Retrieve and return stored value
        return result[n];
    }

    public static long fib(int n) {
        if (n <= 2)
            return 1;
        else
            return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] args) {
        for (int n = 1; n < MAX; n++)
            System.out.println("fibDP(" + n + ") = " + fibDP(n));
        for (int n = 1; n < MAX; n++)
            System.out.println("fib(" + n + ") = " + fib(n));
    }
}
