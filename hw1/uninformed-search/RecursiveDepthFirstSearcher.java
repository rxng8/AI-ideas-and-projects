import java.util.ArrayList;

public class RecursiveDepthFirstSearcher extends Searcher {

	@Override
	public boolean search(SearchNode rootNode) {
		return dfs(rootNode);
	}
	
	public boolean dfs(SearchNode node) {
		
		nodeCount++;
		
		if (node.isGoal()) {
			goalNode = node;
			return true;
		}

		for (SearchNode child : node.expand()) {
			if(dfs(child)) {
				return true;
			}
		}
		
		return false;
	}

}
