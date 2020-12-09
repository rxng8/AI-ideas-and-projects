import java.util.ArrayList;
import java.util.Stack;

/*

Student implementation hints:
- It's not beneficial to copy or modify data structures if you don't have to.  
  The best representation of the state is an array of the indices of the current top cards.
  Instead of drawing cards, copy the indices and decrement accordingly.
  Keep variables that don't change from node to node as static (one copy per class) rather than
  non-static (one copy per object).
  
Which is the best search algorithm and why?
  
 */

public class SuitYourselfSoloNode extends SearchNode implements Cloneable {

	public static ArrayList<Stack<Integer>> columns;
	public static int numSuits, numColumns;
	
	private int[] topIndices;

	public SuitYourselfSoloNode() {
		this(4, 13, 5, 0L);
	}
	
	public SuitYourselfSoloNode(long seed) {
		this(4, 13, 5, seed);
	}
	
	public SuitYourselfSoloNode(int numSuits, int numRanks, int numColumns, long seed) {
		columns = SuitYourselfSoloGenerator.generate(numSuits, numRanks, numColumns, seed);
		SuitYourselfSoloNode.numSuits = numSuits;
		SuitYourselfSoloNode.numColumns = numColumns;
		topIndices = new int[numColumns];
		for (int i = 0; i < numColumns; i++)
			topIndices[i] = columns.get(i).size() - 1;
	}
	
	public SuitYourselfSoloNode(SuitYourselfSoloNode node) {
		this.topIndices = node.topIndices.clone();
	}
	
	@Override
	public boolean isGoal() {
		for (int i = 0; i < numColumns; i++)
			if (topIndices[i] >= 0)
				return false;
		return true;
	}

	@Override
	public ArrayList<SearchNode> expand() {
		ArrayList<SearchNode> children = new ArrayList<>();
		boolean[] chosen = new boolean[numSuits];
		for (int i = 0; i < numColumns; i++) {
			if (topIndices[i] < 0)
				continue;
			int suit = columns.get(i).get(topIndices[i]);
			if (chosen[suit])
				continue;
			chosen[suit] = true;
			SuitYourselfSoloNode child = (SuitYourselfSoloNode) childClone();
			for (int j = 0; j < numColumns; j++)
				while (child.topIndices[j] >= 0 && columns.get(j).get(child.topIndices[j]) == suit)
					child.topIndices[j]--;
			children.add(child);
		}
		return children;
	}
	
	public SearchNode clone() {
		return new SuitYourselfSoloNode(this);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numColumns; i++) {
			sb.append(i + ":");
			for (int j = 0; j <= topIndices[i]; j++)
				sb.append(" " + columns.get(i).get(j));
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		SuitYourselfSoloNode node = new SuitYourselfSoloNode(4, 13, 5, 0L);
		System.out.println(node);
		System.out.println("Children:\n");
		for (SearchNode child : node.expand())
			System.out.println(child);
		System.out.println("Solving...\n");
		Searcher searcher = new BreadthFirstSearcher(); //new IterativeDeepeningDepthFirstSearcher();
		searcher.search(node);
		searcher.printGoalPath();
		System.out.println("Depth " + searcher.getGoalNode().depth);
	}

}
