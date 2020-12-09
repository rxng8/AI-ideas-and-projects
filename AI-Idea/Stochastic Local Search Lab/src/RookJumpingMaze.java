import java.util.LinkedList;
import java.util.Random;

public class RookJumpingMaze implements State{

	public static int UNREACHED = -1000000;
	public static Random random = new Random();
	public int[][] maze; 
	public int size, curRow, curCol, lastRow, lastCol, jump, lastJump;

	/**
	 * Construct a RookJumpingMaze that is a square grid with random, legal jump number for each position
	 * @param size
	 */
	public RookJumpingMaze(int size) {

		this.size = size;
		maze = new int[size][size];

		for(int r = 0; r<size; r++) {
			for(int c = 0; c<size; c++) {
				//Max value from left to right and up to down
				int max1= (size-r) > (size-c) ? (size-r):(size-c);
				//Max value from right to left and down to up
				int max2 = (r+1) >(c+1)? (r+1):(c+1);
				//Max value of the two
				int max = max1 > max2? max1: max2;
				int value = random.nextInt(max);
				while(value == 0) {
					value = random.nextInt(max);
				}
				maze[r][c] = value;
			}
		}
		//goal state
		maze[size-1][size-1] = 0;
	}

	/**
	 * Returns the jump number for the given row and column
	 * @param row
	 * @param col
	 * @return
	 */
	public int getJump(int row, int col) {
		return maze[row][col];
	}

	/**
	 * Returns the size for the square maze grid
	 * @return
	 */
	public int getSize() {
		return size;
	}

	public double energy() {
		int[][] check = new int [size][size];
		for(int r = 0; r<size; r++)
			for(int c = 0; c<size; c++) 
				check[r][c] = UNREACHED;
		
		int[] start = {0,0,0};
		LinkedList<int[]> queue = new LinkedList<>();
		queue.add(start);
		check[0][0] = 0;
		while(true){
			
			if(queue.isEmpty())
				return -check[size-1][size-1];
			
			int[] node = queue.remove();
			int row = node[0];
			int col = node[1];
			int move = getJump(row, col);
			
			if(move == 0) {
				return -1 * node[2];
			}
			
			if(row + move < size && check[row + move][col] < 0) {
				int[] child = (int[]) node.clone();
				child[0] = row + move;
				child[1] = col;
				child[2] = node[2] + 1;
				queue.add(child);
				check[child[0]][child[1]] = child[2];
			}
			if(col + move < size && check[row][col + move] < 0) {
				int[] child = (int[]) node.clone();
				child[0] = row;
				child[1] = col + move;
				child[2] = node[2] + 1;
				queue.add(child);
				check[child[0]][child[1]] = child[2];
			}
			if(row - move >-1 && check[row - move][col] < 0) {
				int[] child = (int[]) node.clone();
				child[0] = row - move;
				child[1] = col;
				child[2] = node[2] + 1;
				queue.add(child);
				check[child[0]][child[1]] = child[2];
			}
			if(col - move >-1 && check[row][col- move] < 0)  {
				int[] child = (int[]) node.clone();
				child[0] = row;
				child[1] = col - move;
				child[2] = node[2] + 1;
				queue.add(child);
				check[child[0]][child[1]] = child[2];
			}
		}
	}

	public void step() {
		
		curRow = random.nextInt(size);
		curCol = random.nextInt(size);
		while(curRow == size-1 && curCol == size-1) {
			curRow = random.nextInt(size);
			curCol = random.nextInt(size);
		}
		
		lastJump = maze[curRow][curCol];
		lastRow = curRow; 
		lastCol = curCol;
		
		//Max value from left to right and up to down
		int max1= (size-curRow) > (size-curCol) ? (size-curRow):(size-curCol);
		//Max value from right to left and down to up
		int max2 = (curRow+1) >(curCol+1)? (curRow+1):(curCol+1);
		//Max value of the two
		int max = max1 > max2? max1: max2;

		jump = random.nextInt(max);
		while(jump == 0 || jump == maze[curRow][curCol]) {
			jump = random.nextInt(max);
		}
		maze[curRow][curCol] = jump;
	}

	public void undo() {
		maze[lastRow][lastCol] = lastJump; 
	}

	public RookJumpingMaze clone() {
		try {
			RookJumpingMaze copy = (RookJumpingMaze) super.clone();
			copy.maze = new int[size][size];
			for(int r = 0; r<size; r++) {
				for(int c = 0; c<size; c++) {
					copy.maze[r][c] = maze[r][c];
				}
			}
			copy.size = size;
			copy.curRow = curRow; 
			copy.curCol = curCol; 
			copy.lastRow = lastRow;
			copy.lastCol = lastCol;
			copy.jump = jump;
			copy.lastJump = lastJump;
			return copy;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} 
		return null;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int r = 0; r<size; r++) {
			for(int c = 0; c<size; c++) {
				sb.append(maze[r][c] + " ");
			}
			sb.append("\n");
		}
//		sb.append(energy() + "\n");
		return sb.toString();
	}


}