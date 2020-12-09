import java.util.Random;

public class HillDescender {
	State state;
	double energy;
	State minState;
	double minEnergy;
	double acceptRate = 0;
	
	public HillDescender(State initState) {
		state = initState;
		energy = initState.energy();
		minState = (State) state.clone();
		minEnergy = energy;
	}
	
	public HillDescender(State initState, double acceptRate) {
		this(initState);
		this.acceptRate = acceptRate;
	}
	
	public State search(int iterations) {
		Random random = new Random();
		for (int i = 0; i < iterations; i++) {
			if (i % 100000 == 0) {
				System.out.println(minEnergy + "\t" + energy);
			}
			
			state.step();
			double nextEnergy = state.energy();
			if (nextEnergy <= energy || random.nextDouble() < acceptRate) {
				energy = nextEnergy;
				if (nextEnergy < minEnergy) {
					minState = (State) state.clone();
					minEnergy = nextEnergy;
				}
			} else {
				state.undo();
			}
		}
		return minState;
	}
	
}
