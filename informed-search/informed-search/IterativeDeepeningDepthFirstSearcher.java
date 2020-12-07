
public class IterativeDeepeningDepthFirstSearcher extends Searcher {

	private boolean checkAncestor = false;
	
	public IterativeDeepeningDepthFirstSearcher() {
		
	}
	
	public IterativeDeepeningDepthFirstSearcher(boolean checkAncestorMode) {
		this.checkAncestor = checkAncestorMode;
	}
	
	@Override
	public boolean search(SearchNode rootNode) {
		int depth = 1;
		nodeCount = 0;
		while (depth <= Integer.MAX_VALUE) {
			Searcher dls;
			if (this.checkAncestor) {
				dls = new DepthLimitedSearcher(depth, 12);
			} else {
				dls = new DepthLimitedSearcher(depth);
			}
			
			boolean found = dls.search(rootNode);
			nodeCount += dls.getNodeCount();
			
			if (found) {
				goalNode = dls.getGoalNode();
				return true;	
			}

			depth++;
		}

		
		return false;
	}

}
