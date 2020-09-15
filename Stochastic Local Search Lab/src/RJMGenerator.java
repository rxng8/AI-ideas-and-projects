import java.util.Arrays;
import java.util.Random;


public class RJMGenerator {

	/**
	 * Generate a RookJumpingMaze of the given size and optimize it using a stochastic local search algorithm of your choice, limited to a maximum of 5000 iterations.  
	 * For a size 5 RookJumpingMaze, your algorithm should be tuned to achieve a median energy of -18.0.
	 * @param size the number of rows / columns
	 * @return the generated RookJumpingMaze
	 */
	public static RookJumpingMaze generate(int size) {
	    // TODO - Implement.
		final int ITERATIONS = 5000;
		State state = new RookJumpingMaze(size);
		SimulatedAnnealer searcher = new SimulatedAnnealer(state, 1000.0, 0.997);
		State minState = searcher.search(ITERATIONS);
		
		return (RookJumpingMaze) minState;
	}

	@SuppressWarnings("unused")
	private static void computeMedian(int size, int numMazes) {
		double[] energies = new double[numMazes];
		for (int i = 0; i < numMazes; i++) {
			energies[i] = generate(size).energy();
		}
		Arrays.sort(energies);
		System.out.println("Median energy: " + energies[numMazes / 2]);
	}
	
	/**
	 * Generate a size 5 RookJumpingMaze object, print the RookJumpingMaze object, and print the energy of the RookJumpingMaze object.
	 * @param args (not used)
	 */
	public static void main(String[] args) {
		RookJumpingMaze maze = generate(5);
		System.out.println(maze);
//		System.out.println("Energy: " + maze.energy());
//		computeMedian(5, 100); // -18
	}

}
