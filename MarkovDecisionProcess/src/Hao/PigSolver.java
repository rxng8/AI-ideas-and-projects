public class PigSolver {

	double goal; 
	double epsilon;
	double [][][] p;
	boolean [][][] roll;

	/**
	 * Constructor 
	 */
	public PigSolver(int goal, double epsilon) {
		this.goal = goal;
		this.epsilon = epsilon; 
		p = new double[goal][goal][goal];
		roll = new boolean[goal][goal][goal];
		valueIterate();
	}

	void valueIterate() {
		double maxChange;
		do {
			maxChange = 0;
			for(int i = 0; i < goal; i++)
				for(int j = 0; j < goal; j++)
					for(int k = 0; k < goal - i; k++) {
						if(k == 1) {
							continue; 
						}

						double oldProb = p[i][j][k];
						double pRoll = 1 - pWin(j,i,0);
						for(int roll = 2; roll <= 6; roll++) {
							pRoll += pWin(i,j,k+roll);
						}
						pRoll /= 6;

						double pHold = 1.0 - pWin(j, i + k, 0);

						roll[i][j][k] = pRoll > pHold;
						p[i][j][k] = Math.max(pRoll, pHold);

						double change = Math.abs(p[i][j][k] - oldProb);
						maxChange = Math.max(maxChange, change);
					}
		}while(maxChange >= epsilon);
	}

	public double pWin(int i, int j, int k) { 
		if (i + k >= goal)
			return 1.0;
		else if (j >= goal)
			return 0.0;
		else 
			return p[i][j][k];
	}

	/**
	 * Whether roll or hold in the given state
	 */
	public boolean shouldRoll(int i, int j , int k) {
		return roll[i][j][k];
	}

	public static void main(String[] args){
		PigSolver pig = new PigSolver(100, 1e-9);

		for(int i = 0; i < pig.goal; i++)
			for(int j = 0; j < pig.goal; j++){
				int k = 0;
				System.out.print(i + "\t" + j + "\t" + (pig.shouldRoll(i, j, k) ? "roll " : "hold "));
				for (k = 2; i + k < pig.goal; k++) { // for all valid k
					boolean compare = pig.shouldRoll(i, j, k-1);
					if(k==2) 
						compare = pig.shouldRoll(i, j, 0);
					if (pig.shouldRoll(i, j, k) != compare)
						System.out.print(k + " " + (pig.shouldRoll(i, j, k) ? "roll " : "hold "));
				}
				System.out.println();
			}
	}
}
