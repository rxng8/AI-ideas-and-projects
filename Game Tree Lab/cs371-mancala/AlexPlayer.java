/**
 * TnellerMancalaPlayer - My simple implementation of a mancala
 * player with a simple, messy hack for distributing game-play.
 *
 * @author Todd Neller
 * @version 1.1 */

public class AlexPlayer implements MancalaPlayer {

	/**
	 * Choose a move for the given game situation given play time
	 * remaining.  */
	public int chooseMove(MancalaNode node, long timeRemaining) {
		// WARNING: This is a sloppy hack to try to distribute search
		// time over course of game.  Use at your own risk.
		final double DEPTH_FACTOR = 1.8;
		int depthLimit 
		= (int) (DEPTH_FACTOR 
				* Math.log((double) timeRemaining 
						/ piecesRemaining(node)));
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
