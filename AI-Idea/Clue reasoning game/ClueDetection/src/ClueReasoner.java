/**
 * ClueReasoner.java - project skeleton for a propositional reasoner
 * for the game of Clue.  Unimplemented portions have the comment "TO
 * BE IMPLEMENTED AS AN EXERCISE".  The reasoner does not include
 * knowledge of how many cards each player holds.  See
 * http://cs.gettysburg.edu/~tneller/nsf/clue/ for details.
 *
 * @author Todd Neller
 * @version 1.0
 *

Copyright (C) 2005 Todd Neller

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

import java.io.*;
import java.util.*;
import org.sat4j.*;

public class ClueReasoner 
{
	private int numPlayers;
	private int playerNum;
	private int numCards;
	private SATSolver solver;    
	private String caseFile = "cf";
	private String[] players = {"sc", "mu", "wh", "gr", "pe", "pl"};
	private String[] suspects = {"mu", "pl", "gr", "pe", "sc", "wh"};
	private String[] weapons = {"kn", "ca", "re", "ro", "pi", "wr"};
	private String[] rooms = {"ha", "lo", "di", "ki", "ba", "co", "bi", "li", "st"};
	private String[] cards;

	public ClueReasoner()
	{
		numPlayers = players.length;

		// Initialize card info
		cards = new String[suspects.length + weapons.length + rooms.length];
		int i = 0;
		for (String card : suspects)
			cards[i++] = card;
		for (String card : weapons)
			cards[i++] = card;
		for (String card : rooms)
			cards[i++] = card;
		numCards = i;

		// Initialize solver
		solver = new SAT4JSolver();
		addInitialClauses();
	}

	private int getPlayerNum(String player) 
	{
		if (player.equals(caseFile))
			return numPlayers;
		for (int i = 0; i < numPlayers; i++)
			if (player.equals(players[i]))
				return i;
		System.out.println("Illegal player: " + player);
		return -1;
	}

	private int getCardNum(String card)
	{
		for (int i = 0; i < numCards; i++)
			if (card.equals(cards[i]))
				return i;
		System.out.println("Illegal card: " + card);
		return -1;
	}

	private int getPairNum(String player, String card) 
	{
		return getPairNum(getPlayerNum(player), getCardNum(card));
	}

	private int getPairNum(int playerNum, int cardNum)
	{
		return playerNum * numCards + cardNum + 1;
	}    

	public void addInitialClauses() 
	{
		// TO BE IMPLEMENTED AS AN EXERCISE

		// Each card is in at least one place (including case file).
		for (int c = 0; c < numCards; c++) {
			int[] clause = new int[numPlayers + 1];
			for (int p = 0; p <= numPlayers; p++)
				clause[p] = getPairNum(p, c);
			solver.addClause(clause);
		}

		// If a card is one place, it cannot be in another place.
		for (int c = 0; c < numCards; c++) {
			for (int p1 = 0; p1 < numPlayers; p1++) {
				for (int p2 = p1 + 1; p2 <= numPlayers; p2++) {
					int[] clause = {-getPairNum(p1, c), - getPairNum(p2, c)};
					solver.addClause(clause);
				}
			}
		}

		// At least one card of each category is in the case file.

		int[] suspectClause = new int[suspects.length];
		for (int i = 0; i < suspects.length; i++) {
			suspectClause[i] = getPairNum(caseFile, suspects[i]);
		}
		solver.addClause(suspectClause);

		int[] weaponClause = new int[weapons.length];
		for (int i = 0; i < weapons.length; i++) {
			weaponClause[i] = getPairNum(caseFile, weapons[i]);
		}
		solver.addClause(weaponClause);

		int[] roomClause = new int[rooms.length];
		for (int i = 0; i < rooms.length; i++) {
			roomClause[i] = getPairNum(caseFile, rooms[i]);
		}
		solver.addClause(roomClause);


		// No two cards in each category can both be in the case file.
		for(int suspect1 = 0; suspect1 < suspects.length - 1; suspect1++) {
			for(int suspect2 = suspect1 + 1; suspect2 < suspects.length; suspect2++) {
				int[] clause = {-getPairNum("cf", suspects[suspect1]), -getPairNum("cf", suspects[suspect2])};
				solver.addClause(clause);
			}
		}

		for(int weapon1 = 0; weapon1 < weapons.length - 1; weapon1++) {
			for(int weapon2 = weapon1 + 1; weapon2 < weapons.length; weapon2++) {
				int[] clause = {-getPairNum("cf", weapons[weapon1]), -getPairNum("cf", weapons[weapon2])};
				solver.addClause(clause);
			}
		}

		for(int room1 = 0; room1 < rooms.length - 1; room1++) {
			for(int room2 = room1 + 1; room2 < rooms.length; room2++) {
				int[] clause = {-getPairNum("cf", rooms[room1]), -getPairNum("cf", rooms[room2])};
				solver.addClause(clause);
			}
		}


	}

	public void hand(String player, String[] cards) 
	{
		playerNum = getPlayerNum(player);

		// TO BE IMPLEMENTED AS AN EXERCISE
		// Add clause that correspond to the card dealt.
		// Your column fill out yes or no whether have it or not

		boolean[] playerHasCard = new boolean[numCards];
		for (int i = 0; i < cards.length; i++) {
			playerHasCard[getCardNum(cards[i])] = true;
		}
		
		for (int i = 0; i < numCards; i++) {
			int[] clauses = {getPairNum(playerNum, i)};
			if (!playerHasCard[i]) {
				clauses[0] = - clauses[0];
			}
			solver.addClause(clauses);
		}

	}

	public void suggest(String suggester, String card1, String card2, 
			String card3, String refuter, String cardShown) 
	{
		// TO BE IMPLEMENTED AS AN EXERCISE
		
		// If the refuter cannot refute, all the players (except suggester) do not have these three card!
		if (refuter == null) {
			for (String p : players) {
				if (p.equals(suggester)) {
					continue;
				}
				solver.addClause(new int[] {-getPairNum(p, card1)});
				solver.addClause(new int[] {-getPairNum(p, card2)});
				solver.addClause(new int[] {-getPairNum(p, card3)});
			}
		}
		
		// If the the refuter can refuse, the suggester know the cardshown, 
		// 		refuter has one of these card, 
		// 		and players in the middle clockwisely know refuter has one of these card, they also dont have any of these cards.
		else {
			int suggesterNum = getPlayerNum(suggester);
//			int refuterNum = getPlayerNum(refuter);
			// Handle the the fact that every one in the middle do not have these card
			for (int i = 1; i < players.length; i++) {
				String middlePlayer = players[(suggesterNum + i) % players.length];
				if (middlePlayer.equals(refuter)) {
					break;
				}
				solver.addClause(new int[] {-getPairNum(middlePlayer, card1)});
				solver.addClause(new int[] {-getPairNum(middlePlayer, card2)});
				solver.addClause(new int[] {-getPairNum(middlePlayer, card3)});
			}
			
			// Handle the refuter have the cardShown
			if (cardShown != null) {
				solver.addClause(new int[] {getPairNum(refuter, cardShown)});
			} 
			// Spectator mode
			else {
				solver.addClause(new int[] {getPairNum(refuter, card1), getPairNum(refuter, card2), getPairNum(refuter, card3)});
			}
			
			
		}
		
	}

	public void accuse(String accuser, String card1, String card2, 
			String card3, boolean isCorrect)
	{
		// TO BE IMPLEMENTED AS AN EXERCISE
		
		// accuser should not have card1, card2, and card3?
		
		// If the accusation is correct. card1, card2, card3 is in the case file!
		// Since we have initial clauses to check the cards, we dont have to include the
		//		clauses with the cards that are not in the casefile.
		if (isCorrect) {
			int[][] clauses = new int[3][1];
			clauses[0] = new int[] {getPairNum(caseFile, card1)};
			clauses[1] = new int[] {getPairNum(caseFile, card2)};
			clauses[2] = new int[] {getPairNum(caseFile, card3)};
			for (int[] clause : clauses)
				solver.addClause(clause);
		}
		// If not correct. At least 1 of these 3 card is not in the case file!
		else {
			int[] clause = {
					-getPairNum(caseFile, card1),
					-getPairNum(caseFile, card2),
					-getPairNum(caseFile, card3)
			};
			solver.addClause(clause);
		}

	}

	public int query(String player, String card) 
	{
		return solver.testLiteral(getPairNum(player, card));
	}

	public String queryString(int returnCode) 
	{
		if (returnCode == SATSolver.TRUE)
			return "Y";
		else if (returnCode == SATSolver.FALSE)
			return "n";
		else
			return "-";
	}

	public void printNotepad() 
	{
		PrintStream out = System.out;
		for (String player : players)
			out.print("\t" + player);
		out.println("\t" + caseFile);
		for (String card : cards) {
			out.print(card + "\t");
			for (String player : players) 
				out.print(queryString(query(player, card)) + "\t");
			out.println(queryString(query(caseFile, card)));
		}
	}

	public static void clue20FATest() {
		/*
		 * Private game information, unknown to observers:
			case file: [White (wh), Knife (kn), Study (st)]
			Player 1: [Green (gr), Ballroom (ba), Conservatory (co)]
			Player 2: [Candlestick (ca), Wrench (wr), Library (li)]
			Player 3: [Mustard (mu), Rope (ro), Hall (ha)]
			Player 4: [Lounge (lo), Kitchen (ki), Billiard Room (bi)]
			Player 5: [Plum (pl), Revolver (re), Dining Room (di)]
			Player 6: [Peacock (pe), Scarlet (sc), Lead Pipe (pi)]
		 */

		ClueReasoner cr = new ClueReasoner();
		cr.suggest("sc", "mu", "ca", "bi", "mu", null);
		cr.suggest("mu", "pe", "ro", "di", "wh", null);
		cr.suggest("wh", "gr", "re", "ba", "pe", null);
		cr.suggest("gr", "pl", "wr", "lo", "pe", null);
		cr.suggest("pe", "sc", "kn", "li", "pl", null);
		cr.suggest("pl", "pe", "ca", "ki", "mu", null);

		cr.suggest("sc", "wh", "pi", "ha", "wh", null);
		cr.suggest("mu", "sc", "ca", "st", "pl", null);
		cr.suggest("wh", "pe", "kn", "co", "pl", null);
		cr.suggest("gr", "mu", "ca", "bi", "mu", null);
		cr.suggest("pe", "gr", "wr", "li", "sc", null);
		cr.suggest("pl", "mu", "pi", "st", "wh", null);

		cr.suggest("sc", "wh", "ro", "st", "wh", null);
		cr.suggest("mu", "pe", "re", "st", "pe", null);
		cr.suggest("wh", "gr", "pi", "ki", "gr", null);
		cr.suggest("gr", "mu", "ro", "st", "wh", null);
		cr.suggest("pe", "mu", "wr", "ba", "sc", null);
		cr.suggest("pl", "pe", "ro", "st", "wh", null);

		cr.suggest("sc", "pe", "wr", "st", "mu", null);
		cr.suggest("mu", "mu", "ca", "st", "wh", null);
		cr.printNotepad(); // show what was known by non-game-participants before winning accusation

		cr.accuse("mu", "wh", "kn", "st", true);
		cr.printNotepad(); // show what was known by non-game-participants after winning accusation
	}


	public static void main(String[] args) 
	{
		ClueReasoner cr = new ClueReasoner();
		String[] myCards = {"wh", "li", "st"};
		cr.hand("sc", myCards);
		cr.suggest("sc", "sc", "ro", "lo", "mu", "sc");
		cr.suggest("mu", "pe", "pi", "di", "pe", null);
		cr.suggest("wh", "mu", "re", "ba", "pe", null);
		cr.suggest("gr", "wh", "kn", "ba", "pl", null);
		cr.suggest("pe", "gr", "ca", "di", "wh", null);
		cr.suggest("pl", "wh", "wr", "st", "sc", "wh");
		cr.suggest("sc", "pl", "ro", "co", "mu", "pl");
		cr.suggest("mu", "pe", "ro", "ba", "wh", null);
		cr.suggest("wh", "mu", "ca", "st", "gr", null);
		cr.suggest("gr", "pe", "kn", "di", "pe", null);
		cr.suggest("pe", "mu", "pi", "di", "pl", null);
		cr.suggest("pl", "gr", "kn", "co", "wh", null);
		cr.suggest("sc", "pe", "kn", "lo", "mu", "lo");
		cr.suggest("mu", "pe", "kn", "di", "wh", null);
		cr.suggest("wh", "pe", "wr", "ha", "gr", null);
		cr.suggest("gr", "wh", "pi", "co", "pl", null);
		cr.suggest("pe", "sc", "pi", "ha", "mu", null);
		cr.suggest("pl", "pe", "pi", "ba", null, null);
		cr.suggest("sc", "wh", "pi", "ha", "pe", "ha");
		cr.suggest("wh", "pe", "pi", "ha", "pe", null);
		cr.suggest("pe", "pe", "pi", "ha", null, null);
		cr.suggest("sc", "gr", "pi", "st", "wh", "gr");
		cr.suggest("mu", "pe", "pi", "ba", "pl", null);
		cr.suggest("wh", "pe", "pi", "st", "sc", "st");
		cr.suggest("gr", "wh", "pi", "st", "sc", "wh");
		cr.suggest("pe", "wh", "pi", "st", "sc", "wh");
		cr.suggest("pl", "pe", "pi", "ki", "gr", null);
		cr.printNotepad();
		cr.accuse("sc", "pe", "pi", "bi", true);
//		clue20FATest();
	
	}
}
