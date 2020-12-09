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
		double acceptRate = 0.0005;
		final int ITERATIONS = 5000;
		RookJumpingMaze state = new RookJumpingMaze(size);
		RookJumpingMaze minState = state.clone();
		double energy = state.energy();
		double minEnergy = energy;
		Random random = new Random();
		for (int i = 0; i < ITERATIONS; i++) {	
			state.step();
			double nextEnergy = state.energy();
			if (nextEnergy <= energy || random.nextDouble() < acceptRate) {
				energy = nextEnergy;
				if (nextEnergy < minEnergy) {
					minState = state.clone();
					minEnergy = nextEnergy;
				}
			} else {
				state.undo();
			}
		}
		
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