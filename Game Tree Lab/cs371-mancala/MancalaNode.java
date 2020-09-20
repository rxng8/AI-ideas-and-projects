/** MancalaNode - a mancala game node */

import java.util.ArrayList;

public abstract class MancalaNode extends GameNode
{
	protected static final int MAX_SCORE_PIT = 6;
	protected static final int MIN_SCORE_PIT = 13;
	protected static final int TOTAL_PITS = 14;
	protected static final int PLAY_PITS = 6;
	protected static final int INIT_SEEDS_PER_PIT = 4;
	protected static final int NUM_PIECES = 2 * PLAY_PITS * INIT_SEEDS_PER_PIT;
	/** 
<pre> 
How to interpret the Mancala state variable:

Let the mancala pits be notated thus:

  _ _ _ _ _ _
_ 1 2 3 4 5 6
s             s
  6 5 4 3 2 1

where 
6-1 are the first player's (MAX's) pits,
s1 is the first player's (MAX's) scoring pit,
_ _
6-1 are the second player's (MIN's) pits, and 
_
s is the second player's (MIN's) scoring pit.

The numbers of pieces in each pit are stored in an array as follows:
state[0] ... state[6] store the number of pieces in 6, 5, 4, 3, 2, 1, and s.
                                                     _  _  _  _  _  _      _
state[7] ... state[13] store the number of pieces in 6, 5, 4, 3, 2, 1, and s.

Each player's goal is to end the game with more pieces in one's own
                                                             _
scoring pit.  Thus a simple measure of utility would be (s - s).
</pre> */
	protected int[] state = new int[TOTAL_PITS];

	public MancalaNode() 
	{
		// See general class comments to interpret Mancala state
		state = new int[TOTAL_PITS];
		// four pieces initially in each pit...
		for (int i = 0; i < TOTAL_PITS; i++) 
			state[i] = INIT_SEEDS_PER_PIT;
		// ...except scoring pits.
		state[MAX_SCORE_PIT] = state[MIN_SCORE_PIT] = 0;
		// First player is MAX by default.
		player = MAX;
	}

	/**
	 * Initialize Mancala node with given state piece distribution with MAX to play.  State should
	 * contain a total of NUM_PIECES pieces.
	 * @param state given state piece distribution.
	 */
	public MancalaNode(int[] state) {
		this();
		this.state = state.clone();
	}

	public MancalaNode(int fairStateIndex) {
		this();
		if (fairStateIndex < 0 || fairStateIndex >= fairkalahStates.length)
			fairStateIndex = (int) (fairkalahStates.length * Math.random());
		this.state = fairkalahStates[fairStateIndex].clone();
	}

	public MancalaNode(MancalaNode node) {
		this.state = (int[]) node.state.clone();
		this.player = node.player;
		this.prevMove = node.prevMove;
		this.parent = node.parent;
	}


	/**
	 * <code>clone</code> - return a deep clone of the
	 * MancalaNode.
	 *
	 * @return an <code>Object</code> value - a deep clone of the
	 * MancalaNode.*/
	public Object clone() {
		MancalaNode newNode = (MancalaNode) super.clone();
		newNode.state = (int[]) state.clone();
		return newNode;
	}        

	/**
	 * <code>gameOver</code> - return true if no pieces left in
	 * play pits.
	 *
	 * @return a <code>boolean</code> value */
	public boolean gameOver() {
		return (state[MAX_SCORE_PIT] + state[MIN_SCORE_PIT] == NUM_PIECES);
	}


	public ArrayList<GameNode> expand() {
		ArrayList<GameNode> children = new ArrayList<GameNode>();
		for (int move : getLegalMoves()) {
			MancalaNode child = (MancalaNode) childClone();
			child.makeMove(move);
			children.add(child);
		}
		return children;
	}

	public ArrayList<Integer> getLegalMoves() 
	{
		ArrayList<Integer> legalMoves = new ArrayList<Integer>();
		final int PLAYER_OFFSET = (player == MAX) ? 0 : MAX_SCORE_PIT + 1;
		for (int i = PLAYER_OFFSET; i < PLAYER_OFFSET + PLAY_PITS; i++)
			if (state[i] > 0)
				legalMoves.add(i);
		return legalMoves;
	}


