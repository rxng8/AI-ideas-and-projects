import java.util.Random;
import java.util.Scanner;

public class PreferredGroup implements State {

	public static Random random = new Random();
	public int groups, students;
	public int[][] ratingMatrix;
	public int[] studentMap;
	public int lastIndex1, lastIndex2;
	
	public PreferredGroup(int students, int groups, int[][] ratingMatrix) {
		this.groups = groups;
		this.students = students;
		this.ratingMatrix = ratingMatrix;
		
		int[] studentMap = new int[students];
		for (int i = 0; i < students; i++) {
			studentMap[i] = i;
		}
	}
	
	@Override
	public void step() {
		// pick a random item
		
		int i = random.nextInt(students);
		int j = random.nextInt(students);
		while(i % groups == j % groups) {
			j = random.nextInt(students);
		}
		
		// store undo information
		lastIndex1 = i;
		lastIndex2 = j;
		
		// swap
		swap(i, j);
	}

	@Override
	public void undo() {
		swap(lastIndex1, lastIndex2);
	}
	
	public void swap(int i, int j) {
		int tmp = studentMap[i];
		studentMap[i] = studentMap[j];
		studentMap[j] = tmp;
	}

	@Override
	public double energy() {
		int energy = 0;
		for (int group = 0; group < groups; group++) {
			for (int s1 = group; s1 < studentMap.length; s1 += group) {
				for (int s2 = group; s2 < studentMap.length; s2 += group) {
					energy += (10 - ratingMatrix[studentMap[s1]][studentMap[s2]]);
				}
			}
		}
		return energy;
	}
	
	public Object clone() {
		try {
			PreferredGroup copy = (PreferredGroup) super.clone();
			copy.ratingMatrix = ratingMatrix.clone();
			copy.groups = groups; 
			copy.students = students;
			copy.studentMap = studentMap.clone();
			copy.lastIndex1 = lastIndex1; 
			copy.lastIndex2 = lastIndex2;
			return copy;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		
		
		return sb.toString();
	}
	
	public static void main(String[] args) {

		Scanner sc = new Scanner("group-input.txt");
		
		int students = sc.nextInt();
		int groups = sc.nextInt();
		
		int[][] ratingMatrix = new int[students][students];
		for (int i = 0; i < students; i++) {
			for (int j = 0; j < students; i++) {
				ratingMatrix[i][j] = sc.nextInt();
			}
		}
		sc.close();
		PreferredGroup state = new PreferredGroup(students, groups, ratingMatrix);
		System.out.println(state);
	}
	
}
