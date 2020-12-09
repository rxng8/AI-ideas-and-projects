import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
 * Suit Yourself Solo is a novel solitaire variation of Sid Sackson's 2-4 player card game "Suit Yourself" from "A Gamut of Games" by Sid Sackson, Dover Publications, 1982, p. 93.
 * 
 * In this card game, the object is to empty the stacks of cards in the minimum number of turns.
 * 
 * For each generated game, one supplies:
 * - numSuits: the number of card suits (numbered 0 through (numSuits - 1))
 * - numRanks: the number of cards in each suit (ranks don't matter to the game and will not be represented)
 * - numColumns: the number of columns the cards are dealt into as evenly as possible (numbered 0 through (numColumns - 1))
 * - seed: the pseudorandom number seed for deterministically generating initial game states
 * 
 * On each turn, the player selects a suit number for a suit of a non-empty stack top card.  
 * The player then removes top cards (and potentially successor top cards) of that suit until each stack is either
 * (1) empty, or (2) having a top card of a different suit.
 * 
 * @author Todd W. Neller
 *
 */
public class SuitYourselfSoloGenerator {
	
	public static ArrayList<Stack<Integer>> generate(int numSuits, int numRanks, int numColumns, long seed) {
		Random random = new Random(seed);
		ArrayList<Stack<Integer>> columns = new ArrayList<>();
		for (int c = 0; c < numColumns; c++)
			columns.add(new Stack<Integer>());
		Stack<Integer> cardSuits = new Stack<>();
		for (int s = 0; s < numSuits; s++)
			for (int r = 0; r < numRanks; r++)
				cardSuits.push(s);
		Collections.shuffle(cardSuits, random);
		int c = 0;
		while (!cardSuits.isEmpty()) {
			columns.get(c++).push(cardSuits.pop());
			if (c == numColumns)
				c = 0;
		}
		return columns;
	}	

}