/**
 * HSearchNode.java - a simple node for informed AI search.
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

public abstract class HSearchNode extends SearchNode implements Cloneable {

	/**
	 * <code>getF</code> - returns the node's f-value, which is the
	 * sum of its g-value and h-value.  A node's g-value is the cost
	 * to reach the node from the initial node.  A node's h-value is
	 * the estimated cost to reach a goal node from the node.
	 *
	 * @return a <code>double</code> - the node's f-value
	 */
	public double getF() 
	{
		return getG() + getH();
	}

	/**
	 * <code>getG</code> - returns the node's g-value, that is, the cost
	 * to reach the node from the initial node.
	 *
	 * @return a <code>double</code> value - the node's g-value, that
	 * is, the cost to reach node n
	 */
	public abstract double getG();

	/**
	 * <code>getH</code> - returns the node's h-value, that is, the
	 * estimated cost to reach a goal node from the node.
	 *
	 * @return a <code>double</code> value - the node's h-value
	 */
	public abstract double getH();

}// HSearchNode

