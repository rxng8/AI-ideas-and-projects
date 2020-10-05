
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
						
						// Compute pwin hold
						double pHold = 1.0 - pWin(j, i + k, 0);
						
						
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
	
	public boolean shouldRoll(int i, int j, int k) {
		return roll[i][j][k];
	}

}
