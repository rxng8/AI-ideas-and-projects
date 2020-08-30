import java.util.*;

/**
 * RomaniaNode - an implementation of Russell & Norvig's Romania
 * travel planning problem to reach Bucharest from Arad (Russell &
 * Norvig, AI: a modern approach, 2nd ed.; pp. 62-64, 95).  Cities
 * names are abbreviated as the characters A...Z.
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

public class RomaniaNode extends HSearchNode {

	/**
	 * variable <code>sld</code> - maps city (character) to its
	 * straight-line distance to Bucharest */
	static HashMap<Character, Integer> sld = new HashMap<Character, Integer>();

	/**
	 * variable <code>map</code> - maps city (character) to a map of
	 * its adjacent cities to their distances from this city */
	static HashMap<Character, HashMap<Character,Integer>> map 
	= new HashMap<Character, HashMap<Character,Integer>>();

	/* The static initialization block runs when the class is loaded. */
	static 
	{	
		// Build a HashMap of straight-line distances from each city
		// to Bucharest
		char[] cities = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'L', 'M', 'N', 'O', 'P', 'R', 'S', 'T', 'U', 'V', 'Z'};	
		int[] straightLineDistances = {366, 0, 160, 242, 161, 176, 77, 151, 226, 244, 241, 234, 380, 100, 193, 253, 329, 80, 199, 374};
		for (int i = 0; i < cities.length; i++) {
			sld.put(cities[i], straightLineDistances[i]);
			map.put(cities[i], new HashMap<Character,Integer>());
		}

		// Build roadmap
		addRoad('A', 'S', 140);
		addRoad('A', 'T', 118);
		addRoad('A', 'Z', 75);
		addRoad('B', 'F', 211);
		addRoad('B', 'G', 90);
		addRoad('B', 'P', 101);
		addRoad('B', 'U', 85);
		addRoad('C', 'D', 120);
		addRoad('C', 'P', 138);
		addRoad('C', 'R', 146);
		addRoad('D', 'M', 75);
		addRoad('E', 'H', 86); 
		addRoad('F', 'S', 99);
		addRoad('H', 'U', 98);
		addRoad('I', 'N', 87);
		addRoad('I', 'V', 92);
		addRoad('L', 'M', 70);
		addRoad('L', 'T', 111);
		addRoad('O', 'S', 151);
		addRoad('O', 'Z', 71);
		addRoad('P', 'R', 97);
		addRoad('R', 'S', 80);
		addRoad('U', 'V', 142);
	}

	/**
	 * <code>addRoad</code> - add a road to the map.  The HashMap has
	 * two layers.  The first layer maps a city to another HashMap
	 * which contains information about adjacent cities.  This other
	 * HashMap maps adjacent cities to the distance from the given
	 * city.  Since each road is two-way, both directions are handled.
	 *
	 * @param city1 a <code>char</code> value - the city at one end of the road
	 * @param city2 a <code>char</code> value - the cite at the other
	 * end of the road
	 * @param distance an <code>int</code> value - the length of the road
	 */
	static void addRoad(char city1, char city2, int distance) 
	{
		map.get(city1).put(city2, distance);
		map.get(city2).put(city1, distance);
	}

	/**
	 * <code>city</code> - the current city, initially "Arad" by default
	 */
	private char city = 'A';

	/**
	 * <code>distanceTraveled</code> - the distance traveled to reach
	 * this node.
	 */
	private int distanceTraveled = 0;


	/**
	 * <code>isGoal</code> - test whether or not the current node is a
	 * goal node.
	 *
	 * @return a <code>boolean</code> value - whether or not the
	 * current node is a goal node */
	public boolean isGoal() 
	{
		return city == 'B';
	}


	/**
	 * <code>getG</code> - returns the node's g-value, that is, the
	 * cost to reach the node from the initial node, i.e. the distance
	 * traveled so far.
	 *
	 * @return a <code>double</code> value - the node's g-value
	 */
	public double getG() 
	{
		return distanceTraveled;
	}


	/**
	 * <code>getH</code> - returns the node's h-value, that is, the
	 * estimated cost to reach a goal node from the node, i.e. the
	 * straight-line distance to Bucharest.
	 *
	 * @return a <code>double</code> value - the node's h-value
	 */
	public double getH() 
	{
		return sld.get(city);
	}


	/**
	 * <code>expand</code> - return a (possibly empty) ArrayList of this
	 * node's children.  A new child is created by calling
	 * <code>childClone</code> and appropriately modifying the state
	 * of the returned node.
	 *
	 * @return a <code>ArrayList</code> of SearchNodes that are children
	 * of this node */
	public ArrayList<SearchNode> expand() 
	{
		ArrayList<SearchNode> children = new ArrayList<SearchNode>();
		HashMap<Character,Integer> roads = map.get(city);
		for (char adjCity : roads.keySet()) {
			int dist = roads.get(adjCity);
			RomaniaNode child = (RomaniaNode) childClone();
			child.city = adjCity;
			child.distanceTraveled += dist;
			children.add(child);
		}
		return children;	
	}

	public String toString() 
	{
		return city + "(f:" + getF() + ",g:" + getG() + ",h:" + getH() + ")";
	}

}
