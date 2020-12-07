import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GridNavigationNodeTest {

	public static void main(String[] args) throws IOException {
		// Generate a maze of a given number of internal rows and columns and save an image of it.
		final int ROWS = 100, COLS = 100;
		Maze maze = new Maze(ROWS, COLS);
		
		// Export maze to file "maze.png" and read it in as an image.
		maze.exportImage("maze");
		File imageFile = new File("maze.png");
		BufferedImage image = ImageIO.read(imageFile);
		
		// Randomly clear pixels with a fixed probability, admitting possible additional shorter solutions.
		final double CLEAR_PROB = .03; // As probability goes to 0/1, the path tends towards windy/straight.
		int rows = image.getHeight();
		int cols = image.getWidth();
		int whiteRGB = Color.WHITE.getRGB();
		for (int y = 0; y < rows; y++)
			for (int x = 0; x < cols; x++)
				if (Math.random() < CLEAR_PROB)
					image.setRGB(x, y, whiteRGB);
		
		// Export new updated maze image as "maze.png".
		ImageIO.write(image, "png", new File("maze.png"));    

		// Convert to binary isBlocked representation.
		boolean[][] isBlocked = new boolean[rows][cols];
		int blackRGB = Color.BLACK.getRGB();
		for (int y = 0; y < rows; y++)
			for (int x = 0; x < cols; x++)
				isBlocked[y][x] = image.getRGB(x, y) == blackRGB;

		// Perform A* search using GridNavigateNode.
		GridNavigationNode root = new GridNavigationNode(0, 1, rows - 1, cols - 2, isBlocked);
		BestFirstSearcher searcher = new BestFirstSearcher(new AStarComparator());
		if (!searcher.search(root)) {
			System.out.println("Error: No solution found.");
			System.exit(1);
		}
		
		// Map solution path onto image.
		GridNavigationNode pathNode = (GridNavigationNode) searcher.getGoalNode();
		int greenRGB = Color.GREEN.getRGB();
		while (pathNode != null) {
			image.setRGB(pathNode.getCol(), pathNode.getRow(), greenRGB);
			pathNode = (GridNavigationNode) pathNode.parent;
		}
		
		// Display image with solution.
		JFrame frame = new JFrame();
		ImageIcon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon);
		frame.add(label);
		frame.setDefaultCloseOperation
		(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
		// Write solution image to file "maze-solution.png".
		ImageIO.write(image, "png", new File("maze-solution.png")); 
	}

}
