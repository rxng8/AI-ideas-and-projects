public class PPigSolver {
    int goal;
    boolean[][][] computed;
    double[][][] p;
    boolean[][][] roll;

    PPigSolver(int goal) {
        this.goal = goal;
        computed = new boolean[goal][goal][goal];
        p = new double[goal][goal][goal];
        roll = new boolean[goal][goal][goal];
        for (int i = 0; i < goal; i++) // for all i
            for (int j = 0; j < goal; j++) // for all j
                for (int k = 0; i + k < goal; k++) // for all k
                    pWin(i, j, k);
    }

    public double pWin(int i, int j, int k) {
        if (i + k >= goal) return 1.0;
        if (j >= goal) return 0.0;
        if (computed[i][j][k]) return p[i][j][k];

        // Compute the probability of winning with a roll
        double pRoll = 1.0 - pWin(j, i + 1, 0);
        for (int roll = 2; roll <= 6; roll++)
            pRoll += pWin(i, j, k + roll);
        pRoll /= 6.0;

        // Compute the probability of winning with a hold
        double pHold;
        if (k == 0) 
            pHold = 1.0 - pWin(j, i + 1, 0);
        else 
            pHold = 1.0 - pWin(j, i + k, 0);

        // Optimal play chooses the action with the greater win probability
        roll[i][j][k] = pRoll > pHold;
        if (roll[i][j][k])
            p[i][j][k] = pRoll;
        else
            p[i][j][k] = pHold;
        computed[i][j][k] = true;
        return p[i][j][k];
    }

    public void summarize() {
        System.out.println("p[0][0][0] = " + p[0][0][0]);
        System.out.println();
        System.out.println("i\tj\tPolicy changes at k =");
        for (int i = 0; i < goal; i++) // for all i
            for (int j = 0; j < goal; j++) { // for all j
                int k = 0;
                System.out.print(i + "\t" + j + "\t" + (roll[i][j][k] ? "roll " : "hold "));
                for (k = 1; i + k < goal; k++) // for all valid k
                    if (roll[i][j][k] != roll[i][j][k-1])
                        System.out.print(k + " " + (roll[i][j][k] ? "roll " : "hold "));
                System.out.println();
            }
    }

    public static void main(String[] args) {
        new PPigSolver(100).summarize();
    }
}
