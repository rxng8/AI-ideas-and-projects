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
        int[][] clauses = {}; // TODO - Insert your clauses here.
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
        int[][] clauses = {}; // TODO - Insert your clauses here.
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
