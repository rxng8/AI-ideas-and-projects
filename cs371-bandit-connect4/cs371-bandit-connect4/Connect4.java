

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <code>Connect4</code> - a simple GUI for playing basic Connect 4. See
 * <a href="http://en.wikipedia.org/wiki/Connect_Four">http://en.wikipedia.org/wiki/Connect_Four</a>
 *
 * Usage: java Connect4 
 * @author Todd W. Neller
 * @version 1.0
 * *** DO NOT DISTRIBUTE ***
 */
public class Connect4
{
	private static final String[] IMAGE_FILENAMES = {"black100.gif", "red100.gif"}; // image filenames
	private static final Image[] IMAGES = new Image[2];  // loaded images
	private static final int IMAGE_SIZE = 100; // square image dimensions in pixels
	private boolean usingUCT = true; // using UCT versus Simple Monte Carlo
	private boolean donePlaying = false; // whether or not the user is done playing games
	private int rows = 6; // default initial rows (superseded once preference file is created)
	private int cols = 7; // default initial columns (superseded once preference file is created)
	private boolean blackAI = true; // whether or not AI is playing for black
	private boolean redAI = false; // whether of not AI is playing for red
	private CountDownLatch prefsLatch;
	private CountDownLatch gameLatch;
	private CountDownLatch userPlayLatch;
	private JFrame frame;

	/**
	 * <code>main</code> - Create the JFrame and start a new embedded
	 * Connect 4 game.  
	 *
	 * @param args a <code>String[]</code> value - command line arguments
	 */
	public static void main(String[] args) {
		new Connect4().start();
	}

