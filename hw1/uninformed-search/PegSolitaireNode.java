import java.util.ArrayList;

/**
 * PegSolitaireNode.java - Traditional 5-on-a-side Triangle Peg
 * Solitaire; Goal: to leave one peg after removal of all others via
 * linear jumps.
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

public class PegSolitaireNode extends SearchNode 
{
	/**
	 * variable <code>pegs</code> - state (peg 'O' or empty ' ') of
	 * each of the 15 triangle positions numbers from left to right,
	 * top to bottom.  */
	private char[] pegs = new char[15];

	/*
Peg position layout:
        0 
       / \
      1---2
     / \ / \ 
    3---4---5
   / \ / \ / \
  6---7---8---9 
 / \ / \ / \ / \
10--11--12--13--14
	 */

	/**
	 * constant <code>LINEAR_JUMP_POSITIONS</code> - lists of positions in a
	 * line along which jumps can occur */
	private static final int[][] LINEAR_JUMP_POSITIONS = {{0,1,3,6,10}, {2,4,7,11}, {5,8,12}, {10,11,12,13,14}, {6,7,8,9}, {3,4,5}, {14,9,5,2,0}, {13,8,4,1}, {12,7,3}};


	/**
	 * Creates a <code>PegSolitaireNode</code> instance and sets it
	 * to an initial search state. */
	public PegSolitaireNode() 
	{
		for (int i = 0; i < 15; i++)
			pegs[i] = 'O';
		pegs[4] = ' ';
	}


	/**
	 * <code>isGoal</code> - test whether or not the current node is a
	 * goal node.
	 *
	 * @return a <code>boolean</code> value */
	public boolean isGoal() 
	{
		// If depth is 13, we've jumped and removed 13 pegs.  Starting
		// with 14, that leaves 1 (our goal).
		return (depth == 13);
	}


	/**
	 * <code>expand</code> - return a (possibly empty) ArrayList of this
	 * node's children
	 *
	 * @return an <code>ArrayList<SearchNode></code> of SearchNodes */
	public ArrayList<SearchNode> expand() 
	{
		// For each operation, check if it is valid and will change
		// the state.  If so, generate a child and add it to the
		// children ArrayList.
		ArrayList<SearchNode> children = new ArrayList<SearchNode>();
		// For each line of positions, and ...
		for (int i = 0; i < LINEAR_JUMP_POSITIONS.length; i++) {
			int[] lineOfPos = LINEAR_JUMP_POSITIONS[i];
			// ... for each three positions along that line
			for (int j = 0; j < (lineOfPos.length - 2); j++)
				// ... check if a jump can occur.
				if (pegs[lineOfPos[j+1]] == 'O' && (pegs[lineOfPos[j]] != pegs[lineOfPos[j+2]])) {
					// If it can, create the child node and add it.
					PegSolitaireNode child = (PegSolitaireNode) this.childClone();
					for (int k = j; k < j + 3; k++)
						child.pegs[lineOfPos[k]] = 
						(child.pegs[lineOfPos[k]] == 'O') ? ' ' : 'O';
					children.add(child);
				}
		}
		return children;
	}


	/**
	 * <code>clone</code> - return a deep copy of this node.
	 *
	 * @return an <code>Object</code> value
	 */
	public Object clone() 
	{
		PegSolitaireNode newNode = (PegSolitaireNode) super.clone();
		newNode.pegs = (char[]) pegs.clone();
		return newNode;
	}    


	/**
	 * The <code>equals</code> method is especially useful for
	 * repeated state detection.
	 *
	 * @param o an <code>Object</code> value - object to test for
	 * equality with this
	 * @return a <code>boolean</code> value - whether or not two
	 * objects are equal */
	public boolean equals(Object o) 
	{
		if (o instanceof PegSolitaireNode) { 
			PegSolitaireNode n = (PegSolitaireNode) o;
			// Check that all peg positions are identical.
			for (int i = 0; i < pegs.length; i++)
				if (pegs[i] != n.pegs[i])
					return false;
			return true;
		}
		return false;
	}


	/**
	 * <code>toString</code> - especially useful for debugging,
	 * tracing search execution, reporting results, etc.
	 *
	 * @return a <code>String</code> value */
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("        ");
		sb.append(pegs[0]);
		sb.append("\n       / \\\n      ");
		sb.append(pegs[1]);
		sb.append("---");
		sb.append(pegs[2]);
		sb.append("\n     / \\ / \\ \n    ");
		sb.append(pegs[3]);
		sb.append("---");
		sb.append(pegs[4]);
		sb.append("---");
		sb.append(pegs[5]);
		sb.append("\n   / \\ / \\ / \\\n  ");
		sb.append(pegs[6]);
		sb.append("---");
		sb.append(pegs[7]);
		sb.append("---");
		sb.append(pegs[8]);
		sb.append("---");
		sb.append(pegs[9]);
		sb.append(" \n / \\ / \\ / \\ / \\\n");
		sb.append(pegs[10]);
		sb.append("---");
		sb.append(pegs[11]);
		sb.append("---");
		sb.append(pegs[12]);
		sb.append("---");
		sb.append(pegs[13]);
		sb.append("---");
		sb.append(pegs[14]);
		sb.append("\n");
		return sb.toString();
	}

}// PegSolitaireNode
