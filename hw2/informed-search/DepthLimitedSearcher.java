import java.util.Stack;

public class DepthLimitedSearcher extends Searcher {

	private int maxValue;
	private int maxAncestor;
	
	public DepthLimitedSearcher() {
		
	}
	
	public DepthLimitedSearcher(int maxValue) {
		this.maxValue = maxValue;
	}
	
	public DepthLimitedSearcher(int maxValue, int maxAncestor) {
		this.maxValue = maxValue;
		this.maxAncestor = maxAncestor;
	}

	@Override
	public boolean search(SearchNode rootNode) {
		// IMPLEMENT:

		// Initialize search variables.
		Stack<SearchNode> stack = new Stack<>();
		stack.add(rootNode);
		nodeCount = 0;
		
		// Main search loop.
		while (true) {

			// If the search stack is empty, return with failure
			// (false).
			if (stack.isEmpty()) {
				return false;
			}
			
			// Otherwise pop the next search node from the top of
			// the stack.
			SearchNode node = stack.pop();
			nodeCount ++;
			
//					System.out.println(nodeCount);
			// If the search node is a goal node, store it and return
			// with success (true).
			if (node.isGoal()) {
				goalNode = node;
				return true;
			}
			
			// Otherwise, expand the node and push each of its
			// children into the stack.
//			if (node.depth <= this.maxValue) {
//				for (SearchNode child : node.expand()) {
//					// Mode check if the same as ancestor.
//					if (this.maxAncestor != 0) {
//						SearchNode ancestor = node.parent;
//						int i;
//						for (i = 0; i < this.maxAncestor; i++) {
//							if (!child.equals(ancestor)) {
//								if (ancestor != null) {
//									ancestor = ancestor.parent;
//								} else {
//									break;
//								}
//							}
//							break;
//						}
//						if (i >= 12) {
//							stack.push(child);
//						}
//					} 
//					// Normal mode
//					else {
//						if (!child.equals(node.parent)) {
//							stack.push(child);
//						}						
//					}
//				}
//			}
			for (SearchNode child : node.expand()) {
				if (child.depth <= this.maxValue) {
					stack.push(child);
				}
			}
		}
	}

}
