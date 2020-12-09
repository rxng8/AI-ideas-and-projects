import java.util.Scanner;
import java.util.Stack;

public class Connect4Game {

	/**
	 * A text-based version of the Connect 4 game.  See
	 * <a href="http://en.wikipedia.org/wiki/Connect_Four">http://en.wikipedia.org/wiki/Connect_Four</a>
	 * <ul>
	 *   <li>Initially, prompt the user for positive integers with prompts "Board rows? " and "Board columns? ", and "y"/"n" Strings with prompts "Black computer player (y/n)? " and "Red computer player (y/n)? ".</li>
	 *   <li>Create an initial game state accordingly.</li>
	 *   <li>Print "At any time, enter "u" to undo."
	 *   <li>While the game is not over:
	 *   	<ul>
	 *   		<li>Print the current board position.</li>
	 *   		<li>Prompt with "Black, enter column: " or "Red, enter column: " depending on the current player.</li>
	 *   		<li>If the current player is a computer player, get the recommended computer play from the game state and print it.  Otherwise, read the column integer from the user.</li>
	 *   		<li>If the user instead enters "u", attempt to undo the move to the previous user turn (black or red).  If there is no such previous move, print "Cannot undo."  Either way, continue the next iteration of the play loop.
	 *   		<li>Attempt to make the play.  If the play is illegal, print "Illegal move."</li>
	 *   	</ul>
	 *   </li>
	 *   <li>Print the final board position.</li>
	 *   <li>Print the game outcome, that is, "Black wins!", "Red wins!", or "Draw.".</li>
	 * </ul>
	 * @param args
	 *
	 * @author Todd W. Neller
	 * @version 1.0
	 * *** DO NOT DISTRIBUTE ***
	 */
	public static void main(String[] args) {
		// Initialization
		Scanner in = new Scanner(System.in);
		// - get board dimensions
		System.out.print("Board rows? ");
		int rows = in.nextInt();
		System.out.print("Board columns? ");
		int columns = in.nextInt();
		in.nextLine();
		System.out.print("Black computer player (y/n)? ");
		boolean blackAI = in.nextLine().toLowerCase().trim().charAt(0) == 'y';
		System.out.print("Red computer player (y/n)? ");
		boolean redAI = in.nextLine().toLowerCase().trim().charAt(0) == 'y';
		System.out.println("At any time, enter \"u\" to undo."); 
		Stack<Connect4State> stateStack = new Stack<Connect4State>();
		// - create board (state)
		Connect4State state = new Connect4State(rows, columns);
		// Game play loop
		while (!state.isGameOver()) { // not game over
			// Print game situation
			System.out.println(state);
			System.out.print((state.getPlayer() == Connect4State.BLACK) ? "Black, enter column: " : "Red, enter column: ");
			// Get move
			int col = 0;
			if ((state.getPlayer() == Connect4State.BLACK && blackAI) || (state.getPlayer() == Connect4State.RED && redAI)) { // computer turn
				// Get computer move
				col = state.getPlayColumn();
				System.out.println(col);
				// Play move
				if (!state.playColumn(col))
					System.out.println("Illegal move.");
			}
			else {
				// Get user move
				String move = in.nextLine().trim().toLowerCase(); 
				if (move.charAt(0) == 'u') { // undo
					if (stateStack.isEmpty())
						System.out.println("Cannot undo.");
					else 
						state = stateStack.pop();
					continue;
				}
				// Play move
				col = Integer.parseInt(move);
				stateStack.add((Connect4State) state.clone());
				if (!state.playColumn(col)) {
					System.out.println("Illegal move.");
					stateStack.pop();
				}		
			}

		}
		// Conclusion (print winner)
		System.out.println(state);
		String[] endMessages = {"Red wins!", "Draw.", "Black wins!"};
		System.out.println(endMessages[state.getWinner() + 1]);
		in.close();
	}

}
