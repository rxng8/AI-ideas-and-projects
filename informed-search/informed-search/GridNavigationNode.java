import java.util.ArrayList;

public class GridNavigationNode extends HSearchNode{

	private int startRow, startCol, currRow, currCol, goalRow, goalCol; 
	private boolean[][] isBlocked;
	private double cost;

	/**
	 * Create the root node for the grid navigation search 
	 * @param startRow is the starting row
	 * @param startCol is the starting column
	 * @param goalRow is the goal row
	 * @param goalCol is the goal column
	 * @param isBlocked checks if the entry is blocked
	 */
	public GridNavigationNode(int startRow, int startCol, int goalRow, int goalCol, boolean[][]isBlocked) {
		
		this.startRow = startRow; 
		this.startCol = startCol;
		currRow = startRow; 
		currCol = startCol;
		//NEED TO separate start position and current position
		this.goalRow = goalRow;
		this.goalCol = goalCol;
		this.isBlocked = isBlocked;
		cost = 0;
	}

	/**
	 * Returns the node's gird row
	 * @return	
	 */
	public int getRow() {
		return currRow;
	}

	/**
	 * Returns the node's grid column
	 * @return
	 */
	public int getCol() {
		return currCol;
	}

	/**
	 * Get the total Euclidean distance traveled to reach this node.
	 */
	@Override
	public double getG() {
		return cost;

	}

	/**
	 * Get an admissible estimate of the remaining distance to the goal row and column 
	 * in the form of the Euclidean distance from this node to the goal.
	 */
	@Override
	public double getH() {
		double distRow = Math.abs(currRow - goalRow);
		double distCol = Math.abs(currCol - goalCol);
		double dist = (distRow * distRow) + (distCol * distCol);
		return Math.sqrt(dist);
	}

	/**
	 * Returns whether or not this node is at the goal row and goal column
	 */
	@Override
	public boolean isGoal() {
		return (currRow == goalRow && currCol == goalCol);
	}

	/**
	 * Return a list of successor nodes (a.k.a. children) 
	 * that are within the grid, as yet unvisited, and unblocked.
	 */
	@Override
	public ArrayList<SearchNode> expand() {
		ArrayList<SearchNode> children = new ArrayList<SearchNode>();
		for(int newRow = currRow - 1; newRow <= currRow + 1; newRow++) {
			// check row
			if(newRow < 0 || newRow >= isBlocked.length) {
				continue;
			}
			for(int newCol = currCol - 1; newCol <= currCol + 1; newCol++) {
				// Check index bound
				if(newCol < 0 
						|| newCol >= isBlocked[newRow].length
						|| (newCol == currCol && newRow == currRow)) {
					continue;
				}
				GridNavigationNode child = (GridNavigationNode) childClone();
				if (!isBlocked[newRow][newCol]) {
					child.isBlocked[newRow][newCol] = true;
					child.currCol = newCol;
					child.currRow = newRow;
					
					int disRow = Math.abs(currRow - newRow);
					int disCol = Math.abs(currCol - newCol);
					double dist = Math.sqrt((disRow * disRow) + (disCol * disCol));
					child.cost += dist;
					
					children.add(child);
				}
			}
		}
		return children;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
