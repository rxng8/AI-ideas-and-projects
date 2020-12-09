/**
 * HSearcher.java - a superclass for informed AI searcher classes.
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

public abstract class HSearcher extends Searcher {

	/**
	 * <code>getGoalNode</code> - Returns a goal node if the previous
	 * search was successful, and null otherwise.
	 *
	 * @return a <code>HSearchNode</code> value - the goal node from
	 * previous search or null if no goal node was found */
	public HSearchNode getGoalNode() 
	{
		return (HSearchNode) goalNode;
	}

}// HSearcher
