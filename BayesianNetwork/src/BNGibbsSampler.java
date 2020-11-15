import java.util.ArrayList;
import java.util.Random;

/**
 * An implementation of the Gibbs Sampling Stochastic Simulation method for estimating Bayesian Network probabilities with/without evidence.
 * <b>You should only modify the simulate method.</b>
 * Algorithm from Section 4.4.3 of Pearl, Judea. "Probabilistic Reasoning in Intelligent Systems"
 */

public class BNGibbsSampler {
	/** iteration frequency of progress reports */
	public static int reportFrequency = 200000;
	/** total iterations; each non-evidence variable is updated in each iteration */
	public static int iterations = 1000000;

	/**
	 * Initialize parameters, parse input, display BN information, and perform Gibbs sampling. <b>You should not modify this method</b>
	 * @param args an array of command-line arguments
	 * @throws ParseException standard input does not match grammar for Bayesian network specification. (See assignment documentation for BNF grammar.)
	 */
	public static void main(java.lang.String[] args) throws ParseException {
		// Initialize iterations and update frequency
		if (args.length > 0) {
			iterations = Integer.parseInt(args[0]);
			reportFrequency = (args.length > 1) ? Integer.parseInt(args[1]) : iterations;
		}

		// Read in belief net specification from System.in
		new BNParse(System.in).parseInput();
		BNNode.printBN();

		// Do stochastic simulation.
		simulate();
	}

	/**
	 * Perform Stochastic Simulation as described in Section 4.4.3 of Pearl, Judea. "Probabilistic Reasoning in Intelligent Systems".
	 * The enclosed file pearl.out shows the output format given the input:
	 *   java BNGibbsSampler 1000000 200000 &lt; sample.in &gt; sample.out
	 * <b>This is the only method you should modify.</b>
	 */
	public static void simulate() {
		
		// Build a list of non evidence node!
		ArrayList<BNNode> nonEvidenceNodes = new ArrayList<>();
		for (BNNode node : BNNode.nodes) {
			if (!node.isEvidence) {
				nonEvidenceNodes.add(node);
			}
		}
		
		// Randomly assign them. Initialization does not really matter!
		Random random = new Random();
		for (BNNode node : nonEvidenceNodes) {
			node.value = random.nextBoolean();
		}
		
		// Initialize the statistic variables
		int totalNENs = nonEvidenceNodes.size();
		int[] trueCounts = new int[totalNENs];
		double[] totalCPs = new double[totalNENs];
		
		// For each iterations
		for (int i= 0; i < iterations; i++) {
			
			// Loop through non evidence node and update!
			// For each non evidence node index ..
			for (int j = 0; j < totalNENs; j++) {
				BNNode node = nonEvidenceNodes.get(j);
				// Set the node to true
				node.value = true;
				// Compute CPT entry (without normalizaing constant). (CPT Return the probs that the node is true)
				double trueCP = node.cptLookup();
				
				// For each child
				for (int k = 0; k < node.children.length; k++) {
					// Multiply this child CPT entry (or 1 minus that if the child is false)
					if (node.children[k].value) { 
						trueCP *= node.children[k].cptLookup();
					} else {
						trueCP *= (1 - node.children[k].cptLookup());
					}
				}
				
				// Set the node to false
				node.value = false;
				// Compute CPT entry (without normalizaing constant). (CPT Return the probs that the node is true)
				double falseCP = 1 - node.cptLookup();
				
				// For each child
				for (int k = 0; k < node.children.length; k++) {
					// Multiply this child CPT entry (or 1 minus that if the child is false)
					if (node.children[k].value) { 
						falseCP *= node.children[k].cptLookup();
					} else {
						falseCP *= (1 - node.children[k].cptLookup());
					}
				}
				
				// Computing normalization to get the probability of the node being true ...
				double normalizationSum = trueCP + falseCP;
				trueCP /= normalizationSum;
				falseCP /= normalizationSum;
				// .. add this to the conditional probs total for that node ...
				totalCPs[j] += trueCP;
				// ... pick a new random variable value for the node according to that probs.
				node.value = random.nextDouble() < trueCP;
				// ... and tally the assignment if it's true!
				if (node.value) {
					trueCounts[j] ++;
				}
			}
			
			// Report estimates with given frequency!
			if ((i + 1) % reportFrequency == 0 || i == iterations - 1) {
				System.out.println("__________________________________");
				System.out.println("After iteration " + (i + 1) + ":");
				System.out.println("Variable, Average Conditional Probability, Fraction True");
				for (int j = 0; j < totalNENs; j++) {
					BNNode node = nonEvidenceNodes.get(j);
					System.out.println(node.name + ", " + totalCPs[j] / (i + i) + ", "
							+ trueCounts[j] / ((double) i + 1));
				}
			}
			
		}
		
		
	}
}