	public void start() {
		// get user game prefs (or quit)
		getPrefs();
		while (!donePlaying) {
			// Play game
			// First, create the game frame and add the play panel.
			frame = new JFrame("Connect 4");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Connect4Panel panel = new Connect4Panel();
			frame.add(panel);
			frame.pack();
			gameLatch = new CountDownLatch(1);
			frame.setVisible(true);
			// Then set the panel thread to start and await its completion.
			new Thread(panel).start();
			try {
				gameLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gameLatch = null;
			// Dispose of frame
			frame.setVisible(false);
			frame.dispose();
			frame = null;
			// get user game prefs (or quit)
			getPrefs();
		}
	}

	public void getPrefs() {
		try { // Try to read user preferences file values (stored from previous preference frame)
			Scanner in = new Scanner(new File("Connect4.prefs"));
			rows = in.nextInt();
			in.nextLine();
			cols = in.nextInt();
			in.nextLine();
			blackAI = in.nextBoolean();
			in.nextLine();
			redAI = in.nextBoolean();
			in.nextLine();
			usingUCT = in.nextBoolean();
			in.close();
		}
		catch (Exception e) {
			// do nothing
		}

		// Create the preferences frame and populate it with text fields, checkboxes, etc. for how the user wishes to proceed.
		JFrame pFrame = new JFrame("Connect 4 Settings");
		pFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pFrame.setLayout(new FlowLayout());
		JTextField rowsField = new JTextField(2);
		rowsField.setText(Integer.toString(rows));
		pFrame.add(rowsField);
		pFrame.add(new JLabel("Rows"));
		JTextField colsField = new JTextField(2);
		colsField.setText(Integer.toString(cols));
		pFrame.add(colsField);
		pFrame.add(new JLabel("Columns"));
		JCheckBox blackAICheckBox = new JCheckBox("Black AI Player");
		blackAICheckBox.setSelected(blackAI);
		pFrame.add(blackAICheckBox);
		JCheckBox redAICheckBox = new JCheckBox("Red AI Player");
		redAICheckBox.setSelected(redAI);
		pFrame.add(redAICheckBox);
		JCheckBox uctCheckBox = new JCheckBox("UCT Player?");
		uctCheckBox.setSelected(usingUCT);
		pFrame.add(uctCheckBox);
		prefsLatch = new CountDownLatch(1);
		JButton playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				donePlaying = false;
				prefsLatch.countDown();
			}
		});
		pFrame.add(playButton);
		JButton quitButton = new JButton("Quit");
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				donePlaying = true;
				prefsLatch.countDown();
			}
		});
		pFrame.add(quitButton);
		pFrame.pack();
		pFrame.setVisible(true);
		// Await the button action that indicates the completion of user preferences.
		try {
			prefsLatch.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		pFrame.setVisible(false);
		// Read the user's preferences from the frame components.
		try {
			rows = Math.max(4, Integer.parseInt(rowsField.getText())); // 4 rows minimum
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		try {
			cols = Math.max(4, Integer.parseInt(colsField.getText()));// 4 columns minimum
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		blackAI = blackAICheckBox.getSelectedObjects() != null;
		redAI = redAICheckBox.getSelectedObjects() != null;
		usingUCT = uctCheckBox.getSelectedObjects() != null;
		pFrame.dispose();
		// Write the user's preferenced to a file.
		try {
			PrintWriter out = new PrintWriter(new File("Connect4.prefs"));
			out.printf("%d Rows\n%d Columns\n%b Black AI Player\n%b Red AI Player\n%b Using UCT\n", rows, cols, blackAI, redAI, usingUCT);
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	class Connect4Panel extends JPanel implements Runnable
	{
		private static final long serialVersionUID = 1L;
		private Connect4State state; // current game state
		private int userPlayColumn; // column for play indicated by user mouse release
		private int mouseX = -IMAGE_SIZE; // last x position of mouse
		private Stack<Connect4State> stateStack = new Stack<Connect4State>();

		public Connect4Panel() 
		{
			super();
			setBackground(new Color(0.95f, 0.95f, 0.815f)); // light tan background
			setPreferredSize(new Dimension(IMAGE_SIZE * cols + 1, IMAGE_SIZE * (rows + 1) + 1));
			setSize(IMAGE_SIZE * cols + 1, IMAGE_SIZE * (rows + 1) + 1);
			System.out.println(usingUCT + " Using UCT");
			state = usingUCT ? new Connect4StateUCT(rows, cols) : new Connect4State(rows, cols);

			addMouseListener(new Connect4Listener());
			addMouseMotionListener(new MouseMotionHandler());
			addKeyListener(new UndoHandler());
			setFocusable(true);
			requestFocusInWindow();

			// load IMAGES
			try {
				for (int i = 0; i < IMAGE_FILENAMES.length; i++)
					IMAGES[i] = ImageIO.read(new File(IMAGE_FILENAMES[i]));
			} catch (IOException e) {
				e.printStackTrace();
			}

			MediaTracker mt = new MediaTracker(this);
			for (int i = 0; i < IMAGE_FILENAMES.length; i++)
				mt.addImage(IMAGES[i], i);
			try {
				mt.waitForAll();
			} catch (Exception e) {
				System.err.println("Exception while loading image.");
			}

			// check that IMAGES loaded
			for (int i = 0; i < IMAGE_FILENAMES.length; i++)
				if (IMAGES[i].getWidth(this) == 0) {
					System.err.printf("Image \"%s\" not loaded.\n", IMAGE_FILENAMES[i]);
					System.exit(0);
				}
		}

		class Connect4Listener extends MouseAdapter 
		{
			public void mouseReleased(MouseEvent e) 
			{
				if (userPlayLatch != null) { // if user input awaited
					userPlayColumn = (int) Math.floor(e.getX() / IMAGE_SIZE); // get the play column from the mouse release x position
					userPlayLatch.countDown(); // indicate user input complete
				}
			}    
		}

		// a mouse motion handler to track piece movement and force repainting during movement
		// based on example from http://www.java2s.com/Code/Java/2D-Graphics-GUI/Imagewithmousedragandmoveevent.htm
		class MouseMotionHandler extends MouseMotionAdapter {
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX(); // track piece movement in mouseX field
				repaint(); // force repaint to animate motion
			}
		}

		// a keypress listener that listens for "u" undo commands
		class UndoHandler extends KeyAdapter {
			public void keyTyped(KeyEvent e) {
				if (userPlayLatch != null && e.getKeyChar() == 'u') {
					if (stateStack.isEmpty())
						JOptionPane.showMessageDialog(null, "Cannot undo.");
					else {
						state = stateStack.pop();
						repaint();
					}
				}
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		public void paintComponent(Graphics g) // draw the board (and moving user piece if user turn)
		{
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.WHITE); // white top rectangle for piece user is considering playing
			g2.fill(new Rectangle2D.Double(0, 0, getWidth(), IMAGE_SIZE));
			g2.setColor(new Color(.7f, .7f, 1f)); // light blue lines
			// draw each board square
			boolean flipBoard = true;
			for (int row = 0; row < rows; row++)
				for (int col = 0; col < cols; col++) {
					int y = IMAGE_SIZE * (flipBoard ? rows - row : row + 1);
					int x = IMAGE_SIZE * col;
					int contents = state.getPiece(row, col);
					// draw square contents (if any)
					if (contents == Connect4State.BLACK) // if black
						g2.drawImage(IMAGES[0], x, y, this); // draw black piece
					else if (contents == Connect4State.RED) // if red 
						g2.drawImage(IMAGES[1], x, y, this); // draw red piece
				}
			// draw grid lines
			for (int row = 0; row <= rows; row++) 
				g2.draw(new Line2D.Double(0, (row + 1) * IMAGE_SIZE, cols * IMAGE_SIZE, (row + 1) * IMAGE_SIZE));
			for (int col = 0; col <= cols; col++) 
				g2.draw(new Line2D.Double(col * IMAGE_SIZE, IMAGE_SIZE, col * IMAGE_SIZE, (rows + 1) * IMAGE_SIZE));
			// if user playing, draw piece at top of column
			if (userPlayLatch != null) {
				g2.drawImage(state.getPlayer() == Connect4State.BLACK ? IMAGES[0] : IMAGES[1], mouseX - IMAGE_SIZE / 2, 0, this);
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			while (!state.isGameOver()) { // While the game is not over,
				// Show game situation
				repaint();
				// Get move
				int col = 0;
				if ((state.getPlayer() == Connect4State.BLACK && blackAI) || (state.getPlayer() == Connect4State.RED && redAI)) {
					// Get computer move
					col = state.getPlayColumn();
					//System.out.println(col);
					System.out.println(state);
					// Attempt to play move or report illegal move.
					if (!state.playColumn(col))
						JOptionPane.showMessageDialog(null, "Illegal move.");
				}
				else {
					// Get user move
					userPlayLatch = new CountDownLatch(1);
					try {
						userPlayLatch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					userPlayLatch = null;
					col = userPlayColumn;
					System.out.println(state);
					stateStack.add((Connect4State) state.clone());
					if (!state.playColumn(col)) {
						JOptionPane.showMessageDialog(null, "Illegal move.");
						stateStack.pop();
					}
				}
			}
			repaint(); // Redraw final game situation
			// Pop up a dialog with game results.
			String[] endMessages = {"Red wins!", "Draw.", "Black wins!"};
			JOptionPane.showMessageDialog(null, endMessages[state.getWinner() + 1]);
			gameLatch.countDown();
		}
	}
}
