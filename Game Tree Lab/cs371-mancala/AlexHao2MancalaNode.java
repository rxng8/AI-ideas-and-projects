import java.util.ArrayList;

public class AlexHao2MancalaNode extends MancalaNode {


	/**
	 * AH1MancalaNode constructor.
	 */
	public AlexHao2MancalaNode() {
		super();
	}

	public AlexHao2MancalaNode(MancalaNode node) {
		super(node);
	}

	public AlexHao2MancalaNode(int stateIndex) {
		super(stateIndex);
	}

	/**
	 * A player need to have 25 pieces in the scoring pit to win
	 * Mobility: number of movable pits and the number of controlling pieces
	 * Score: The closeness of scoring the majority pieces before the other player
	 */
	public double utility() {

		int WIN_PIECES = NUM_PIECES/2 + 1;
		double mobility = 0;
		int pieces = 0; 
		int pits = 0;

		//Current state scores 
		double p1Score = state[MAX_SCORE_PIT]-WIN_PIECES;
		if(p1Score == WIN_PIECES) {
			return Double.MAX_VALUE;
		}
		double p2Score = state[MIN_SCORE_PIT]-WIN_PIECES;
		
		//Get the number of movable pits and number of piece s
		for(int i = 0; i <MAX_SCORE_PIT; i++){
			if(state[i] != 0) {
				pits++;
			}
			pieces =+ state[i];
		}

		//preventing starvation 
		mobility =+ pits > 2 ?  pits + 0.1 * pieces: 0;

		//Avoid being capture
		double avoid= 0;		
		for(int check = MAX_SCORE_PIT+1; check< MIN_SCORE_PIT; check++){
			if(state[check]==0) {
				for(int oppoent = 7; oppoent<MIN_SCORE_PIT; oppoent++) {
					if(oppoent + state[oppoent] == check && state[12-check] !=0) {
						avoid = state[12-check];
					}
				}
			}
				
		}

		return p1Score - p2Score + mobility - avoid;
	}
}