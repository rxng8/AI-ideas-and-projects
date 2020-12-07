import java.util.Comparator;

/**
 * GreedyComparator - A comparator for HSearchNodes which bases
 * comparison on h(n), the estimated cost to reach a goal node from
 * node n.
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

public class GreedyComparator implements Comparator<HSearchNode> {

	/**
	 * <code>compare</code> - compare two nodes, returning a value
	 * indicating their ordering according to h(n), the estimated cost
	 * to reach a goal node from node n.  If h(n1) < h(n2), -1 is
	 * returned.  If h(n1) = h(n2), 0 is returned.  If h(n1) > h(n2),
	 * 1 is returned.
	 *
	 * @param n1 a <code>HSearchNode</code> value - the first node of
	 * the comparison
	 * @param n2 a <code>HSearchNode</code> value - the second node of
	 * the comparison
	 * @return a <code>int</code> value - -1 if h(n1) < h(n2); 0 if
	 * h(n1) = h(n2); 1 if h(n1) > h(n2) */
	public int compare(HSearchNode n1, HSearchNode n2) 
	{
		double h1 = n1.getH();
		double h2 = n2.getH();
		if (h1 < h2)
			return -1;
		else if (h1 == h2)
			return 0;
		else
			return 1;
	}    

}
