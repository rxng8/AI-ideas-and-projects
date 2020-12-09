import java.util.ArrayList;
import java.util.Random;


public class UCTConnect4 {
	public static final int MAX = 1;
	public static final int MIN = -1;
	public static final int EMPTY = 0;
	private int rows = 6;
	private int cols = 7;
	private int inARow = 4;
	private int iterations = 100000;
	private Random random = new Random();

	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @return the cols
	 */
	public int getCols() {
		return cols;
	}

	/**
	 * @param cols the cols to set
	 */
	public void setCols(int cols) {
		this.cols = cols;
	}

	/**
	 * @return the inARow
	 */
	public int getInARow() {
		return inARow;
	}

	/**
	 * @param inARow the inARow to set
	 */
	public void setInARow(int inARow) {
		this.inARow = inARow;
	}

	/**
	 * @return the iterations
	 */
	public int getIterations() {
		return iterations;
	}

	/**
	 * @param iterations the iterations to set
	 */
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public int evalBoard(int[][] board, int lastMove) {
		if (lastMove < 0 || board[0][lastMove] == EMPTY)
			return 0;
		final int[] dRows = {0, 1, 1, 1};
		final int[] dCols = {1, 1, 0, -1};
		int initCol = lastMove;
		int initRow = 0;
		while (initRow < rows && board[initRow][initCol] != EMPTY)
			initRow++;
		initRow--;
		for (int dir = 0; dir < dRows.length; dir++) {
			int dRow = dRows[dir];
			int dCol = dCols[dir];
			int r = initRow;
			int c = initCol;
			while (r >= 0 && r < rows && c >= 0 && c < cols) {
				r -= dRow;
				c -= dCol;
			}
			r += dRow;
			c += dCol;
			int rBack = r - inARow * dRow;
			int cBack = c - inARow * dCol;
			int sum = 0;
			while (r >= 0 && r < rows && c >= 0 && c < cols) {
				sum += board[r][c];
				if (rBack >= 0 && rBack < rows && cBack >= 0 && cBack < cols)
					sum -= board[rBack][cBack];
				if (sum == inARow)
					return MAX;
				else if (sum == - inARow)
					return MIN;
				r += dRow;
				rBack += dRow;
				c += dCol;
				cBack += dCol;
			}
		}
		return 0;
	}

	public Node getUCTTree(Node root, int player, int[][] board, int iterations) {
		if (root == null)
			root = new Node(player, board, 0, -1);
		for (int i = 0; i < iterations; i++) 
			root.iterateUCT(player, copyBoard(board), -1);
		return root;
	}
	
	public Node getNode(int player, int[][] board, int depth, int lastMove) {
		return new Node(player, board, depth, lastMove);
	}

	class Node {
		int depth;
		int visits;
		int[] legalMoves;
		int[] selections;
		double[] q;
		Node[] children;
		double eval;

		public Node(int player, int[][] board, int depth, int lastMove) {
			this.depth = depth;
			eval = evalBoard(board, lastMove);
			if (eval != MAX && eval != MIN) { 
				legalMoves = getLegalMoves(board);
				selections = new int[legalMoves.length];
				q = new double[legalMoves.length];
				children = new Node[legalMoves.length];
			}
		}
		
		public void clear() {
			visits = 0;
			selections = new int[selections.length];
			q = new double[q.length];
			children = new Node[children.length];
		}

		public double iterateUCT(int player, int[][] board, int lastMove) {
//			int testDepth = 0;
			visits++;
			if (eval == MAX || eval == MIN)
				return eval;
			int moveIndex = getNewMoveIndex();
			if (moveIndex == -1) { // All moves tried once, all children generated
				moveIndex = getMoveIndex();
				if (moveIndex == -1) { // No legal moves, draw.
					return eval;
				}
				selections[moveIndex]++;
				int nextMove = legalMoves[moveIndex];
				makeMove(player, board, nextMove);
				double childEval = children[moveIndex].iterateUCT(- player, board, nextMove);
				q[moveIndex] += (player * childEval - q[moveIndex]) / selections[moveIndex];
//				if (depth == testDepth) System.out.print(this);
				return childEval;
			}
			else { // new untried move, create new node and randomly play out.
				selections[moveIndex]++;
				int nextMove = legalMoves[moveIndex];
				makeMove(player, board, nextMove);
				int nextPlayer = - player;
				children[moveIndex] = new Node(nextPlayer, board, depth + 1, nextMove);
				double nextEval = evalBoard(board, nextMove);
				while (nextEval != MAX && nextEval != MIN) {
					int[] nextLegalMoves = getLegalMoves(board);
					if (nextLegalMoves.length == 0)
						break;
					nextMove = nextLegalMoves[random.nextInt(nextLegalMoves.length)];
					makeMove(nextPlayer, board, nextMove);
					nextPlayer = - nextPlayer;
					nextEval = evalBoard(board, nextMove);
				}
//				if (depth == testDepth) printBoard(nextPlayer, board);
				q[moveIndex] += (player * nextEval - q[moveIndex]) / selections[moveIndex];
//				if (depth == testDepth) System.out.print(this);
				return nextEval;
			}
		}

		public int getNewMoveIndex() {
			ArrayList<Integer> unmadeMovesList = new ArrayList<Integer>(cols);
			for (int i = 0; i < legalMoves.length; i++)
				if (selections[i] == 0)
					unmadeMovesList.add(i);
			if (unmadeMovesList.size() > 0)
				return unmadeMovesList.get(random.nextInt(unmadeMovesList.size()));
			else
				return -1;
		}

