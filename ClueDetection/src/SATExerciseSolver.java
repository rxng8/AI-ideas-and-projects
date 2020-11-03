import java.util.Arrays;


public class SATExerciseSolver {

	/**
	 * Return a SATSolver loaded with a knowledge base containing the CNF clauses for Exercise 2. 
	 * For variable numbering, let A = 1, B = 2, and C = 3.
	 * @return a SATSolver loaded with a knowledge base containing the CNF clauses for Exercise 2
	 */
	public SATSolver get2Solver() {
		SATSolver solver = new SAT4JSolver();
        final int A = 1, B = 2, C = 3;
        int[][] clauses = {{-1,3},{-2,-3},{2,3},{-3,2,-1},{-2,3},{1,3}}; // TODO - Insert your clauses here.
        for (int[] clause : clauses)
        	solver.addClause(clause);
        return solver;
	}
	
	/**
	 * Return a SATSolver loaded with a knowledge base containing the CNF clauses for Exercise 5. 
	 * For variable numbering, let A = 1, B = 2, C = 3, D = 4, E = 5, and F = 6.
	 * @return a SATSolver loaded with a knowledge base containing the CNF clauses for Exercise 5
	 */
	public SATSolver get5Solver() {
        final int A = 1, B = 2, C = 3, D = 4, E = 5, F = 6;
        SATSolver solver = new SAT4JSolver();
//        int[][] clauses = {
//            	{-1,2},{-1,3},{-1,4},{-1,5},{-1,6},{-2,-3},{-2,-4},{-2,-5},{-2,-6},
//            	{-3,1},{-3,2},{-4,1,2,3},{-5,-1},{-5,-2},{-5,-3},{-5,-4},{-6,-1},{-6,-2},
//            	{-6,-3},{-6,-4},{-6,-5},
//            	{-2,-3,-4,-5,-6,1},{3,4,5,6,2},{-1,-2,3},{-1,4},{-2,4},{-3,4},{1,2,3,4,5},{1,2,3,4,5,6}};
        
        int[][] clauses = {
        		// From statement (1)
        		{A, -B, -C, -D, -E, -F},
        		{-A, B},
        		{-A, C},
        		{-A, D},
        		{-A, E},
        		{-A, F},
        		
        		// From statement (2)
        		{B, C, D, E, F},
        		{-B, -C},
        		{-B, -D},
        		{-B, -E},
        		{-B, -F},
        		
        		// From statement (3)
        		{C, -A, -B},
        		{-C, A},
        		{-C, B},
        		
        		// From statement (4)
        		{D, -A},
        		{D, -B},
        		{D, -C},
        		{A, B, C, -D},
        		
        		// From statement (5)
        		{A, B, C, D, E},
        		{-E, -A},
        		{-E, -B},
        		{-E, -C},
        		{-E, -D},
        		
        		// From statement (6)
        		{A, B, C, D, E, F},
        		{-F, -A},
        		{-F, -B},
        		{-F, -C},
        		{-F, -D},
        		{-F, -E},
        		
        }; // TODO - Insert your clauses here.
        for (int[] clause : clauses)
            solver.addClause(clause);
        return solver;
	}
	
	/**
	 * Print the clauses of the given SATSolver.
	 * @param solver a SATSolver
	 */
	public void showClauses(SATSolver solver) {
		System.out.println("Clauses:");
		for (int[] clause : solver.clauses)
			System.out.println(Arrays.toString(clause));
	}
	
	/**
	 * Test and print the results of reasoning about the literals of the given SATSolver.
	 * @param solver a SATSolver
	 */
	public void testLiterals(SATSolver solver) {
		String[] resultStr = {"false", "unknown", "true"};
		int maxVar = 0;
		for (int[] clause : solver.clauses)
			for (int literal : clause)
				maxVar = Math.max(maxVar, Math.abs(literal));
		for (int var = 1; var <= maxVar; var++) {
			int result = solver.testLiteral(var);
			System.out.printf("%d: %s\n", var, resultStr[result + 1]);
		}
	}
	
	/**
	 * Test exercises 2 and 5, printing the clauses and the reasoning results for each.
	 */
	public void testExercises() {
		System.out.println("Exercise 2:");
		SATSolver solver = get2Solver();
		showClauses(solver);
		testLiterals(solver);
		System.out.println();
		System.out.println("Exercise 5:");
		solver = get5Solver();
		showClauses(solver);
		testLiterals(solver);
	}

	/**
	 * Test the assignment logic exercises.
	 * @param args (not used)
	 */
	public static void main(String[] args) {
		new SATExerciseSolver().testExercises();
	}
	
}
