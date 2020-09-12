import java.util.Arrays;
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
		for (int[] line : ratingMatrix) {
			sb.append(Arrays.toString(line) + "\n");
			System.out.println(line.length);
		}
		
		
		return sb.toString();
	}
	
	public static void main(String[] args) {

//		Scanner sc = new Scanner(System.in);
//		
//		int students = sc.nextInt();
//		int groups = sc.nextInt();
//		
//		int[][] ratingMatrix = new int[students][students];
//		for (int i = 0; i < students; i++) {
//			for (int j = 0; j < students; i++) {
//				ratingMatrix[i][j] = sc.nextInt();
//			}
//		}
//		sc.close();
//		PreferredGroup state = new PreferredGroup(students, groups, ratingMatrix);
//		System.out.println(state);
		
		int students = 16;
		int groups = 3;
		
		int[][] ratingMatrix = {
				{0 ,0, 6, 8, 5, 6, 2, 2, 6, 2, 0, 2, 2, 2, 5, 2},
				{1, 0, 6, 5, 9, 10, 10, 7, 7, 9, 10, 4, 1, 6, 3, 4},
				{1, 0, 0, 1, 10, 1, 5, 4, 3, 2, 2, 8, 0, 1, 2, 5},
				{1, 6, 2, 0, 8, 5, 0, 3, 6, 9, 5, 3, 9, 10, 1, 0},
				{3, 6, 9, 1, 0, 7, 2, 8, 0, 3, 7, 5, 4, 3, 2, 5},
				{8, 5, 0, 8, 8, 0, 3, 8, 8, 8, 8, 6, 8, 0, 7, 3},
				{4, 6, 3, 10, 10, 7, 0, 10, 1, 1, 10, 2, 5, 2, 9, 3},
				{6, 9, 4, 10, 7, 5, 7, 0, 4, 6, 2, 10, 3, 10, 1, 0},
				{0, 3, 4, 2, 8, 10, 9, 6 ,0, 2, 10, 5, 3, 6, 8, 8},
				{9, 8, 10, 3, 2, 7, 9, 6, 1, 0, 8, 3, 9, 0, 1, 10},
				{0, 9, 10, 0, 1, 8, 4, 1, 10, 8, 0, 5, 7, 0, 0, 9},
				{4, 1, 0, 10, 7, 5, 2, 4, 3, 3, 2, 0, 4, 3, 2, 4},
				{2, 9, 5, 1, 9, 7, 0, 2, 5, 3, 7, 3, 0, 0, 8, 9},
				{10, 4, 3, 10, 9, 0, 7, 7, 5, 9, 7, 4, 8, 0, 9, 6},
				{7, 6, 6, 2, 3, 9, 5, 5, 4, 8, 7, 6, 2, 2, 0, 1},
				{9, 3, 4, 2, 3, 0, 0, 9, 6, 6, 2, 8, 9, 5, 1, 0}
		};
		
		PreferredGroup state = new PreferredGroup(students, groups, ratingMatrix);
		System.out.println(state);
		
	}
	
}
