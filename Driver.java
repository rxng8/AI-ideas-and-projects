
public class Driver {

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
