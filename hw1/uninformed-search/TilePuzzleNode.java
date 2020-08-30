import java.util.ArrayList;
import java.util.Random;

public class TilePuzzleNode extends SearchNode {

	static final Random random = new Random();
	int size;
	int[][] tile;
	int zeroRow, zeroCol;
	
	public TilePuzzleNode(int size, int numShuffles) {
		this.size = size;
		int i = 0;
		tile = new int[size][size];
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				tile[r][c] = i++;
			}
		}
		
		// shuffle
		for (i = 0; i < numShuffles; i++) {
			while (!move(random.nextInt(4)));
		}
		
		
	}
	
	private boolean move (int dir) {
		final int[] dRow = {0, -1, 0, 1};
		final int[] dCol = {1, 0, -1, 0};
		int r2 = zeroRow + dRow[dir];
		int c2 = zeroCol + dCol[dir];
		if (r2 < 0 || r2 >= size || c2 < 0 || c2 >= size) {
			return false;
		}
		tile[zeroRow][zeroCol] = tile[r2][c2];
		tile[r2][c2] = 0;
		zeroRow = r2;
		zeroCol = c2;
		return true;
	}
	
	@Override
	public boolean isGoal() {
		int i = 0;
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				if (tile[r][c] != i++) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public ArrayList<SearchNode> expand() {
		ArrayList<SearchNode> children = new ArrayList<>();
		for (int dir = 0; dir < 4; dir++) {
			TilePuzzleNode child = (TilePuzzleNode) childClone();
			if (child.move(dir)) {
				children.add(child);
			}
		}
		return children;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int digits = ("" + (size * size - 1)).length();
		String fmt = "%" + digits + "d ";
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				sb.append(String.format(fmt, tile[r][c]));
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public TilePuzzleNode clone() {
		TilePuzzleNode copy = (TilePuzzleNode) super.clone();
		copy.tile = new int[size][size];
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				copy.tile[r][c] = tile[r][c];
			}
		}
		return copy;
	}
	
	public boolean equals(SearchNode node) {
		
		TilePuzzleNode other = (TilePuzzleNode) node;
		
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				if (tile[r][c] != other.tile[r][c]) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		TilePuzzleNode root = new TilePuzzleNode(4, 12);
		System.out.println(root);
		System.out.println("Children:");
		System.out.println(root.expand());
	}

}
