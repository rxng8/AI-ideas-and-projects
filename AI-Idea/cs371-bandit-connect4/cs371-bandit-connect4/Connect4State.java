import java.util.ArrayList;
import java.util.Random;

/**
 * <code>Connect4State</code> - a simple state representation for playing basic Connect 4. See
 * http://en.wikipedia.org/wiki/Connect_Four
 *
 * @author Todd W. Neller
 * @version 1.0
 * *** DO NOT DISTRIBUTE ***
 */

public class Connect4State implements Cloneable {

	/**
	 * constant meaning black player/piece. Black is first to play.
	 */
	public final static int BLACK = 1; 
	
	/**
	 * constant meaning red player/piece. Red is second to play.
	 */
	public final static int RED = -1;
	
	/**
	 * constant meaning no winner/piece.
	 */
	public final static int NONE = 0;
	
	/**
	 * random number generator
	 */
	private final static Random random = new Random();
	
	/**
	 * number of Monte Carlo trials per move for simple AI
	 */
	private final static int TRIALS = 10000;

	/**
	 * number of board rows
	 */
	private int rows;
	
	/**
	 * number of board columns
	 */
	private int cols;
	
	/**
	 * game board.  The bottom row has index 0.
	 */
	protected int[][] board;
	
	/**
	 * current player, initially BLACK.
	 */
	protected int player = BLACK;
	
	/**
	 * current winner, initially NONE.
	 */
	protected int winner = NONE;

	/**
	 * Initialize an empty game board with the given number of rows and columns, with the <code>BLACK</code> player to start.
	 * @param rows number of board rows
	 * @param columns number of board columns
	 */
	public Connect4State(int rows, int columns) {
		this.rows = rows;
		this.cols = columns;
		board = new int[rows][cols]; // initially all positions are NONE (0) by default
	}

	/**
	 * Return the piece (<code>BLACK</code>, <code>RED</code>, <code>NONE</code>) at the given board <code>row</code> and <code>column</code>.  
	 * Row and column indices are zero-based.
	 * Out-of-bounds rows/columns yield a <code>NONE</code> return value.
	 * @param row board row
	 * @param column board column
	 * @return the piece (<code>BLACK</code>, <code>RED</code>, <code>NONE</code>) at the given board <code>row</code> and <code>column</code>
	 */
	public int getPiece(int row, int column) {
		if (row >= 0 && row < rows && column >= 0 && column < cols)
			return board[row][column];
		else
			return NONE;
	}

	/**
	 * Return the winner (<code>BLACK</code>, <code>RED</code>), or <code>NONE</code> if no winner exists, that is, there is no current 4-in-a-row. 
	 * @return the winner (<code>BLACK</code>, <code>RED</code>), or <code>NONE</code> if no winner exists, that is, there is no current 4-in-a-row. 
	 */
	public int getWinner() {
		return winner;
	}
	
	/**
	 * Return the current player.  The player changes on a successful call to <code>playColumn(int)</code>.
	 * @return the current player
	 */
	public int getPlayer() {
		return player;
	}

	/**
	 * Return the number of board rows.
	 * @return the number of board rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Return the number of board columns.
	 * @return the number of board columns
	 */
	public int getColumns() {
		return cols;
	}

	/**
	 * Return an ArrayList of legal play columns.  If this list is empty, then there are no legal plays
	 *   and <code>getWinner()</code> indicates the game outcome.  Column indices are zero-based.
	 * @return a list of legal play columns (possibly empty)
	 */
	public ArrayList<Integer> getPlayColumns() {
		ArrayList<Integer> legalColumns = new ArrayList<Integer>();
		if (winner == NONE)
			for (int c = 0; c < cols; c++)
				if (canPlayColumn(c))
					legalColumns.add(c);
		return legalColumns;
	}
	
	/**
	 * Return a legal column for a computer player play, or -1 if no legal play exists.  The play should meet the following specification: 
	 * First, an immediate winning play for the player should be chosen if such play(s) exist.  
	 * Second, a block of an immediate winning play for the opponent should be chosen if such play(s) exist.
	 * Otherwise, any legal play is allowed, although good plays are more interesting and enjoyable.
	 * Column indices are zero-based.
	 * @return a legal column for a computer player play, or -1 if no legal play exists
	 */
	 public int getPlayColumn() {
		if (isGameOver())
			return -1;
		ArrayList<Integer> legalColumns = getPlayColumns();
		for (int c : legalColumns) {
			Connect4State copy = (Connect4State) this.clone();
			if (copy.playColumn(c) && copy.getWinner() == player)
				return c; // play win
		}
		for (int c : legalColumns) {
			player = -player; // change player
			Connect4State copy = (Connect4State) this.clone();
			player = -player; // change player back
			if (copy.playColumn(c) && copy.getWinner() == - player)
				return c; // play block
		}
		boolean randomComputerPlay = false;
		if (randomComputerPlay) 
			return legalColumns.get(random.nextInt(legalColumns.size()));
		else {
			int bestColumn = 0;
			int bestTotal = Integer.MIN_VALUE;
			for (int c : legalColumns) {
				int total = 0;
				for (int t = 0; t < TRIALS; t++) {
					Connect4State simState = (Connect4State) this.clone();
					simState.playColumn(c);
					while (!simState.isGameOver()) {
						ArrayList<Integer> nextLegalColumns = simState.getPlayColumns();
						simState.playColumn(nextLegalColumns.get(random.nextInt(nextLegalColumns.size())));
					}
					total = total + player * simState.getWinner();
				}
				System.out.printf("col %d score %d\n", c, total);
				if (total >= bestTotal) {
					bestTotal = total;
					bestColumn = c;
				}
			}
			return bestColumn;
		}
	}

