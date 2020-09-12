import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class RookJumpingMaze implements State {

	RookJumpingMaze parent;
	Random r;
	static int UNREACHED = -1000000;
	
	public int size;
	public int[][] maze;
	
	public int curRow;
	public int curCol;
	public int distance;
	public int depth, nMoves;
	
	public int lastJump, lastRow, lastCol;
	
	public RookJumpingMaze(int size) {
		maze = new int [size][size];
		this.size = size;
		r = new Random();
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				// get max length grid that the value can move.
				int[] moveDistance = {row, col, size - 1 - row, size - 1 - col};
				int maxDistance = Integer.MIN_VALUE;
				for (int d : moveDistance) {
					if (d > maxDistance) {
						maxDistance = d;
					}
				}
				// range 1 -> maxdistance inclusive
				maze[row][col] = r.nextInt(maxDistance) + 1;
			}
		}
		curRow = 0;
		curCol = 0;
		distance = 0;
		depth = 0;
		nMoves = 0;
	}

	@Override
	public void step() {
		
		int newRow = r.nextInt(size);
		int newCol = r.nextInt(size);
		while(newCol == size - 1 && newRow == size - 1) {
			newRow = r.nextInt(size);
			newCol = r.nextInt(size);
		}
		
		lastRow = newRow;
		lastCol = newCol;
		lastJump = getJump(newRow, newCol);
		// get max length grid that the value can move.
		int[] moveDistance = {newRow, newCol, size - 1 - newRow, size - 1 - newCol};
		int maxDistance = Integer.MIN_VALUE;
		for (int d : moveDistance) {
			if (d > maxDistance) {
				maxDistance = d;
			}
		}
		int newJump = r.nextInt(maxDistance) + 1;
		while (newJump == getJump(newRow, newCol)) {
			newJump = r.nextInt(maxDistance) + 1;
		}
		maze[newRow][newCol] = newJump;
	}
	
	public int getJump(int row, int col) {
		return maze[row][col];
	}
	
	public int getSize() {
		return size;
	}

	@Override
	public void undo() {
		maze[lastRow][lastCol] = lastJump;
	}

	@Override
	public double energy() {
		return (double) bfs(size - 1, size - 1);
	}
	
//	private int bfs(int goalRow, int goalCol) {
//		
//		boolean[][] check = new boolean[size][size];
//		Queue<int[]> queue = new LinkedList<>();
//		queue.add(new int[] {0, 0});
//		
//		while(true) {
//			if (queue.isEmpty()) {
//				break;
//			}
//			
//			int[] curId = queue.remove();
//			check[curId[0]][curId[1]] = true;
//			
//			// If is goal
//			if (curId[0] == goalRow && curId[1] == goalCol) {
//				break;
//			}
//			
//			
//			// Else expand
//			// If it's possible add the new id to the queue
//			int jump = getJump(curId[0], curId[1]);
//			int[] moveN = {curId[0] - jump, curId[1]};
//			int[] moveS = {curId[0] + jump, curId[1]};
//			int[] moveE = {curId[0], curId[1] - jump};
//			int[] moveW = {curId[0], curId[1] + jump};
//			
//			int[][] uncheckedMoves = {moveN, moveS, moveE, moveW};
//			for (int[] move : uncheckedMoves) {
//				// check valid
//				if (move[0] < size && move[1] < size && move[0] > 0 && move[1] > 0 && !check[move[0]][move[1]]) {
//					queue.add(move);
//				}
//			}
//			
//		}
//		
//		return 0;
//	}
	
	private boolean checkBound (int row, int col) {
		return row >= 0
				&& col >= 0
				&& row < size
				&& col < size;
	}
	
	private int bfs(int goalRow, int goalCol) {
		RookJumpingMaze goal = null;
		boolean[][] check = new boolean[size][size];
		Queue<RookJumpingMaze> queue = new LinkedList<>();
		queue.add(this);
		
		while(true) {
			if (queue.isEmpty()) {
				break;
			}
			
			RookJumpingMaze cur = queue.remove();
			check[cur.curRow][cur.curCol] = true;
			
			// If is goal
			if (cur.curRow == goalRow && cur.curCol == goalCol) {
				goal = cur;
				break;
			}
			
			// Else expand
			// If it's possible add the new id to the queue, 4 directions
			int jump = getJump(cur.curRow, cur.curCol);
			
			RookJumpingMaze moveN = clone();
			moveN.curRow = cur.curRow - jump;
			moveN.nMoves ++;
			moveN.distance += jump;
			if(checkBound(moveN.curRow, moveN.curCol) && !check[moveN.curRow][moveN.curCol]) {
				queue.add(moveN);
			}
			
			RookJumpingMaze moveS = clone();
			moveS.curRow = cur.curRow + jump;
			moveS.nMoves ++;
			moveS.distance += jump;
			if(checkBound(moveS.curRow, moveS.curCol) && !check[moveS.curRow][moveS.curCol]) {
				queue.add(moveS);
			}
			
			RookJumpingMaze moveE = clone();
			moveE.curRow = cur.curCol - jump;
			moveE.nMoves ++;
			moveE.distance += jump;
			if(checkBound(moveE.curRow, moveE.curCol) && !check[moveE.curRow][moveE.curCol]) {
				queue.add(moveE);
			}
			
			RookJumpingMaze moveW = clone();
			moveW.curRow = cur.curCol + jump;
			moveW.nMoves ++;
			moveW.distance += jump;
			if(checkBound(moveW.curRow, moveW.curCol) && !check[moveW.curRow][moveW.curCol]) {
				queue.add(moveW);
			}
		}
		
		if (goal != null) {
			return goal.distance;
		}
		
		return 1000000;
	}
	
	public RookJumpingMaze clone() {
		try {
			RookJumpingMaze copy = (RookJumpingMaze) super.clone();
			copy.parent = this;
			copy.depth = depth + 1;
			copy.size = size;
			copy.curCol = curRow;
			copy.curCol = curCol;
			copy.curCol = distance;
			copy.curCol = nMoves;
			
			return copy;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	public String toString() {
		return null;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}
