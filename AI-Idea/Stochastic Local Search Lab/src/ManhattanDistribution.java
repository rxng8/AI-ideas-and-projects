import java.util.Random;

public class ManhattanDistribution implements State {

	public static Random random = new Random();
	public int n, rows, cols;
	public int[] row, col; //Item placement index by the item number;
	public int lastItem, lastRow, lastCol;
	
	public ManhattanDistribution(int numItems, int numRows, int numCols) {
		n = numItems;
		rows = numRows;
		cols = numCols;
		row = new int[n];
		col = new int[n];
	}
	
	
	
	@Override
	public void step() {
		// pick a randome item
		
		int i = random.nextInt(n);
		
		// store undo information
		lastItem = i;
		lastRow = row[i];
		lastCol = col[i];
		
		// move item to a random adjacent position
		int minRow = Math.max(0, row[i] - 1);
		int maxRow = Math.min(rows - 1, row[i] + 1);
		int minCol = Math.max(0, col[i] - 1);
		int maxCol = Math.min(cols - 1, col[i] + 1);
		while (row[i] == lastRow && col[i] == lastCol) {
			row[i] = minRow + random.nextInt(maxRow - minRow + 1);
			col[i] = minCol + random.nextInt(maxCol - minCol + 1);
		}
	}

	@Override
	public void undo() {
		row[lastItem] = lastRow;
		col[lastItem] = lastCol;
		
	}

	@Override
	public double energy() {
		int energy = 0;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				int minDist = Integer.MAX_VALUE;
				for (int i = 0; i < n; i++) {
					int dist = Math.abs(r - row[i]) + Math.abs(c - col[i]);
					if (dist < minDist) {
						minDist = dist;
					}
				}
				energy += minDist * minDist;
			}
		}
		return energy;
	}
	
	public Object clone() {
		try {
			ManhattanDistribution copy = (ManhattanDistribution) super.clone();
			copy.row = row.clone();
			copy.col = col.clone();
			return copy;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		char[][] grid = new char[rows][cols];
		
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				grid[r][c] = '-';
			}
		}
		
		for (int i = 0; i < n; i++) {
			grid[row[i]][col[i]] = '#';
		}
		
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				sb.append(grid[r][c] + " ");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		ManhattanDistribution state = new ManhattanDistribution(4, 5, 5);
		System.out.println(state);
	}
	
}