		public int getMoveIndex() {
			if (legalMoves.length == 0)
				return -1;
			ArrayList<Integer> bestMoves = new ArrayList<Integer>(cols);
			bestMoves.add(0);
			double bestQPlus = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < legalMoves.length; i++)
				if (selections[i] > 0) {
					// From Bandit based Monte-Carlo Planning, Levente Kocsis and Csaba Szepesvari, p. 8 (ECML'06)
					int D = rows * cols - depth;
					double exponent = (double) (D + depth) / (2 * D + depth);
					double bias = Math.pow(Math.log(visits)  / selections[i], exponent);
					double qPlus = q[i] + bias;
					if (qPlus > bestQPlus)
						bestMoves.clear();
					if (qPlus >= bestQPlus) {
						bestQPlus = qPlus;
						bestMoves.add(i);
					}
				}
			return bestMoves.get(random.nextInt(bestMoves.size()));
		}		
		
		public int getBestMoveIndex() {
			if (legalMoves.length == 0)
				return -1;
			ArrayList<Integer> bestMoves = new ArrayList<Integer>(cols);
			bestMoves.add(0);
			double bestQ = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < legalMoves.length; i++)
				if (selections[i] > 0) {
					if (q[i] > bestQ)
						bestMoves.clear();
					if (q[i] >= bestQ) {
						bestQ = q[i];
						bestMoves.add(i);
					}
				}
			return bestMoves.get(random.nextInt(bestMoves.size()));
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("Node: depth %d visits %d eval %g\n", depth, visits, eval));
			if (legalMoves != null) {
				sb.append("Column\tSelects\tQ\n");
				for (int i = 0; i < legalMoves.length; i++)
					sb.append(String.format("%d\t%d\t%g\t\n", legalMoves[i], selections[i], q[i]));
			}
			return sb.toString();
		}
	}

	private boolean canMakeMove(int[][] board, int move) {
		return board[rows - 1][move] == EMPTY;
	}

	private int[] getLegalMoves(int[][] board) {
		ArrayList<Integer> moveList = new ArrayList<Integer>(cols);
		int numMoves = 0;
		for (int c = 0; c < cols; c++) {
//			if (board[0][c] != EMPTY) {
//				double eval = evalBoard(board, c);
//				if (eval == MAX || eval == MIN) {
//					int[] legalMoves = new int[0];
//					return legalMoves;
//				}
//			}
			if (canMakeMove(board, c)) {
				moveList.add(c);
				numMoves++;
			}
		}
		int[] legalMoves = new int[numMoves];
		for (int i = 0; i < numMoves; i++)
			legalMoves[i] = moveList.get(i);
		return legalMoves;
	}

	private boolean makeMove(int player, int[][] board, int column) {
		int moveRow = 0;
		while (moveRow < rows && board[moveRow][column] != EMPTY)
			moveRow++;
		if (moveRow == rows)
			return false;
		board[moveRow][column] = player;
		return true;
	}

//	private boolean undoMove(int[][] board, int column) {
//		int moveRow = 0;
//		while (moveRow < rows && board[moveRow][column] != EMPTY)
//			moveRow++;
//		if (moveRow == 0)
//			return false;
//		board[moveRow - 1][column] = EMPTY;
//		return true;
//	}

	public boolean isGameOver(int[][] board) {
		for (int c = 0; c < cols; c++) {
			double eval = evalBoard(board, c);
			if (eval == MAX || eval == MIN)
				return true;
		}
		return getLegalMoves(board).length == 0;
	}
	
	public String boardToString(int player, int[][] board) {
		StringBuilder sb = new StringBuilder();
		char[] pieces = {'O', ' ', 'X'};
		for (int r = rows - 1; r >= 0; r--) {
			for (int c = 0; c < cols; c++) {
				sb.append(pieces[board[r][c] + 1] + " ");
			}
			sb.append("\n");
		}
		for (int c = 0; c < cols; c++)
			sb.append(c + " ");
		System.out.println();
		int eval = 0;
		for (int c = 0; c < cols; c++) {
			eval = evalBoard(board, c);
			if (eval != 0)
				break;
		}
		if (eval != 0)
			sb.append(String.format("\n%s wins.\n", pieces[eval + 1]));
		else if (getLegalMoves(board).length == 0)
			sb.append("\nDraw.\n");
		else
			sb.append(String.format("\n%s to play.\n\n", pieces[player + 1]));
		return sb.toString();
	}

	private int[][] copyBoard(int[][] board) {
		int[][] copy = new int[board.length][board[0].length];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				copy[r][c] = board[r][c];
			}
		}
		return copy;
	}

	private void playGame() {
		int[][] board = new int[rows][cols];
		int player = MAX;
		int move = -1;
		Node node = null;
		while (evalBoard(board, move) == 0) {
			node = getUCTTree(node, player, board, iterations);
			System.out.println(node);
			int moveIndex = node.getBestMoveIndex();
			if (moveIndex == -1)
				break;
			move = node.legalMoves[moveIndex];
			makeMove(player, board, move);
			node = node.children[moveIndex];
			player = - player; // switch player
			System.out.println(boardToString(player, board));
			//System.out.println(evalBoard(board, move));
		}		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new UCTConnect4().playGame();
	}
}
