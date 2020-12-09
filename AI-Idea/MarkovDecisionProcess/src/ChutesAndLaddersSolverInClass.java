public class ChutesAndLaddersSolverInClass
{
	public static final int GOAL = 100; // Goal position
	public static final double GAMMA = 1.0, EPS = 1e-9; // Discount and convergence threshold constants
	public static int[] goesTo = new int[GOAL + 1]; // Chute & Ladders chute and ladder transitions
	public static double[] V = new double[GOAL + 1]; // Value function of number of expected turns remaining in game, indexed by position

	static {
		// Initialize chute/ladder transitions
		// Source: http://cs.gettysburg.edu/~tneller/cs112/06fa/hw1.html 
		for (int i = 0; i < goesTo.length; i++)
			goesTo[i] = i;
		int[] from = {1, 4, 9, 16, 21, 28, 36, 48, 49, 51, 56, 62, 64, 71, 80, 87, 93, 95, 98};
		int[] to = {38, 14, 31, 6, 42, 84, 44, 26, 11, 67, 53, 19, 60, 91, 100, 24, 73, 75, 78};
		for (int i = 0; i < from.length; i++)
			goesTo[from[i]] = to[i];
	}


	public static void valueIterate() 
	{
		// Value iterate the Chutes & Ladders game to compute the expected turns to go from any given state.
		double maxChange;
		do {
			maxChange = 0.0;
			for (int i = 0; i < GOAL; i++) { // For all non-terminal states
				double old = V[i];
				double newV = 0;
				for (int dice = 1; dice <= 6; dice++) {
					if (i + dice >= goesTo.length) {
						newV += 1 + V[i];
					} else {
						newV += 1 + V[goesTo[i + dice]];
					}
				}
				
				newV /= 6;
				
				V[i] = newV;
				maxChange = Math.max(Math.abs(newV - old), maxChange);
			}
		} while (maxChange >= EPS);
	}

	public static void report() 
	{
		System.out.println("Expected turns per game: " + Math.abs(V[0]));
		System.out.println("Expected turns remaining by position:");
		for (int row = 9; row >= 0; row--) {
			for (int col = 0; col < 10; col++) {
				int boardPos = (row % 2 == 0) ? 10 * row + col + 1 : 10 * row + 10 - col; 
				System.out.printf("%3d: %5.2f ", boardPos, V[boardPos]);
			}
			System.out.println();
		}
	}

	public static void main(String[] args) 
	{
		valueIterate();
		report();
	}

}