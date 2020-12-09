
public class AlexHao1MancalaNode extends MancalaNode {
	/**
	 * TnellerMancalaNode constructor.
	 */
	public AlexHao1MancalaNode() {
		super();
	}

	public AlexHao1MancalaNode(MancalaNode node) {
		super(node);
	}

	public AlexHao1MancalaNode(int stateIndex) {
		super(stateIndex);
	}
	
	
	@Override
	public double utility() {

		double util = 0;
		double[] array = utility(state.clone());
		for (int i = 0; i < array.length; i++) {
			if (i == 1) {
				util += 1.75 * array[i];
			} else if (i == 3) {
				util += 1.9 * array[i];
			} else {
				util += array[i];
			}
		}
		
		// Print stats:
//		System.out.printf("Score difference: %.1f\nEstimated capture: %.1f\nOp cap: %.1f\nChaining: %.1f\n", array[0], array[1], array[2], array[3]);
		return util;
	}
	
	// https://fiasco.ittc.ku.edu/publications/documents/Gifford_ITTC-FY2009-TR-03050-03.pdf
	private double[] utility(int[] localState) {

		// difference between scores
		double h1 = state[MAX_SCORE_PIT] - state[MIN_SCORE_PIT];
		
		// Capturing opportunity
		double h2 = 0;
		
		// Opponent capturing opportunity
		double h3 = 0;
		
		// Chaining opportunity
		double h4 = 0;

		// simulated move
		for (int move : getLegalMoves()) {
			int position = move, scorePit, oppositePit;
			
			// Take the pieces from the indicated pit.
			int pieces = localState[position];
			localState[position] = 0;
	
			// Redistribute them around the pits, skipping the opponent's scoring pit.
			while (pieces > 0) {
				position = (position + 1) % TOTAL_PITS;
	
				// Skip over opponent's scoring pit
				if (position == ((player == MAX) ? MIN_SCORE_PIT : MAX_SCORE_PIT))
					continue;
	
				// Distribute piece
				localState[position] ++;
				pieces--;
			}
			
			// If it land on maximizing player pit, then it's a chain
			if (position == MAX_SCORE_PIT) {
				h4++;
				for (double d : utility(localState.clone())) {
					h2 += d;
				}
			}
	
			// If the last piece distributed landed in an empty pit on
			// one's side, capture both the last
			// piece and any pieces opposite.
			scorePit = (player == MAX) ? MAX_SCORE_PIT : MIN_SCORE_PIT;
			// if last piece distributed in empty pit on own side
			if (localState[position] == 1 && (scorePit - position) > 0 
					&& (scorePit - position <= PLAY_PITS)) { // last piece into empty play pit
				oppositePit = MIN_SCORE_PIT - position - 1;
				// capture own pit
				localState[scorePit] ++;
				localState[position] --;
				// capture opposite pit
				localState[scorePit] += localState[oppositePit];
				localState[oppositePit] = 0;
				
				h2 += localState[oppositePit] + 1;
				
			}
	
			// Check for starvation.  If next player has no moves (is
			// starved), their opponent scores all remaining pieces.
			boolean starved = true; // assume next player starved
			scorePit = (player == MAX) ? MAX_SCORE_PIT : MIN_SCORE_PIT; // set scoring pit of next player
			for (position = scorePit - PLAY_PITS; starved && position < scorePit; position++)
				if (localState[position] > 0)
					starved = false; // flag starved false if any next player pits contain anything
			if (starved) { // if next player starved, then opponent scores all remaining pieces.
				scorePit = (scorePit + PLAY_PITS + 1) % TOTAL_PITS;
				for (position = scorePit - PLAY_PITS; starved && position < scorePit; position++) {
					localState[scorePit] += localState[position];
					localState[position] = 0;
					
					h2 += localState[position];
					
				}
			}
		}
		
		return new double[]{h1, h2, h3, h4};
	}

}
