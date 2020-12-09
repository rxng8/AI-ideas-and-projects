import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.ArrayList;

/**
 * BestFirstSearcher.java - a simple implementation of best-first
 * search.
 *
 * @author Todd Neller
 * @version 1.1
 *

Copyright (C) 2006 Todd Neller

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

Information about the GNU General Public License is available online at:
  http://www.gnu.org/licenses/
To receive a copy of the GNU General Public License, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
02111-1307, USA.

 */

public class BestFirstSearcher extends HSearcher {

	/**
	 * <code>comparator</code> - the comparator that orders nodes in
	 * the search priority queue, determining what is "best" in
	 * best-first search.
	 */
	private Comparator<HSearchNode> comparator;

	/**
	 * Creates a new <code>BestFirstSearcher</code> instance with the
	 * given comparator to order HSearchNodes in the Priority Queue.
	 *
	 * @param comparator a <code>Comparator</code> value - determiner
	 * of what is "best" in the best-first search.
	 */
	public BestFirstSearcher(Comparator<HSearchNode> comparator) 
	{
		this.comparator = comparator;
	}

	/**
	 * <code>search</code> - given an initial node, perform best-first
	 * search.
	 *
	 * @param rootNode a <code>SearchNode</code> value - the initial node
	 * @return a <code>boolean</code> value - whether or not goal node
	 * was found */
	public boolean search(SearchNode rootNode) {
		HSearchNode hRootNode = (HSearchNode) rootNode;

		// IMPLEMENT:

		// Initialize search variables.
		PriorityQueue<HSearchNode> queue = new PriorityQueue<>(comparator);
		queue.add(hRootNode);
		nodeCount = 0;
		
		// Main search loop:
		while (true) {

			// If the search queue is empty, return with failure
			// (false).
			if (queue.isEmpty()) {
				return false;
			}
			
			// Otherwise get the next search node from the front of
			// the queue and increment the nodeCount.
			HSearchNode node = queue.poll();
			nodeCount ++;
			
			// If the search node is a goal node, store it and return
			// with success (true).
			if (node.isGoal()) {
				goalNode = node;
				return true;
			}
			// Otherwise, expand the node and insert each of its
			// children into the queue.
			else {
				ArrayList<SearchNode> list = node.expand();
				for (SearchNode child : list) {
					queue.offer((HSearchNode) child);
				}
			}
		}

	}    

}// BestFirstSearcher
