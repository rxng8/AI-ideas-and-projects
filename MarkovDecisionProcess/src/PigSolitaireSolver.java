
public class PigSolitaireSolver {
	
	int goal, turns;
	boolean[][][] computed;
    double[][][] p;
    boolean[][][] roll;
	
	public PigSolitaireSolver(int goal, int turns) {
		this.goal = goal;
		this.turns = turns;
		computed = new boolean[goal][turns][goal];
        p = new double[goal][turns][goal];
        roll = new boolean[goal][turns][goal];
        for (int i = 0; i < goal; i++) // for all i
            for (int j = 0; j < turns; j++) // for all j
                for (int k = 0; i + k < goal; k++) // for all k
                    pWin(i, j, k);
	}
	
    public double pWin(int i, int j, int k) {
    	if (j >= turns) {
    		return 0;
    	}
    	
    	if (i + k >= goal) {
    		return 1;
    	}
    	
    	if (computed[i][j][k]) return p[i][j][k];
    	
    	// Compute the probability of winning with a roll
        double pRoll = pWin(i, j + 1, 0);
        for (int roll = 2; roll <= 6; roll++) {
            pRoll += pWin(i, j, k + roll);
        }
        pRoll /= 6.0;

        // Compute the probability of winning with a hold
        double pHold;
        if (k == 0) 
            pHold = pWin(i, j + 1, 0);
        else 
            pHold = pWin(i + k, j + 1, 0);

        // Optimal play chooses the action with the greater win probability
        roll[i][j][k] = pRoll > pHold;
        if (roll[i][j][k])
            p[i][j][k] = pRoll;
        else
            p[i][j][k] = pHold;
        computed[i][j][k] = true;
        return p[i][j][k];
    }
	
	boolean shouldRoll (int i, int j, int k) {
		return roll[i][j][k];
	}
//	public static void main(String[] args){
//		PigSolitaireSolver pig = new PigSolitaireSolver(10, 10);
//
//		for(int i = 0; i < pig.goal; i++)
//			for(int j = 0; j < pig.turns; j++){
//				int k = 0;
//				System.out.print(i + "\t" + j + "\t" + (pig.shouldRoll(i, j, k) ? "roll " : "hold "));
//				for (k = 2; i + k < pig.goal; k++) { // for all valid k
//					boolean compare = pig.shouldRoll(i, j, k-1);
//					if(k==2) 
//						compare = pig.shouldRoll(i, j, 0);
//					if (pig.shouldRoll(i, j, k) != compare)
//						System.out.print(k + " " + (pig.shouldRoll(i, j, k) ? "roll " : "hold "));
//				}
//				System.out.println();
//			}
//		System.out.println();
//	}
}
