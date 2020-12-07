import java.util.ArrayList;
import java.util.Arrays;

public class ReversePerfectShuffleNode extends SearchNode{

	private int[]deck = new int[52];

	public ReversePerfectShuffleNode() {
		for(int i=0; i<52; i++) 
			deck[i]=i;
	}

	public Object clone() {
		ReversePerfectShuffleNode newNode = (ReversePerfectShuffleNode) super.clone();
		newNode.deck= (int[]) deck.clone();
		return newNode;
	}

	@Override
	public boolean isGoal() {

		return(deck[4] == 0 && deck[9]== 1 && 
				deck[14] == 2 && deck[19]==3);
	}

	@Override
	public ArrayList<SearchNode> expand() {

		ArrayList<SearchNode> children = new ArrayList<SearchNode>();
		ReversePerfectShuffleNode inChild = (ReversePerfectShuffleNode)this.childClone();
		ReversePerfectShuffleNode outChild = (ReversePerfectShuffleNode)this.childClone();
		int[] array1 = new int[deck.length/2];
		int[] array2 = new int[deck.length/2];
		int index = 0;
		for(int i=0; i<deck.length; i=i+2) {
			array1[index] = inChild.deck[i];
			array2[index] = inChild.deck[i+1];
			index++;
		}
		
		//In-Shuffle
		for(int i=0; i<deck.length/2;i++) {
			inChild.deck[i]=array1[i];
			inChild.deck[i+26] = array2[i];
		}
		children.add(inChild);
		
		//Out-Shuffle
		for(int i=0; i<deck.length/2;i++) {
			outChild.deck[i+26]=array1[i];
			outChild.deck[i] = array2[i];
		}
		children.add(outChild);
		return children;
	}

	public String toString() {
		String str= Arrays.toString(deck);
//		str = str.replace("[","");
//		str = str.replace("]","");
		return str.substring(1,str.length() - 1);
		
	}

	public static void main(String[] args){
		Searcher searcher = new BreadthFirstSearcher();
		ReversePerfectShuffleNode root = new ReversePerfectShuffleNode();
		
		if (searcher.search(root)) {
			// successful search
//			System.out.println("Goal node found in " + searcher.getNodeCount() 
//			+ " nodes.");
//			System.out.println("Goal path:");
			searcher.printGoalPath();
		} else {
			// unsuccessful search
			System.out.println("Goal node not found in " 
					+ searcher.getNodeCount() + " nodes.");
		}
		
	}
}
