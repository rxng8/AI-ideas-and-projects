
public class TwoDicePigSolver {

	int goal;
	double theta;
	double[][][] p;
	boolean[][][] roll;

	public TwoDicePigSolver(int goal, double theta) {
		this.goal = goal;
		this.theta = theta;
		p = new double[goal][goal][goal];
		roll = new boolean[goal][goal][goal];
		valueIterate();
	}

	private void valueIterate() {
		double maxChange;
		do {
			maxChange = 0.0;
			for (int i = 0; i < goal; i++) // for all i
				for (int j = 0; j < goal; j++) // for all j
					for (int k = 0; k < goal - i; k++) { // for all k

						double oldProb = p[i][j][k];
						
						// Compute p win roll
						double pRoll = 0;
						for (int dice1 = 1; dice1 <= 6; dice1 ++) {
							for (int dice2 = 1; dice2 <= 6; dice2 ++) {
								if (dice1 == 1 && dice2 == 1) {
									pRoll += (1.0 - pWin(j, 0, 0));
								} else if (dice1 == 1 || dice2 == 1) {
									pRoll += (1.0 - pWin(j, i, 0));
								} else {
									pRoll += pWin(i, j, k + dice1 + dice2);
								}
							}
						}
						pRoll /= 36.0;
//						System.out.println(pRoll);
						
						// Compute pwin hold
						double pHold = 1.0 - pWin(j, i + k, 0);
//						System.out.println(pHold);
						
						p[i][j][k] = Math.max(pRoll, pHold);
						roll[i][j][k] = pRoll > pHold;
						double change = Math.abs(p[i][j][k] - oldProb);
						maxChange = Math.max(maxChange, change);
					}
		} while (maxChange >= theta);

	}

	public double pWin(int myScore, int otherScore, int turnScore) {
		
		if (myScore + turnScore >= goal) {
			return 1;
		}
		
		if (otherScore >= goal) {
			return 0;
		}
		
		return p[myScore][otherScore][turnScore];
	}
	
	public void outputHoldValues() {
		for (int i = 0; i < goal; i++) {
			for (int j = 0; j < goal; j++) {
				int k = 0;
				while (k < goal - i && roll[i][j][k])
					k++;    
				System.out.print(k + " ");
			}
			System.out.println();
		}
	}
	
	public boolean shouldRoll(int i, int j, int k) {
		return roll[i][j][k];
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
	
	public static void main(String[] args){
//		new TwoDicePigSolver(100, 1e-9).outputHoldValues();
		TwoDicePigSolver pig = new TwoDicePigSolver(100, 1e-9);
		pig.summarize();
//		TwoDicePigSolver pig = new TwoDicePigSolver(100, 1e-9);
//		for(int i = 0; i < pig.goal; i++) {
//			for(int j = 0; j < pig.goal; j++){
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
//		}
	}
	
}
