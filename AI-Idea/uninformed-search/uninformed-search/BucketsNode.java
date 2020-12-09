import java.util.ArrayList;

/**
 * BucketsNode.java - Search node for buckets puzzle.  Problem: Given
 * a 5 unit and a 3 unit bucket, how can one measure precisely 4
 * units?  Possible operations: fill/empty 1st/2nd bucket or pour
 * contents of one to the other until source empty or recipient is
 * full.
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

public class BucketsNode extends SearchNode 
{

	/**
	 * variable <code>bucket1</code> - amount in first bucket */
	public int bucket1 = 0;


	/**
	 * variable <code>bucket2</code> - amount in second bucket */
	public int bucket2 = 0;


	/**
	 * variable <code>MAX_AMOUNT1</code> - max amount in first bucket */
	public static final int MAX_AMOUNT1 = 5;


	/**
	 * variable <code>MAX_AMOUNT2</code> - max amount in second bucket */
	public static final int MAX_AMOUNT2 = 3;


	/**
	 * Creates a <code>BucketsNode</code> instance and sets it
	 * to an initial search state. */
	public BucketsNode() {}


	/**
	 * <code>isGoal</code> - test whether or not the current node is a
	 * goal node.
	 *
	 * @return a <code>boolean</code> value */
	public boolean isGoal() 
	{
		return (bucket1 + bucket2) == 4;
	}


	/**
	 * <code>expand</code> - return an ArrayList<SearchNode> of this node's children.
	 *
	 * @return an <code>ArrayList<SearchNode></code> of SearchNodes */
	public ArrayList<SearchNode> expand() 
	{
		// For each operation, check if it is valid and will change
		// the state.  If so, generate a child and add it to the
		// children ArrayList.
		ArrayList<SearchNode> children = new ArrayList<SearchNode>();
		// Empty bucket1
		if (bucket1 > 0) {
			BucketsNode child = (BucketsNode) this.childClone();
			child.bucket1 = 0;
			children.add(child);
		}

		// Empty bucket2
		if (bucket2 > 0) {
			BucketsNode child = (BucketsNode) this.childClone();
			child.bucket2 = 0;
			children.add(child);
		}

		// Fill bucket1
		if (bucket1 != MAX_AMOUNT1) {
			BucketsNode child = (BucketsNode) this.childClone();
			child.bucket1 = MAX_AMOUNT1;
			children.add(child);
		}

		// Fill bucket2
		if (bucket2 != MAX_AMOUNT2) {
			BucketsNode child = (BucketsNode) this.childClone();
			child.bucket2 = MAX_AMOUNT2;
			children.add(child);
		}   

		// Pour bucket1 into bucket2
		if (bucket2 < MAX_AMOUNT2 && bucket1 > 0) {
			BucketsNode child = (BucketsNode) this.childClone();
			int pourAmount = Math.min(bucket1, MAX_AMOUNT2 - bucket2);
			child.bucket1 -= pourAmount;
			child.bucket2 += pourAmount;
			children.add(child);
		}

		// Pour bucket2 into bucket1
		if (bucket1 < MAX_AMOUNT1 && bucket2 > 0) {
			BucketsNode child = (BucketsNode) this.childClone();
			int pourAmount = Math.min(bucket2, MAX_AMOUNT1 - bucket1);
			child.bucket2 -= pourAmount;
			child.bucket1 += pourAmount;
			children.add(child);
		}

		return children;
	}


	// No deep cloning (copying) of instance variables is necessary
	// for this class.  The inherited shallow clone suffices.  (Can
	// you understand why?)


	/**
	 * <code>equals</code> - whether or not two BucketsNode objects
	 * have the same state.
	 *
	 * @param o an <code>Object</code> value - object to test for
	 * equality with this
	 * @return a <code>boolean</code> value - whether or not two
	 * objects are equal */
	public boolean equals(Object o) 
	{
		return ((o instanceof BucketsNode)
				&& ((BucketsNode)o).bucket1 == bucket1 
				&& ((BucketsNode)o).bucket2 == bucket2);
	}


	/**
	 * <code>toString</code> - string representation of state
	 * 
	 * @return a <code>String</code> value */
	public String toString() 
	{
		return "Bucket 1: " + bucket1 + ", Bucket 2: " + bucket2;
	}


}// BucketsNode

