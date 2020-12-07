import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class HTilePuzzleNode extends HSearchNode {
	private static final Random random = new Random();
	int size;
	int[][] tile;
	int zeroRow, zeroCol;
	String mode = "normal";
	
	public HTilePuzzleNode(int size, int numShuffles) {
		this.size = size;
		int i = 0;
		tile = new int[size][size];
		for (int r = 0; r < size; r++)
			for (int c = 0; c < size; c++)
				tile[r][c] = i++;
		// shuffle
		for (i = 0; i < numShuffles; i++)
			if (!move(random.nextInt(4)))
				i--;
	}
	
	private boolean move(int dir) {
		final int[] dRow = {0, -1, 0, 1};
		final int[] dCol = {1, 0, -1, 0};
		int r2 = zeroRow + dRow[dir];
		int c2 = zeroCol + dCol[dir];
		if (r2 < 0 || r2 >= size || c2 < 0 || c2 >= size)
			return false;
		else {
			tile[zeroRow][zeroCol] = tile[r2][c2];
			tile[r2][c2] = 0;
			zeroRow = r2;
			zeroCol = c2;
			return true;
		}
	}
	
	@Override
	public boolean isGoal() {
		int i = 0;
		for (int r = 0; r < size; r++)
			for (int c = 0; c < size; c++)
				if (tile[r][c] != i++)
					return false;
		return true;
	}

	@Override
	public ArrayList<SearchNode> expand() {
		ArrayList<SearchNode> children = new ArrayList<SearchNode>();
		for (int dir = 0; dir < 4; dir++) {
			HTilePuzzleNode child = (HTilePuzzleNode) childClone();
			if (child.move(dir))
				children.add(child);
		}
		return children;
	}
	
	public static int getDistance(int row1, int col1, int row2, int col2) {
		return Math.abs(row1 - row2) + Math.abs(col1 - col2);
	}
	
	public int getCost() {
		int sumDistance = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (tile[i][j] != 0) {
					// Get the correct position
					int correct_row = (tile[i][j] / 4) % 4;
					int correct_col = tile[i][j] % 4;
					sumDistance += getDistance(i, j, correct_row, correct_col);
				}
			}
		}
		return sumDistance;
	}
	
	public int getCost2() {
		int outNodes = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (tile[i][j] != 0) {
					// Get the correct position
					int correct_row = (tile[i][j] / 4) % 4;
					int correct_col = tile[i][j] % 4;
					if (getDistance(i, j, correct_row, correct_col) != 0) {
						outNodes++;
					}
				}
			}
		}
		return outNodes;
	}
	
	public boolean equals(HSearchNode other) {
		
		// Casting
		HTilePuzzleNode otherNode = (HTilePuzzleNode) other;
		
		// Compare every postion
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (this.tile[i][j] != otherNode.tile[i][j]) {
					return false;
				}
			}
		}
		
		return true;
	}
	
//	private int[] getCurrentPosition() {
//		int[] indices = new int[2]; // indices[0] is the current row position, indices[1] is the current col position, 
//		for (int i = 0; i < size; i++) {
//			for (int j = 0; j < size; j++) {
//				if (tile[0][0] == 0) {
//					indices[0] = i;
//					indices[1] = j;
//					break;
//				}
//			}
//		}
//		return indices;
//	}

	@Override
	public double getG() {
		return depth;
	}

	@Override
	public double getH() {

		if(this.mode.equals("mat_distance")) {
			return getCost();
		} else if (this.mode.equals("out_nodes")) {
			return getCost2();
		}
		return 0;
	}
	
	public void setHMode(String mode) {
		this.mode = mode;
	}

	public String toString() {
		int chars = new Integer(size * size - 1).toString().length();
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++)
				sb.append(String.format("%" + chars + "d ", tile[r][c]));
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public HTilePuzzleNode clone() {
		HTilePuzzleNode copy = (HTilePuzzleNode) super.clone();
		copy.tile = new int[size][size];
		for (int r = 0; r < size; r++)
			for (int c = 0; c < size; c++)
				copy.tile[r][c] = tile[r][c];
		return copy;
		
	}
	
	public static void main(String[] args) {
		HTilePuzzleNode root = new HTilePuzzleNode(4, 2);
		
//		Searcher searcher = new IterativeDeepeningDepthFirstSearcher();
		Searcher searcher = new BestFirstSearcher(new UniformComparator());
//		Searcher searcher = new BestFirstSearcher(new GreedyComparator());
//		Searcher searcher = new BestFirstSearcher(new AStarComparator());
//		if (searcher.search(root)) {
//		    // successful search
//		    System.out.println("Goal node found in " + searcher.getNodeCount() 
//				       + " nodes.");
//		    System.out.println("Goal path:");
//		    searcher.printGoalPath();
//		} else {
//		    // unsuccessful search
//		    System.out.println("Goal node not found in " 
//				       + searcher.getNodeCount() + " nodes.");
//		}
		String[] modes = {"normal", "mat_distance", "out_nodes"};
		for (String mode : modes) {
			System.out.println("Mode: " + mode + ": ");
			final int TRIALS = 500;
			ArrayList<Integer> nodeCounts = new ArrayList<>();
			final int SIZE = 4;
			final int NUM_MOVES = 12;
			for (int i = 0; i < TRIALS; i++) {
				root = new HTilePuzzleNode(SIZE, NUM_MOVES);
				// Set heuristic mode
				root.setHMode(mode);
				searcher.search(root);
//				System.out.println("Node count: " + searcher.getNodeCount());
				nodeCounts.add(searcher.getNodeCount());
			}
			long totalNodeCounts = 0;
			for (int count : nodeCounts) {
				totalNodeCounts += count;
			}
			System.out.printf("Average node Count: %g\n", ((double) totalNodeCounts) / TRIALS);
			Collections.sort(nodeCounts);
			System.out.printf("Median node Count: %d\n\n", nodeCounts.get(TRIALS / 2));
		}
		
		
	}


}
