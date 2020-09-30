import java.util.Random;

/**
 * NArmedBandit - simple NArmedBandit test problem
 * @author Todd W. Neller
 *
 */
public class NArmedBandit {
	
	private static final Random random = new Random(); // random number generator
	private double cumulativeRegret = 0; // the cumulative expected regret of decisions
	private double cumulativeReward = 0; // the observed cumulative reward of decisions
	private double maxPayoutMean; // the maximum payout mean
	private final int numArms; // the number of bandit arms
	private int numPulls = 0; // the number of arm pulls taken
	private double[] payoutMean; // the arm payout means
	public static final double PAYOUT_STD_DEV = 1.0; // the payout standard deviation
	
	/**
	 * @param payoutMean - the payout means of the bandit arms.   All payout standard deviations will be 1.0.
	 */
	public NArmedBandit(double[] payoutMean) {
		this.payoutMean = payoutMean.clone();
		numArms = payoutMean.length;
		computeMaxMean();
	}
	
	/**
	 * @param numArms the number of bandit arms.  All payout means will be drawn randomly from a normal distribution of mean 0 and standard deviation 1.  All payout standard deviations will be 1.0.
	 */
	public NArmedBandit(int numArms) {
		this.numArms = numArms;
		payoutMean = new double[numArms];
		for (int i = 0; i < numArms; i++)
			payoutMean[i] = random.nextGaussian();
		computeMaxMean();
	}

	/**
	 * @param numArms - the number of bandit arms.  
	 * @param decay - the decay in the mean payout increase from arm to arm.  All payouts will have the same standard deviation of 1.0 and will differ only in the mean.  The first arm will have payout mean 0, the second arm will have mean 1.0, the third will have 1 + decay, the fourth will have 1 + decay + decay^2, etc.  Thus, the payout for an arm n will be the summation of i from 0 to (n - 1) of (decay^i).
	 */
	public NArmedBandit(int numArms, double decay) {
		this.numArms = numArms;
		payoutMean = new double[numArms];
		double increment = 1.0;
		double payout = 0.0;
		for (int i = 0; i < numArms; i++) {
			payoutMean[i] = payout;
			payout += increment;
			increment *= decay;
		}
		computeMaxMean();
	}
	
	/**
	 * Compute the maximum payout mean from the current distribution.
	 */
	private void computeMaxMean() {
		maxPayoutMean = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < numArms; i++) 
			if (payoutMean[i] > maxPayoutMean)
				maxPayoutMean = payoutMean[i];
	}
	
	/**
	 * @return the cumulative expected regret of decisions
	 */
	public double getCumulativeRegret() {
		return cumulativeRegret;
	}
	
	/**
	 * @return the cumulative observed reward of decisions
	 */
	public double getCumulativeReward() {
		return cumulativeReward;
	}
	
	/**
	 * @return the maximum payout mean
	 */
	public double getMaxPayoutMean() {
		return maxPayoutMean;
	}


	/**
	 * @return the number of bandit arms
	 */
	public int getNumArms() {
		return numArms;
	}


	/**
	 * @return the number of arm pulls taken
	 */
	public int getNumPulls() {
		return numPulls;
	}


	/**
	 * @return the current payout means of the bandit arms
	 */
	public double[] getPayoutMeans() {
		return payoutMean.clone();
	}
	
	/**
	 * @param arm - zero-based arm number
	 * @return payout
	 */
	public double pull(int arm) {
		numPulls++;
		double payout = random.nextGaussian() * PAYOUT_STD_DEV + payoutMean[arm];
		cumulativeReward += payout;
		cumulativeRegret += (maxPayoutMean - payoutMean[arm]); // cumulative expected regret 
		return payout;
	}
	
	/**
	 * Reset all play statistics.
	 */
	public void resetStatistics() {
		numPulls = 0;
		cumulativeReward = 0;
		cumulativeRegret = 0;
	}
	
	/**
	 * @param seed - new random number seed
	 */
	static public void setSeed(long seed) {
		random.setSeed(seed);
	}
	
	/**
	 * Shuffle the payout means of the arms. 
	 */
	public void shuffleArms() {
		for (int i = numArms - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			double tmp = payoutMean[i];
			payoutMean[i] = payoutMean[j];
			payoutMean[j] = tmp;
		}
	}
	
}
