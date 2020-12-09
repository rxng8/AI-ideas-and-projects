import java.util.Arrays;
import java.util.Scanner;

/**
 * NArmedBanditGame - A text-based user interface to NArmedBandit, an n-armed bandit problem model allowing normal, logorithmic, and specified payout mean distributions. 
 * @author Todd W. Neller
 */
public class NArmedBanditGame {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NArmedBandit nab = null;
		Scanner in = new Scanner(System.in);
		System.out.print("Set random seed (y/n)? ");
		if (in.next().toLowerCase().charAt(0) == 'y') {
			System.out.print("Seed? ");
			NArmedBandit.setSeed(in.nextLong());
		}
		System.out.print("(1) Normal mean distribution, (2) Logorithmic mean distribution, or (3) Given mean distribution? ");
		int selection = in.nextInt();
		System.out.print("Number of arms? ");
		int numArms = in.nextInt();
		if (selection == 1)
			nab = new NArmedBandit(numArms);
		else if (selection == 2) {
			System.out.print("Decay (e.g. 0.5)? ");
			double decay = in.nextDouble();
			nab = new NArmedBandit(numArms, decay);
		}
		else if (selection == 3) {
			double[] payoutMeans = new double[numArms];
			System.out.printf("Enter %d payout means: ", numArms);
			for (int i = 0; i < numArms; i++)
				payoutMeans[i] = in.nextDouble();
			nab = new NArmedBandit(payoutMeans);
		}
		else {
			System.out.println("Invalid selection.");
			System.exit(1);
		}
		System.out.print("Total number of pulls? ");
		int totalPulls = in.nextInt();
		System.out.printf("With each successive pull, select an arm 0 - %d.\n", numArms - 1);
		nab.shuffleArms();
		for (int i = 0; i < totalPulls; i++) {
			boolean validEntry = false;
			while (!validEntry) {
				try {
					System.out.printf("Pull %d? ", i + 1);
					int arm = in.nextInt();
					if (arm >= 0 && arm < numArms) {
						double payout = nab.pull(arm);
						System.out.println("Payout: " + payout);
					}
					else {
						System.out.println("Invalid arm.");
					}
					validEntry = true;
				}
				catch (Exception e) {
					System.out.println("Invalid entry.");
				}
			}
		}
		System.out.println("Arm Payout Means: " + Arrays.toString(nab.getPayoutMeans()));
		System.out.println("Cumulative Reward: " + nab.getCumulativeReward());
		System.out.println("Cumulative Regret: " + nab.getCumulativeRegret());
		in.close();
	}

}
