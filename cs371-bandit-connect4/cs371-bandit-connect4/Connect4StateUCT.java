import java.util.ArrayList;

public class Connect4StateUCT extends Connect4State implements Cloneable {

	public UCTConnect4 uct = new UCTConnect4();
	public UCTConnect4.Node node;
	public static int UCT_ITERATIONS = 100000; 

	public Connect4StateUCT(int rows, int columns) {
		super(rows, columns);
		uct.setRows(rows);
		uct.setCols(columns);
		uct.setInARow(4);
		uct.setIterations(UCT_ITERATIONS);
		node = uct.getUCTTree(node, player, board, 0);
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
		node = uct.getUCTTree(node, player, board, UCT_ITERATIONS);
		return node.legalMoves[node.getBestMoveIndex()];
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
		// advance UCT node
		int moveIndex = 0;
		while (node.legalMoves[moveIndex] != column)
			moveIndex++;
		if (node.children[moveIndex] == null)
			node.children[moveIndex] = uct.getNode((player == BLACK) ? RED : BLACK, board, node.depth + 1, column);
		//UCTConnect4.Node parent = node;
		node = node.children[moveIndex];
		//parent.clear();
		// check for winner
		if (hasFourInARow(row, column, 0, 1) || hasFourInARow(row, column, 1, 0) 
				|| hasFourInARow(row, column, 1, 1) || hasFourInARow(row, column, 1, -1))
			winner = player;
		// change player
		player = (player == BLACK) ? RED : BLACK;
		return true;
	}

	public String toString() {
		boolean showDetails = true;
		StringBuilder sb = new StringBuilder(uct.boardToString(player, board));
		if (showDetails)
			sb.append(node.toString());
		return sb.toString();
		
	}
}
