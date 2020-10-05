
public class PigSolver {

	int goal;
	double epsilon;
	double[][][] p;
	boolean[][][] flip;

	public PigSolver(int goal, double epsilon) {
		this.goal = goal;
		this.epsilon = epsilon;
		p = new double[goal][goal][goal];
		flip = new boolean[goal][goal][goal];
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
						double pFlip = (1.0 - pWin(j, i, 0) + pWin(i, j, k + 1)) / 2;
						double pHold = 1.0 - pWin(j, i + k, 0);
						p[i][j][k] = Math.max(pFlip, pHold);
						flip[i][j][k] = pFlip > pHold;
						double change = Math.abs(p[i][j][k] - oldProb);
						maxChange = Math.max(maxChange, change);
					}
		} while (maxChange >= epsilon);

	}

	public double pWin(int i, int j, int k) {
		if (i + k >= goal) {
			return 1;
		}
		
		if (j >= goal) {
			return 0;
		}
		
		return p[i][j][k];
	}
	
	public boolean shouldRoll(int i, int j, int k) {
		return flip[i][j][k];
	}

}
