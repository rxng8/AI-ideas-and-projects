/**
 * TnellerMancalaPlayer - My simple implementation of a mancala
 * player with a simple, messy hack for distributing game-play.
 *
 * @author Todd Neller
 * @version 1.1 */

public class AlexHaoMancalaPlayer implements MancalaPlayer {

	/**
	 * Choose a move for the given game situation given play time
	 * remaining.  */
	public int chooseMove(MancalaNode node, long timeRemaining) {
		// get the current move number
		// start the clock ?
		int nMoves = 0;
		MancalaNode cursor = node;
		while (cursor != null) {
			nMoves++;
			cursor = (MancalaNode) cursor.parent;
		}
		double gaussian = gaussian(nMoves);
		// End the clock ?
		
		final double DEPTH_FACTOR = 1.7;
		int depthLimit 
		= (int) (DEPTH_FACTOR 
				* Math.log((1 + gaussian ) * (double) timeRemaining / piecesRemaining(node)));
		if (depthLimit < 1) depthLimit = 1;

		AlexHaoAlphaBetaSearcher searcher 
		= new AlexHaoAlphaBetaSearcher(depthLimit);

		// Create a new copy of the input node in my own node
		// type (with my own evaluation function)
		AlexHao1MancalaNode searchNode 
		= new AlexHao1MancalaNode(node);

		searcher.eval(searchNode);
		return searcher.getBestMove();
	}

	// Param are tuned by graphing calculator
	private double gaussian (int nMove) {
		double a = 1.8;
		double b = 12.1;
		double c = 9.2;
		return a * Math.exp(- Math.pow(nMove - b, 2) / (2 * c * c));
	}
	

	/**
	 * Returns the number of pieces not yet captured.
	 * @return int - uncaptured pieces
	 * @param node MancalaNode - node to check
	 */
	public int piecesRemaining(MancalaNode node) {
		int pieces = 0;
		for (int i = 0; i < 6; i++) pieces += node.state[i];
		for (int i = 7; i < 13; i++) pieces += node.state[i];
		return pieces;
	}

}
