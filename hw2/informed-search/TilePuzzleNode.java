import java.util.ArrayList;
import java.util.Random;


public class TilePuzzleNode extends SearchNode {
	private static final Random random = new Random();
	int size;
	int[][] tile;
	int zeroRow, zeroCol;
	
	public TilePuzzleNode(int size, int numShuffles) {
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
			TilePuzzleNode child = (TilePuzzleNode) childClone();
			if (child.move(dir))
				children.add(child);
		}
		return children;
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
	
	public TilePuzzleNode clone() {
		TilePuzzleNode copy = (TilePuzzleNode) super.clone();
		copy.tile = new int[size][size];
		for (int r = 0; r < size; r++)
			for (int c = 0; c < size; c++)
				copy.tile[r][c] = tile[r][c];
		return copy;
		
	}
	
	public static void main(String[] args) {
		TilePuzzleNode root = new TilePuzzleNode(4, 12);
		
		Searcher searcher = new IterativeDeepeningDepthFirstSearcher();
		if (searcher.search(root)) {
		    // successful search
		    System.out.println("Goal node found in " + searcher.getNodeCount() 
				       + " nodes.");
		    System.out.println("Goal path:");
		    searcher.printGoalPath();
		} else {
		    // unsuccessful search
		    System.out.println("Goal node not found in " 
				       + searcher.getNodeCount() + " nodes.");
		}

		
		
	}

}
