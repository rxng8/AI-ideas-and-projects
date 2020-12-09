
public class AugmentedMancalaNode extends MancalaNode {
	/**
	 * TnellerMancalaNode constructor.
	 */
	public AugmentedMancalaNode() {
		super();
	}

	public AugmentedMancalaNode(MancalaNode node) {
		super(node);
	}

	public AugmentedMancalaNode(int stateIndex) {
		super(stateIndex);
	}
	
	@Override
	public double utility() {
		double util = 0;
		if (state[MAX_SCORE_PIT] > NUM_PIECES / 2) {
			util = state[MAX_SCORE_PIT];
		} else if (state[MIN_SCORE_PIT] > NUM_PIECES / 2) {
			util = - state[MIN_SCORE_PIT];
		} else {
			util = state[MAX_SCORE_PIT] - state[MIN_SCORE_PIT];
		}
		return util;
	}

}