	/**
	 * Return whether or not the game is over.  If the game is over, then <code>getWinner()</code> yields the game outcome.
	 * @return whether or not the game is over
	 */
	public boolean isGameOver() {
		if (winner != NONE)
			return true;
		for (int c = 0; c < cols; c++)
			if (board[rows - 1][c] == NONE)
				return false;
		return true;
	}

	/**
	 * Return whether or not there is a four-in-a-row through the given <code>row</code> and <code>column</code> in the given direction, 
	 * stepping by change in row (<code>dRow</code>) and column (<code>dColumn</code>).  Row and column indices are zero-based.
	 * @param row row position through which a four-in-a-row is checked 
	 * @param column column position through which a four-in-a-row is checked
	 * @param dRow stepwise change in the row for the direction being search through the given <code>row</code> and <code>column</code>
	 * @param dColumn stepwise change in the column for the direction being search through the given <code>row</code> and <code>column</code>
	 * @return whether or not there is a four-in-a-row through the given <code>row</code> and <code>column</code> in the given direction, 
	 * stepping by change in row (<code>dRow</code>) and column (<code>dColumn</code>).
	 */
	protected boolean hasFourInARow(int row, int column, int dRow, int dColumn) {
		int color = board[row][column];
		if (color == NONE)
			return false;
		while (row - dRow >= 0 && row - dRow < rows && column - dColumn >= 0 && column - dColumn < cols 
				&& board[row - dRow][column - dColumn] == color) {
			row -= dRow;
			column -= dColumn;
		}
		int lastRow = row + 3 * dRow;
		int lastCol = column + 3 * dColumn;
		if (lastRow < 0 || lastRow >= rows || lastCol < 0 || lastCol >= cols)
			return false;
		for (int i = 1; i <= 3; i++)
			if (board[row + i * dRow][column + i * dColumn] != color)
				return false;
		return true;
	}

	/**
	 * Return whether or not it is legal to play in the given column.  No play is legal once a player has won.
	 * @param column
	 * @return whether or not it is legal to play in the given column
	 */
	public boolean canPlayColumn(int column) {
		if (winner != NONE ||column < 0 || column >= cols || board[rows - 1][column] != NONE)
			return false;
		return board[rows - 1][column] == NONE;
	}

	/**
	 * Return whether or not the play in the given <code>column</code> was legal, making a play for the player and changing the player if it indeed was legal.
	 * @param column column indicated for play by the current player
	 * @return whether or not the play in the given <code>column</code> was legal
	 */
	public boolean playColumn(int column) {
		if (!getPlayColumns().contains(column))
			return false;
		// play piece
		int row = 0;
		while (board[row][column] != NONE)
			row++;
		board[row][column] = player;
		// check for winner
		if (hasFourInARow(row, column, 0, 1) || hasFourInARow(row, column, 1, 0) 
				|| hasFourInARow(row, column, 1, 1) || hasFourInARow(row, column, 1, -1))
			winner = player;
		// change player
		player = (player == BLACK) ? RED : BLACK;
		return true;
	}

	/** 
	 * Return a String representation of the board.  <B>There should be no printing in this method.  Build and return a String.</B> 
	 * Rows should be presented in reverse order with row 0 presented last.  
	 * Columns should be presented in ascending order.  Each board position should be presented as a space followed by "*", "o", and "." for 
	 * <code>BLACK</code>, <code>RED</code>, and <code>NONE</code>, respectively.  Finally, a line of column indices should be presented, each
	 * right-justified within two characters width.  Columns indices are zero-based.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int r = rows - 1; r >= 0; r--) {
			for (int c = 0; c < cols; c++) 
				if (board[r][c] == BLACK)
					sb.append(" *");
				else if (board[r][c] == RED)
					sb.append(" o");
				else
					sb.append(" .");
			sb.append("\n");
		}
		for (int c = 0; c < cols; c++)
			sb.append(String.format("%2d", c));
		sb.append("\n");
		return sb.toString();
	}

	/**
	 * Return a copy of this object that is independent of this object, that is, changes to one do not affect the other.  For the first assignment part, simply return <code>null</code>.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try {
			Connect4State copy = (Connect4State) super.clone();
			copy.board = new int[rows][cols];
			for (int r = 0; r < rows; r++)
				copy.board[r] = board[r].clone();
			return copy;
		}
		catch (CloneNotSupportedException e) {
			// Should be unreachable
			e.printStackTrace(System.err);
			return null;
		}
	}
}
