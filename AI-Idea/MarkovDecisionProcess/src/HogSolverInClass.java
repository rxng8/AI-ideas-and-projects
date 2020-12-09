import java.util.Scanner;

/**
 * HogSolver.java - demonstrates the computation of Hog win
 * probabilities using value iteration.
 *
 * @author Todd Neller */

public class HogSolverInClass {

	/**
	 * variable <code>maxDice</code> - the maximum number of dice 
	 * a player can roll
	 */
	int maxDice;

	/**
	 * variable <code>goal</code> - goal score for Pig
	 */
	int goal;

	/**
	 * variable <code>util</code> - probability estimates
	 */
	double[][] p;

	/**
	 * variable <code>dice</code> - best number of dice to roll
	 */
	int[][] dice;


	/**
	 * variable <code>expOutcomes</code> - probability of outcome
	 * indexed by the number of dice and the roll total (HOG being 0).
	 */
	double[][] expOutcomes;


	/**
	 * Creates a new <code>HogSolver</code> instance.
	 *
	 * @param goal an <code>int</code> value - goal value
	 * @param theta a <code>double</code> value - convergence
	 * condition for value iteration
	 */
	HogSolverInClass(int maxDice, int goal, double theta) 
	{
		this.maxDice = maxDice;
		this.goal = goal;
		init();
		while (valueIterate() >= theta);	
	}

	public void init() 
	{
		p = new double[goal][goal];
		dice = new int[goal][goal];
		expOutcomes = new double[maxDice + 1][6 * maxDice + 1];
		// Enter expectations for the roll of a single die
		for (int total = 0; total <= 6; total++)
			if (total != 1)
				expOutcomes[1][total] = 1.0/6.0;
		// Compute expectations for rolls of more dice
		for (int dice = 2; dice <= maxDice; dice++)
			for (int prevTotal = 0; prevTotal < expOutcomes[dice].length; prevTotal++) {
				if (expOutcomes[dice - 1][prevTotal] > 0) 
					if (prevTotal == 0)
						expOutcomes[dice][prevTotal] += expOutcomes[dice - 1][prevTotal];
					else
						for (int roll = 1; roll <= 6; roll++)
							if (roll == 1)
								expOutcomes[dice][0] += expOutcomes[dice - 1][prevTotal] / 6;
							else
								expOutcomes[dice][prevTotal + roll] += expOutcomes[dice - 1][prevTotal] / 6;
			}
	}



	public double pWin(int myScore, int otherScore)
	{ // probability of a win from a given game state
		if (myScore >= goal)
			return 1.0;
		else if (otherScore >= goal)
			return 0.0;
		else return p[myScore][otherScore];
	}

	
	/**
	 * <code>valueIterate</code> - perform one iteration of value
	 * iteration and return the maximum change to a probability
	 * estimate.
	 *
	 * @return a <code>double</code> value - the maximum change to a
	 * probability estimate */
	public double valueIterate()
	{
		double maxChange = 0.0;

		// Compute new probabilities.
		for (int i = 0; i < goal; i++) // for all i
			for (int j = 0; j < goal; j++) { // for all j
				double oldProb = p[i][j];
				int newBestDice = 1;
				double newBestProb = 0;
				
				// TODO - implement
				
				
				for (int nDice = 1; nDice <= maxDice; nDice++) {
					double pWin = 0.0;
					for (int actDice = 1; actDice <= nDice; actDice ++) {
						double pRoll = 0;
						for (int roll = 1; roll <= 6; roll++) {
							pRoll += p[j][i + roll];
						}
						pRoll /= 6;
						pWin += pRoll;
					}
					pWin /= nDice;
					
					
					
				}
				
//				pRoll /= maxDice;
				
				
				
				p[i][j] = newBestProb;
				dice[i][j] = newBestDice;
				double change = Math.abs(p[i][j] - oldProb);
				maxChange = Math.max(maxChange, change);
			}
		return maxChange;
	}

	/**
	 * <code>outputPolicy</code> - output optimal policy table
	 * showing dice to roll for each given i (down) and j (across). */
	public void outputPolicy()
	{
//		System.out.println("util[0][0] = " + util[0][0] + "\n");
//		for (int i = 0; i < goal; i++) {
//			for (int j = 0; j < goal; j++)
//				System.out.print(dice[i][j] + " ");
//			System.out.println();
//		}
		
		System.out.print("{");
		for (int i = 0; i < goal; i++) {
			System.out.print("{");
			for (int j = 0; j < goal; j++) {
				System.out.print(dice[i][j]);
				System.out.print((j < goal - 1) ? ", " : "}");
			}
			System.out.println((i < goal - 1) ? "," : "}");
		}
	}

	public void advise() {
		Scanner in = new Scanner(System.in);
		System.out.println("Starting Hog advice mode.  Enter nothing to terminate.");
		while (true) {
			try {
				System.out.print("Please enter your score and your opponent score, separated by a space: ");
				String line = in.nextLine().trim();
				if (line.equals(""))
					break;
				Scanner lineIn = new Scanner(line);
				int i = lineIn.nextInt();
				int j = lineIn.nextInt();
				if (i >= 0 && j >= 0 && i < goal && j < goal)
					System.out.printf("Roll %d dice.\n", dice[i][j]);
				lineIn.close();
			}
			catch (Exception e) {
				continue;
			}
		}
		System.out.println("Exiting Hog advice mode.");
		in.close();
	}
	

	public static void main(String[] args){
		int maxDice = 30;
		int goal = 100;
		double theta = 1e-9;
		try {
			if (args.length > 3) 
				throw new IllegalArgumentException("Too many arguments");
			if (args.length > 0) maxDice = Integer.parseInt(args[0]);
			if (args.length > 1) goal = Integer.parseInt(args[1]);
			if (args.length > 2) theta = Double.parseDouble(args[2]);
		}
		catch (Exception e) {
			System.out.println(e);
			System.out.println("Usage: java HogSolver [int maxDice [int goal [double theta]]]");
			System.exit(1);
		}
		HogSolverInClass solver = new HogSolverInClass(maxDice, goal, theta);
		solver.outputPolicy();
		solver.advise();
	}
} // HogSolver