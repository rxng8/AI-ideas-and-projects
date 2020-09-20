// Written by Todd W. Neller.  Copyright (c) 2000 Gettysburg College.
// You may distribute this source code for non-commercial purposes
// only.  You may study, modify, and use this source code for any
// purpose, as long as this notice is retained.  Note that this
// example is provided "as is", WITHOUT WARRANTY of any kind either
// expressed or implied.


/**
 * My simple extension of MancalaNode with a simple utility evaluation.
 * @author: Todd Neller
 */
public class TnellerMancalaNode extends MancalaNode {


	/**
	 * TnellerMancalaNode constructor.
	 */
	public TnellerMancalaNode() {
		super();
	}

	public TnellerMancalaNode(MancalaNode node) {
		super(node);
	}

	public TnellerMancalaNode(int stateIndex) {
		super(stateIndex);
	}
	/**
	 * My simple utility method returns different in MAX/MIN score.
	 */
	public double utility() {
		return state[MAX_SCORE_PIT]-state[MIN_SCORE_PIT];
	}

}
