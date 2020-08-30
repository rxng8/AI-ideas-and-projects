import java.util.ArrayList;
import java.util.Random;

public class HPancakeSortNode {
	// The cumulative cost of reaching this node given the sequence of previous flips, i.e.
	int cost;

	// Last number of flipped pancakes (2 through length), or 0 for the root search node.
	int lastFlip;
	
	// Current pancake sequence consisting of a permutation of integers from 0 through length - 1.
	int[] pancake;
	
	/**
	 * Create an HPancakeSortNode that is constructed with the given pancake permutation of pancake 
	 * sizes numbered {0-(length-1)}, setting the initial cost to 0.
	 * @param pancake
	 */
	public HPancakeSortNode(int[] pancake) {
		this.pancake = pancake;
	}
	
	/*
	 * Create an HPancakeSortNode that is constructed by creating a sorted sequence of pancake sizes 
	 * numbered 0 through (length-1) and then performing random reversals of pancakes ranging from 2 
	 * to length and initializing this generated root by setting lastFlipped and cost to 0.
	 */
	public HPancakeSortNode(int size, int numShuffleFlips) {
		
		// Initialize pancake!
		for (int i = 0; i < size; i++) {
			this.pancake[i] = i;
		}
		
		// Perform random reversal!
		for (int i = 2; i <= size; i++) {
			Random r = new Random();
			flip(r.nextInt());
		}
		
		// Reset root node
		lastFlip = 0;
		cost = 0;
	}
	
	/**
	 * Return the result of a deep clone of HPancakeSortNode that has a unique pancake array 
	 * copy for each cloned node.
	 */
	public HPancakeSortNode clone() {
		HPancakeSortNode copy = (HPancakeSortNode) this.clone();
		for (int i = 0; i < this.pancake.length; i++) {
			copy.pancake[i] = this.pancake[i];
		}
		copy.lastFlip = this.lastFlip;
		copy.cost = this.cost;
		return copy;
	}
	
	/**
	 * Return a list of HPancakeSortNode children that result from a flip of 2 through (length) flips.
	 * @return
	 */
	public ArrayList<SearchNode> expand() {
		ArrayList<SearchNode> children = new ArrayList<>();
		
		for (int i = 2; i <= this.pancake.length; i++) {
			HPancakeSortNode child = clone();
			child.flip(i);
		}
		
		return children;
	}
	
	/**
	 * Flip (that is, reverse the order of) the first n pancakes, increase the node cost by n, and set lastFlip to n.
	 * @param n
	 */
	public void flip(int n) {
		
		// Set the cost and lastFlipped
		cost += n;
		lastFlip = n;
		
		// Reverse order of the first n pancake!
		for (int i = 0; i < n / 2; i++) {
			int tmp = pancake[i];
			pancake[i] = pancake[n - i];
			pancake[n - i] = tmp;
		}
	}
	
	/**
	 * Return the total number of pancakes flipped to reach this node from the root node.
	 * @return
	 */
	public double getG() {
		return cost;
	}
	
	/**
	 * Given a pancake index permutation of values from 0 through the array length - 1, return
	 *  an optimal goal node resulting from A* search with an admissible heuristic that is not h(n) = 0.
	 * @param pancake
	 * @return
	 */
	public static HPancakeSortNode getGoalNode(int[] pancake) {
		return null;
	}
	
	/**
	 * Return an admissible heuristic estimate of the cost to a goal node that does not
	 * overestimate, nor is the trivial h(n) = 0 heuristic.
	 * @return
	 */
	public double getH() {
		return 0;
	}
		
	/**
	 * Return the size of the most recently flipped stack of pancakes, or 0 in the case of the root node.
	 * @return
	 */
	public int getLastFlip() {
		return 0;
	}
	
	/**
	 * Return whether or not the pancake values are sorted in ascending order.
	 * @return
	 */
	public boolean isGoal() {
		return false;
	}
	
	/**
	 * Return a single line String representation of the current node including node cost (g(n)), 
	 * the last flip made, and the current pancake sequence.
	 */
	public String toString() {
		return null;
	}
	
	/**
	 * This method will be ignored in JUnit testing, but can serve as a place for your test code.
	 * @param args
	 */
	public static void main(java.lang.String[] args) {
		
	}
}