	/**
	 * Make the designated move, redistributing pieces from the indicated position and
	 * updating player accordingly.
	 * @param move int value of play pit from which pieces are being moved from.
	 */
	public void makeMove(int move) {
		int position = move, scorePit, oppositePit;

		prevMove = move;

		// Take the pieces from the indicated pit.
		int pieces = state[position];
		state[position] = 0;

		// Redistribute them around the pits, skipping the opponent's scoring pit.
		while (pieces > 0) {
			position = (position + 1) % TOTAL_PITS;

			// Skip over opponent's scoring pit
			if (position == ((player == MAX) ? MIN_SCORE_PIT : MAX_SCORE_PIT))
				continue;

			// Distribute piece
			state[position] ++;
			pieces--;
		}

		// If the last piece distributed landed in an empty pit on
		// one's side, capture both the last
		// piece and any pieces opposite.
		scorePit = (player == MAX) ? MAX_SCORE_PIT : MIN_SCORE_PIT;
		// if last piece distributed in empty pit on own side
		if (state[position] == 1 && (scorePit - position) > 0 
				&& (scorePit - position <= PLAY_PITS)) { // last piece into empty play pit
			oppositePit = MIN_SCORE_PIT - position - 1;
			// capture own pit
			state[scorePit] ++;
			state[position] --;
			// capture opposite pit
			state[scorePit] += state[oppositePit];
			state[oppositePit] = 0;
		}

		// If the last piece did not land in one's scoring pit, then
		// the player changes.
		if (position != scorePit)
			player = (player == MAX) ? MIN : MAX;

		// Check for starvation.  If next player has no moves (is
		// starved), their opponent scores all remaining pieces.
		boolean starved = true; // assume next player starved
		scorePit = (player == MAX) ? MAX_SCORE_PIT : MIN_SCORE_PIT; // set scoring pit of next player
		for (position = scorePit - PLAY_PITS; starved && position < scorePit; position++)
			if (state[position] > 0)
				starved = false; // flag starved false if any next player pits contain anything
		if (starved) { // if next player starved, then opponent scores all remaining pieces.
			scorePit = (scorePit + PLAY_PITS + 1) % TOTAL_PITS;
			for (position = scorePit - PLAY_PITS; starved && position < scorePit; position++) {
				state[scorePit] += state[position];
				state[position] = 0;
			}
		}
	}

	/**
	 * Translates move integer to a String.
	 * @return java.lang.String
	 */
	public static String moveToString(int move) {
		final String[] moveString = { "6", "5", "4", "3", "2", "1", "INVALID MOVE", "6", "5", "4", "3", "2", "1" };
		if ((move < 0) && (move >= MIN_SCORE_PIT)) 
			move = MAX_SCORE_PIT;
		return moveString[move];
	}

	/**
	 * String representation of current game state.
	 * Example (initial state):
	 *      _  _  _  _  _  _
	 *      1  2  3  4  5  6
	 * -------------------------
	 * |  | 4| 4| 4| 4| 4| 4|  |
	 * | 0|-----------------| 0|
	 * |  | 4| 4| 4| 4| 4| 4|  | <--
	 * -------------------------
	 *      6  5  4  3  2  1
	 *
	 * @return String representation of current game state
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("     _  _  _  _  _  _\n     1  2  3  4  5  6\n-------------------------\n|  ");
		for (int i = MIN_SCORE_PIT - 1; i > MAX_SCORE_PIT; i--)
			if (state[i] > 9)
				sb.append("|" + state[i]);
			else
				sb.append("| " + state[i]);
		sb.append("|  |");
		if (player == MIN)
			sb.append(" <--");
		if (state[MIN_SCORE_PIT] > 9)
			sb.append("\n|" + state[MIN_SCORE_PIT]);
		else
			sb.append("\n| " + state[MIN_SCORE_PIT]);
		sb.append("|-----------------|");
		if (state[MAX_SCORE_PIT] > 9)
			sb.append(state[MAX_SCORE_PIT]);
		else
			sb.append(" " + state[MAX_SCORE_PIT]);
		sb.append("|\n|  ");
		for (int i = 0; i < MAX_SCORE_PIT; i++)
			if (state[i] > 9)
				sb.append("|" + state[i]);
			else
				sb.append("| " + state[i]);
		sb.append("|  |");
		if (player == MAX)
			sb.append(" <--");
		sb.append("\n-------------------------\n     6  5  4  3  2  1\n");
		return sb.toString();
	}

	/**
	 * Return an estimation of game node utility, unless game is
	 * over.  If game is over, return actual utility.
	 *
	 * NOTE: In your implementation, you must create your own
	 * subclass of MancalaNode (e.g. UserIDMancalaNode extends
	 * MancalaNode) and implement this utility method (inheriting
	 * all others).  This can be confusing if you don't know Java
	 * well, so see the example I've provided with
	 * TnellerMancalaNode.
	 *
	 * Example implementation:
	 * public double utility() {
	 *   return state[MAX_SCORE_PIT]-state[MIN_SCORE_PIT];
	 * }
	 *
	 * @return double an estimation of game node utility, unless game is
	 * over.  If game is over, return actual utility.
	 */
	public abstract double utility();

