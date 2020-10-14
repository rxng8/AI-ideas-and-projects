public class TwoDicePigSolver {

	int goal; 
	double theta;
	double[][][] p;
	boolean[][][]roll;
	/**
	 * Constructor 
	 */
	public TwoDicePigSolver(int goal, double theta) {
		this.goal= goal;
		this.theta = theta;
		p = new double[goal][goal][goal];
		roll = new boolean[goal][goal][goal];
		valueIterate();
	}

	void valueIterate() {
		double maxChange;
		do {
			maxChange = 0;
			for(int i = 0; i < goal; i++) {
				for(int j = 0; j < goal; j++) {
					for(int k = 0; k < goal - i; k++) {
						
						if(k==1 || k==2 || k==3)
							continue;
						
						double oldProb = p[i][j][k];
						double pRoll = 0;
						//When one dice rolls a 1;
						for(int d1 = 1; d1 <= 6; d1++) {
							for(int d2 = 1; d2 <= 6; d2++) {
								if(d1 == 1 || d2 == 1) {
									if(d1 == 1 && d2 == 1)
										pRoll += 1 - pWin(j,0,0);
									else
										pRoll += pWin(i,j,0);
								}
								else
									pRoll += pWin(i,j, k + d1 + d2);	
							}
						}
						pRoll /= 36;

						//Compute the probability of winning when hold
						double pHold = 1 - pWin(j,i+k,0);

						///Update the optimal the play choice for greater winning probability
						roll[i][j][k] = pRoll > pHold;
						p[i][j][k] = Math.max(pRoll, pHold);
						
						double change = Math.abs(p[i][j][k] - oldProb);
						maxChange = Math.max(maxChange, change);

					}
				}
			}
		}
		while(maxChange >= theta);
	}

	public double pWin(int myScore, int otherScore, int turnScore) {
		if(myScore + turnScore >= goal)
			return 1.0;
		if(otherScore >= goal)
			return 0.0;
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

	public static void main(String[] args){
		new TwoDicePigSolver(100, 1e-9).outputHoldValues();
	}
}