	private static int[][] fairkalahStates = {
			{2,4,4,4,4,4,0,4,4,4,4,4,5,1},
			{2,4,4,4,4,4,0,4,4,4,5,4,5,0},
			{2,4,4,4,4,5,0,4,4,4,4,4,5,0},
			{2,5,5,4,4,4,0,4,4,4,4,4,4,0},
			{3,3,5,4,4,4,0,5,4,4,4,4,4,0},
			{3,4,4,4,4,3,0,4,4,4,4,6,4,0},
			{3,4,4,4,4,4,0,4,4,3,4,4,4,2},
			{3,4,4,4,4,4,0,4,4,3,4,4,5,1},
			{3,4,4,4,4,4,0,4,4,3,4,5,5,0},
			{3,4,4,4,4,4,0,4,4,3,5,4,4,1},
			{3,4,4,4,4,4,0,4,4,3,5,4,5,0},
			{3,4,4,4,4,4,0,4,4,3,6,4,4,0},
			{3,4,4,4,4,4,0,4,4,4,3,6,4,0},
			{3,4,4,4,4,4,0,5,4,3,4,4,4,1},
			{3,4,4,4,4,4,0,5,4,3,4,5,4,0},
			{3,4,4,4,4,4,1,4,4,3,4,5,4,0},
			{3,4,4,4,4,5,0,4,4,3,4,4,4,1},
			{3,4,4,4,4,5,0,4,4,3,4,4,5,0},
			{3,4,4,4,4,5,0,4,4,3,4,5,4,0},
			{3,4,4,4,5,4,0,4,4,3,4,4,4,1},
			{3,4,4,4,5,4,0,4,4,3,4,4,5,0},
			{3,4,4,4,5,4,0,4,4,3,5,4,4,0},
			{3,4,4,4,5,5,0,4,4,3,4,4,4,0},
			{3,4,4,4,6,4,0,4,4,3,4,4,4,0},
			{3,4,4,5,4,4,0,4,4,3,4,4,4,1},
			{3,4,4,5,4,4,0,4,4,3,4,4,5,0},
			{3,4,4,5,4,4,0,4,4,3,4,5,4,0},
			{3,4,4,5,4,4,0,4,4,3,5,4,4,0},
			{3,4,4,6,4,4,0,4,4,3,4,4,4,0},
			{3,4,5,4,3,4,0,4,4,4,4,4,4,1},
			{3,4,5,4,3,4,0,4,4,4,4,5,4,0},
			{3,4,5,4,3,4,0,4,4,4,5,4,4,0},
			{3,4,5,4,3,4,0,5,4,4,4,4,4,0},
			{3,4,5,4,3,5,0,4,4,4,4,4,4,0},
			{3,4,5,4,4,4,0,3,4,4,4,4,4,1},
			{3,4,5,4,4,4,0,3,4,4,4,4,5,0},
			{3,4,5,4,4,4,0,3,4,4,4,5,4,0},
			{3,4,5,4,4,4,0,4,3,4,4,4,5,0},
			{3,4,5,4,4,4,0,4,3,4,4,5,4,0},
			{3,4,5,4,4,4,0,4,3,4,5,4,4,0},
			{3,4,5,4,4,4,0,4,4,4,4,3,5,0},
			{3,4,5,4,4,4,0,4,4,4,5,3,4,0},
			{3,4,5,4,4,5,0,4,3,4,4,4,4,0},
			{3,4,5,4,4,5,0,4,4,4,4,3,4,0},
			{3,4,5,4,5,3,0,4,4,4,4,4,4,0},
			{3,4,5,4,5,4,0,3,4,4,4,4,4,0},
			{3,4,5,5,3,4,0,4,4,4,4,4,4,0},
			{3,4,6,4,3,4,0,4,4,4,4,4,4,0},
			{3,4,6,4,4,4,0,3,4,4,4,4,4,0},
			{3,5,4,4,4,4,0,4,4,3,4,4,4,1},
			{3,5,4,4,4,4,0,4,4,3,4,4,5,0},
			{3,5,4,4,4,4,0,4,4,3,4,5,4,0},
			{3,5,4,4,4,5,0,4,4,3,4,4,4,0},
			{3,5,4,4,5,4,0,4,4,3,4,4,4,0},
			{3,5,5,4,3,4,0,4,4,4,4,4,4,0},
			{3,5,5,4,4,4,0,4,4,4,3,4,4,0},
			{3,6,4,4,3,4,0,4,4,4,4,4,4,0},
			{3,6,4,4,4,3,0,4,4,4,4,4,4,0},
			{3,6,4,4,4,4,0,4,4,4,3,4,4,0},
			{4,2,5,4,4,4,0,4,4,4,4,4,5,0},
			{4,2,5,4,4,4,0,4,4,4,4,5,4,0},
			{4,2,5,4,4,4,0,5,4,4,4,4,4,0},
			{4,2,5,4,4,4,1,4,4,4,4,4,4,0},
			{4,2,5,5,4,4,0,4,4,4,4,4,4,0},
			{4,2,6,4,4,4,0,4,4,4,4,4,4,0},
			{4,3,4,4,4,4,0,4,4,3,4,5,4,1},
			{4,3,4,4,4,4,0,4,4,3,4,5,5,0},
			{4,3,4,4,4,4,0,4,4,3,4,6,4,0},
			{4,3,4,4,4,4,0,4,4,3,5,4,4,1},
			{4,3,4,4,4,5,0,5,4,3,4,4,4,0},
			{4,3,4,4,5,4,0,4,4,3,4,5,4,0},
			{4,3,4,5,4,4,0,4,4,3,4,4,4,1},
			{4,3,4,5,4,4,0,4,4,3,4,5,4,0},
			{4,3,4,5,4,5,0,4,4,3,4,4,4,0},
			{4,3,4,5,5,4,0,4,4,3,4,4,4,0},
			{4,3,5,4,3,4,0,5,4,4,4,4,4,0},
			{4,3,5,4,4,3,0,4,5,4,4,4,4,0},
			{4,3,5,4,4,3,0,5,4,4,4,4,4,0},
			{4,3,5,4,4,4,0,4,3,4,5,4,4,0},
			{4,3,5,4,4,4,0,4,4,3,4,4,5,0},
			{4,3,5,4,4,4,0,4,4,3,5,4,4,0},
			{4,3,5,4,4,4,0,4,4,4,4,3,4,1},
			{4,3,5,4,4,4,0,4,4,4,4,3,5,0},
			{4,3,5,4,4,4,0,4,4,4,4,4,3,1},
			{4,3,5,4,4,4,0,4,4,4,5,3,4,0},
			{4,3,5,4,4,4,0,4,4,5,3,4,4,0},
			{4,3,5,4,4,4,0,5,3,4,4,4,4,0},
			{4,3,5,4,4,4,0,5,4,3,4,4,4,0},
			{4,3,5,4,4,4,0,5,4,4,4,3,4,0},
			{4,3,5,4,4,4,1,4,3,4,4,4,4,0},
			{4,3,5,4,4,4,1,4,4,3,4,4,4,0},
			{4,3,5,4,4,5,0,4,3,4,4,4,4,0},
			{4,3,5,4,4,5,0,4,4,4,4,3,4,0},
			{4,3,5,4,5,3,0,4,4,4,4,4,4,0},
			{4,3,5,4,5,4,0,4,4,3,4,4,4,0},
			{4,3,5,5,3,4,0,4,4,4,4,4,4,0},
			{4,3,5,5,4,4,0,4,4,3,4,4,4,0},
			{4,3,6,4,3,4,0,4,4,4,4,4,4,0},
			{4,3,6,4,4,4,0,4,3,4,4,4,4,0},
			{4,3,6,4,4,4,0,4,4,3,4,4,4,0},
			{4,4,3,4,4,4,0,4,4,4,3,4,5,1},
			{4,4,3,4,4,4,0,4,4,4,3,4,6,0},
			{4,4,3,4,4,4,0,5,4,4,3,4,5,0},
			{4,4,3,4,4,4,1,5,4,4,3,4,4,0},
			{4,4,4,4,4,3,0,4,4,4,3,4,4,2},
			{4,4,4,4,4,3,0,4,4,4,3,4,6,0},
			{4,4,4,4,4,3,0,5,4,4,3,4,4,1},
			{4,4,4,4,4,4,0,4,4,4,2,4,4,2},
			{4,4,4,4,4,4,0,4,4,4,2,4,6,0},
			{4,4,4,4,4,4,0,5,4,3,3,4,5,0},
			{4,4,4,4,4,4,1,5,4,3,3,4,4,0},
			{4,4,4,4,5,4,0,5,4,4,2,4,4,0},
			{4,4,4,5,4,3,0,4,4,4,3,4,4,1},
			{4,4,4,5,4,4,0,4,4,4,4,2,5,0},
			{4,4,4,5,4,4,0,5,4,3,3,4,4,0},
			{4,4,5,4,3,3,0,5,4,4,4,4,4,0},
			{4,4,5,4,3,4,0,3,4,4,4,5,4,0},
			{4,4,5,4,3,4,0,3,4,4,5,4,4,0},
			{4,4,5,4,3,4,0,4,4,3,4,5,4,0},
			{4,4,5,4,3,4,0,4,4,4,4,4,3,1},
			{4,4,5,4,3,4,0,4,4,4,4,4,4,0},
			{4,4,5,4,3,4,0,5,3,4,4,4,4,0},
			{4,4,5,4,3,4,1,3,4,4,4,4,4,0},
			{4,4,5,4,3,4,1,4,3,4,4,4,4,0},
			{4,4,5,4,3,4,1,4,4,3,4,4,4,0},
			{4,4,5,4,3,5,0,3,4,4,4,4,4,0},
			{4,4,5,4,3,5,0,4,4,4,3,4,4,0},
			{4,4,5,4,4,2,0,4,5,4,4,4,4,0},
			{4,4,5,4,4,3,0,4,4,3,4,5,4,0},
			{4,4,5,4,4,3,0,4,4,3,5,4,4,0},
			{4,4,5,4,4,3,0,5,3,4,4,4,4,0},
			{4,4,5,4,4,3,0,5,4,3,4,4,4,0},
			{4,4,5,4,4,3,1,4,4,3,4,4,4,0},
			{4,4,5,4,4,3,1,4,4,4,4,3,4,0},
			{4,4,5,4,4,3,1,4,4,4,4,4,3,0},
			{4,4,5,4,4,4,0,3,3,4,4,4,5,0},
			{4,4,5,4,4,4,0,4,2,4,4,4,4,1},
			{4,4,5,4,4,4,0,4,2,4,4,5,4,0},
			{4,4,5,4,4,4,0,4,3,3,4,4,5,0},
			{4,4,5,4,4,4,0,4,3,3,4,5,4,0},
			{4,4,5,4,4,4,0,4,3,4,4,3,4,1},
			{4,4,5,4,4,4,0,4,3,4,4,4,4,0},
			{4,4,5,4,4,4,0,4,3,4,4,5,3,0},
			{4,4,5,4,4,4,0,4,3,4,5,3,4,0},
			{4,4,5,4,4,4,0,4,4,2,5,4,4,0},
			{4,4,5,4,4,4,0,4,4,3,4,3,5,0},
			{4,4,5,4,4,4,0,4,4,3,5,3,4,0},
			{4,4,5,4,4,4,0,4,4,5,2,4,4,0},
			{4,4,5,4,4,4,0,4,5,4,2,4,4,0},
			{4,4,5,4,4,4,0,4,5,4,3,3,4,0},
			{4,4,5,4,4,4,0,4,5,4,4,2,4,0},
			{4,4,5,4,4,4,0,5,2,4,4,4,4,0},
			{4,4,5,4,4,4,0,5,3,3,4,4,4,0},
			{4,4,5,4,4,4,0,5,3,4,4,3,4,0},
			{4,4,5,4,4,4,0,5,4,3,4,3,4,0},
			{4,4,5,4,4,4,0,5,4,3,4,4,3,0},
			{4,4,5,4,4,4,1,3,3,4,4,4,4,0},
			{4,4,5,4,4,4,1,3,4,3,4,4,4,0},
			{4,4,5,4,4,4,1,4,3,3,4,4,4,0},
			{4,4,5,4,4,4,1,4,4,2,4,4,4,0},
			{4,4,5,4,4,4,1,4,4,3,4,3,4,0},
			{4,4,5,4,4,4,1,4,4,3,4,4,3,0},
			{4,4,5,4,4,5,0,4,3,4,3,4,4,0},
			{4,4,5,4,4,5,0,4,3,4,4,3,4,0},
			{4,4,5,4,4,5,0,4,4,2,4,4,4,0},
			{4,4,5,4,5,3,0,3,4,4,4,4,4,0},
			{4,4,5,4,5,3,0,4,3,4,4,4,4,0},
			{4,4,5,4,5,3,0,4,4,3,4,4,4,0},
			{4,4,5,4,5,3,0,4,4,4,4,4,3,0},
			{4,4,5,4,5,4,0,4,3,3,4,4,4,0},
			{4,4,5,4,5,4,0,4,4,2,4,4,4,0},
			{4,4,5,4,5,4,0,4,4,3,4,4,3,0},
			{4,4,5,4,5,4,0,4,4,4,2,4,4,0},
			{4,4,5,5,3,4,0,3,4,4,4,4,4,0},
			{4,4,5,5,3,4,0,4,3,4,4,4,4,0},
			{4,4,5,5,4,3,0,3,4,4,4,4,4,0},
			{4,4,5,5,4,3,0,4,4,4,4,4,3,0},
			{4,4,5,5,4,4,0,3,4,3,4,4,4,0},
			{4,4,5,5,4,4,0,4,2,4,4,4,4,0},
			{4,4,5,5,4,4,0,4,3,4,4,3,4,0},
			{4,4,5,5,4,4,0,4,4,3,4,3,4,0},
			{4,4,5,5,4,4,0,4,4,3,4,4,3,0},
			{4,4,5,5,4,4,0,4,4,4,3,4,3,0},
			{4,4,6,4,3,4,0,4,4,3,4,4,4,0},
			{4,4,6,4,3,4,0,4,4,4,4,3,4,0},
			{4,4,6,4,4,3,0,3,4,4,4,4,4,0},
			{4,4,6,4,4,3,0,4,4,3,4,4,4,0},
			{4,4,6,4,4,3,0,4,4,4,4,4,3,0},
			{4,4,6,4,4,4,0,3,3,4,4,4,4,0},
			{4,4,6,4,4,4,0,3,4,3,4,4,4,0},
			{4,4,6,4,4,4,0,3,4,4,3,4,4,0},
			{4,4,6,4,4,4,0,4,3,4,4,3,4,0},
			{4,4,6,4,4,4,0,4,4,2,4,4,4,0},
			{4,4,6,4,4,4,0,4,4,3,4,3,4,0},
			{4,4,6,4,4,4,0,4,4,3,4,4,3,0},
			{4,5,3,4,4,4,0,4,4,3,4,4,4,1},
			{4,5,3,4,4,4,0,4,4,3,4,5,4,0},
			{4,5,3,4,4,4,0,4,4,3,5,4,4,0},
			{4,5,3,5,4,4,0,4,4,3,4,4,4,0},
			{4,5,4,4,3,4,0,4,4,3,4,4,4,1},
			{4,5,4,4,3,4,0,4,4,3,4,4,5,0},
			{4,5,4,4,3,4,0,4,4,3,4,5,4,0},
			{4,5,4,4,3,4,0,5,4,3,4,4,4,0},
			{4,5,4,4,4,3,0,4,4,3,4,4,4,1},
			{4,5,4,4,4,3,0,4,4,3,4,5,4,0},
			{4,5,4,4,4,4,0,4,3,3,4,5,4,0},
			{4,5,4,4,4,4,0,4,4,3,3,4,4,1},
			{4,5,4,4,4,4,0,4,4,3,4,4,4,0},
			{4,5,4,4,4,4,0,4,4,4,2,5,4,0},
			{4,5,4,4,4,4,0,5,4,3,3,4,4,0},
			{4,5,4,4,4,4,0,5,4,4,2,4,4,0},
			{4,5,4,4,5,3,0,4,4,3,4,4,4,0},
			{4,5,4,4,5,4,0,4,4,3,3,4,4,0},
			{4,5,4,5,3,4,0,4,4,3,4,4,4,0},
			{4,5,4,5,4,4,0,4,4,3,3,4,4,0},
			{4,5,5,4,4,4,0,3,4,3,4,4,4,0},
			{4,5,5,4,4,4,0,4,4,2,4,4,4,0},
			{4,5,5,4,4,4,0,4,4,3,4,3,4,0},
			{4,5,5,4,4,4,0,4,4,3,4,4,3,0},
			{4,6,3,4,3,4,0,4,4,4,4,4,4,0},
			{4,6,4,4,3,4,0,4,3,4,4,4,4,0},
			{4,6,4,4,3,4,0,4,4,3,4,4,4,0},
			{4,6,4,4,4,3,0,4,4,3,4,4,4,0},
			{4,6,4,4,4,4,0,3,4,3,4,4,4,0},
			{4,6,4,4,4,4,0,4,3,3,4,4,4,0},
			{4,6,4,4,4,4,0,4,4,2,4,4,4,0},
			{4,6,4,4,4,4,0,4,4,3,4,3,4,0},
			{4,6,4,4,4,4,0,4,4,3,4,4,3,0},
			{4,6,4,4,4,4,0,4,4,4,4,2,4,0},
			{5,3,4,4,4,4,0,4,4,3,4,4,4,1},
			{5,3,5,4,4,4,0,3,4,4,4,4,4,0},
			{5,3,5,4,4,4,0,4,4,4,4,4,3,0},
			{5,4,3,4,4,4,0,4,4,3,4,4,4,1},
			{5,4,3,4,4,4,0,4,4,3,4,4,5,0},
			{5,4,3,4,4,4,1,4,4,4,3,4,4,0},
			{5,4,4,4,4,3,0,4,4,4,3,4,5,0},
			{5,4,4,4,4,3,1,4,4,4,3,4,4,0},
			{5,4,4,4,4,4,0,4,4,2,4,4,4,1},
			{5,4,4,4,4,4,0,4,4,4,2,4,4,1},
			{5,4,4,4,4,4,0,4,4,4,2,4,5,0},
			{5,4,4,4,4,4,0,4,4,4,2,5,4,0},
			{5,4,4,4,4,4,0,5,4,4,2,4,4,0},
			{5,4,4,5,4,4,0,4,4,2,4,4,4,0},
			{5,4,5,4,3,4,0,4,4,4,4,4,3,0},
			{5,4,5,4,4,4,0,2,4,4,4,4,4,0},
			{5,4,5,4,4,4,0,3,4,4,4,3,4,0},
			{5,4,5,4,4,4,0,4,3,4,4,4,3,0},
			{5,4,5,4,4,4,0,4,4,4,4,3,3,0},
			{5,4,5,4,4,4,0,4,4,4,4,4,2,0},
			{6,4,3,4,4,4,0,4,4,3,4,4,4,0},
			{6,4,4,4,3,4,0,4,4,3,4,4,4,0},
			{6,4,4,4,4,4,0,4,4,3,3,4,4,0},
			{6,4,4,4,4,4,0,4,4,3,4,3,4,0},
			{6,4,4,4,4,4,0,4,4,4,2,4,4,0}
	};

}


