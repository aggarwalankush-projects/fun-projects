import java.util.ArrayList;
import java.util.Collections;

class NormalED {
	public int[][] distance = null;
	Helper hl = new Helper();
	int len1, len2;
	String str1, str2;

	public int computeEditDistance(String str1, String str2) {
		len1 = str1.length();
		len2 = str2.length();
		this.str1 = str1;
		this.str2 = str2;
		distance = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++)
			distance[i][0] = i;
		for (int j = 1; j <= len2; j++)
			distance[0][j] = j;

		for (int i = 1; i <= len1; i++)
			for (int j = 1; j <= len2; j++) {
				distance[i][j] = hl
						.minimum(
								distance[i - 1][j] + 1, // deletion
								distance[i][j - 1] + 1, // insertion
								distance[i - 1][j - 1]
										+ ((str1.charAt(i - 1) == str2
												.charAt(j - 1)) ? 0 : 1)); // substitution

			}

		return distance[len1][len2];
	}

	public ArrayList<String> getSequence() {
		return hl.getEditSequence(distance, len1, len2);
	}

	public void printSequence() {
		hl.print(this.getSequence(), str1, str2);
	}

}

class TranspositionED {
	public int[][] distance = null;
	Helper hl = new Helper();
	int len1, len2;
	String str1, str2;

	public int computeEditDistance(String str1, String str2) {
		len1 = str1.length();
		len2 = str2.length();
		this.str1 = str1;
		this.str2 = str2;

		distance = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++)
			distance[i][0] = i;
		for (int j = 1; j <= len2; j++)
			distance[0][j] = j;

		for (int i = 1; i <= len1; i++)
			for (int j = 1; j <= len2; j++) {

				distance[i][j] = hl
						.minimum(
								distance[i - 1][j] + 1, // deletion
								distance[i][j - 1] + 1, // insertion
								distance[i - 1][j - 1]
										+ ((str1.charAt(i - 1) == str2
												.charAt(j - 1)) ? 0 : 1)); // substitution
				if (i > 1 && j > 1
						&& (str1.charAt(i - 1) == str2.charAt(j - 2))
						&& (str1.charAt(i - 2) == str2.charAt(j - 1)))
					distance[i][j] = hl.minimum(distance[i][j],
							distance[i - 2][j - 2] + 1); // transposition
			}

		return distance[len1][len2];
	}

	public ArrayList<String> getSequence() {
		return hl.getTransEditSequence(distance, len1, len2);
	}

	public void printSequence() {
		hl.print(this.getSequence(), str1, str2);
	}

}

class SubStringMatch {

	public int[][] distance = null;
	Helper hl = new Helper();
	int len1, len2;
	String str1, str2;
	int best = 0, pos1 = 0, pos2 = 0;

	public int findMatch(String str1, String str2) {
		len1 = str1.length();
		len2 = str2.length();

		this.str1 = str1;
		this.str2 = str2;
		distance = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++)
			distance[i][0] = -i;
		for (int j = 1; j <= len2; j++)
			distance[0][j] = -j;

		for (int i = 1; i <= len1; i++)
			for (int j = 1; j <= len2; j++) {
				distance[i][j] = hl
						.maximum(
								distance[i - 1][j] - 1, // deletion
								distance[i][j - 1] - 1, // insertion
								distance[i - 1][j - 1]
										+ ((str1.charAt(i - 1) == str2
												.charAt(j - 1)) ? 2 : -1)// substitution
						);
				
			}
		
		return distance[len1][len2];
	}

	public void printSequence() {
		hl.printSMSequence(distance, len1, len2);
	}
}

class Helper {

	String str1, str2;
	EditDistance ed = new EditDistance();

	@SuppressWarnings("static-access")
	public Helper() {
		str1 = ed.str1;
		str2 = ed.str2;
	}

	ArrayList<String> editSequence = new ArrayList<String>();

	public void print(ArrayList<String> editSequence, String str1, String str2) {

		ArrayList<Character> strc1 = new ArrayList<Character>();
		ArrayList<Character> strc2 = new ArrayList<Character>();

		int c1 = 0, c2 = 0;
		for (int i = 0; i < editSequence.size(); i++) {
			String x = editSequence.get(i);
			
			if (x.equals("I")) {
				strc1.add('-');
				c1++;
				strc2.add(str2.charAt(i - c2));
			} else if (x.equals("D")) {
				strc1.add(str1.charAt(i - c1));
				strc2.add('-');
				c2++;
			} else {
				strc1.add(str1.charAt(i - c1));
				strc2.add(str2.charAt(i - c2));
			}

		}

		System.out.println("String 1 : " + strc1);
		System.out.println("String 2 : " + strc2);
		System.out.println("Operation: " + editSequence);
	}

	public ArrayList<String> getEditSequence(int distance[][], int i, int j) {
		editSequence.clear();
		

		findSequence(distance, i, j);
		Collections.reverse(editSequence);

		return editSequence;
	}

	public void findSequence(int distance[][], int i, int j) {
		if (i >= 0 && j >= 0) {
			if ((i - 1 >= 0) && (distance[i][j] == distance[i - 1][j] + 1)) {
				editSequence.add("D");
				findSequence(distance, i - 1, j);
			} else if ((j - 1 >= 0)
					&& (distance[i][j] == distance[i][j - 1] + 1)) {
				editSequence.add("I");
				findSequence(distance, i, j - 1);
			} else if ((i - 1 >= 0 && j - 1 >= 0)
					&& (distance[i][j] == distance[i - 1][j - 1] + 1)) {
				editSequence.add("S");
				findSequence(distance, i - 1, j - 1);
			} else if ((i - 1 >= 0 && j - 1 >= 0)
					&& (distance[i][j] == distance[i - 1][j - 1] + 0)) {
				editSequence.add("M");
				findSequence(distance, i - 1, j - 1);
			}

		}

	}

	public ArrayList<String> getTransEditSequence(int distance[][], int i, int j) {
		editSequence.clear();
		transFindSequence(distance, i, j);
		Collections.reverse(editSequence);
		return editSequence;
	}

	public void transFindSequence(int distance[][], int i, int j) {

		if (i >= 0 && j >= 0) {
			if ((i - 2 >= 0 && j - 2 >= 0)
					&& (distance[i][j] == distance[i - 2][j - 2] + 1)
					&& (str1.charAt(i - 1) == str2.charAt(j - 2))
					&& (str1.charAt(i - 2) == str2.charAt(j - 1))) {
				editSequence.add("T");
				editSequence.add("T");
				transFindSequence(distance, i - 2, j - 2);
			} else if ((i - 1 >= 0)
					&& (distance[i][j] == distance[i - 1][j] + 1)) {
				editSequence.add("D");
				transFindSequence(distance, i - 1, j);
			} else if ((j - 1 >= 0)
					&& (distance[i][j] == distance[i][j - 1] + 1)) {
				editSequence.add("I");
				transFindSequence(distance, i, j - 1);
			} else if ((i - 1 >= 0 && j - 1 >= 0)
					&& (distance[i][j] == distance[i - 1][j - 1] + 1)) {
				editSequence.add("S");
				transFindSequence(distance, i - 1, j - 1);
			} else if ((i - 1 >= 0 && j - 1 >= 0)
					&& (distance[i][j] == distance[i - 1][j - 1] + 0)) {
				editSequence.add("M");
				transFindSequence(distance, i - 1, j - 1);
			}

		}

	}

	public void printSMSequence(int distance[][], int i, int j) {
		editSequence.clear();
		findSMSequence(distance, i, j);
		strc1.clear();
		strc2.clear();
		p1.add(0, 0);
		p2.add(0, 0);
		int r = 0, c = 0;
		for (int t = 1; t < p1.size(); t++) {
			if (p1.get(t) == p1.get(t - 1))
				strc1.add(t-1, '-');
			else {
				strc1.add(t-1, str1.charAt(r));r++;
			}
			
			if (p2.get(t) == p2.get(t - 1))
				strc2.add(t-1, '-');
			else {
				strc2.add(t-1, str2.charAt(c));c++;
			}
		}
		
		
		System.out.println("String 1 : "+strc1);
		System.out.println("String 2 : "+strc2);

		

	}

	ArrayList<Integer> p1 = new ArrayList<Integer>();
	ArrayList<Integer> p2 = new ArrayList<Integer>();
	ArrayList<Character> strc1 = new ArrayList<Character>();
	ArrayList<Character> strc2 = new ArrayList<Character>();
	int med = 0;

	public void findSMSequence(int distance[][], int i, int j) {
		if (i >= 0 && j >= 0) {
			if ((i - 1 >= 0 && j - 1 >= 0)
					&& (distance[i][j] == distance[i - 1][j - 1] + 2)
					&& (str1.charAt(i - 1) == str2.charAt(j - 1))) {
				
				findSMSequence(distance, i - 1, j - 1);
				p1.add(i);
				p2.add(j);

			} else if ((i - 1 >= 0 && j - 1 >= 0)
					&& (distance[i][j] == distance[i - 1][j - 1] - 1)) {
				
				findSMSequence(distance, i - 1, j - 1);
				p1.add(i);
				p2.add(j);
			} else if ((j - 1 >= 0)
					&& (distance[i][j] == distance[i][j - 1] - 1)) {
				
				findSMSequence(distance, i, j - 1);
				p1.add(i);
				p2.add(j);
			} else if ((i - 1 >= 0)
					&& (distance[i][j] == distance[i - 1][j] - 1)) {
				
				findSMSequence(distance, i - 1, j);
				p1.add(i);
				p2.add(j);
			}

		}

	}

	public int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	public int maximum(int a, int b, int c) {
		return Math.max(Math.max(a, b), c);
	}

	public int minimum(int a, int b) {
		return Math.min(a, b);
	}

}

public class EditDistance {
	public static String str1, str2;

	public static void main(String[] args) {
		/*args = new String[2];
		args[0] = "haksh";
		args[1] = "aksh";*/

		if (args == null | args.length != 2) {
			System.out.println("\nFormat to run");
			System.out.println("java EditDistance String1 String2\n");
			return;
		}
		str1 = args[0];
		str2 = args[1];
		System.out
				.println("\nEdit Operation:\nM--Found Match\nI--Insertion\nD--Deletiion\nS--Substitution\n");

		int edistance = 0, tedistance = 0;
		long start, end, time;

		/*----------------------------
		 * Calculate edit distance
		 * ------------------------------------ */
		NormalED ed = new NormalED();
		TranspositionED ted = new TranspositionED();
		start = System.nanoTime();
		tedistance = ted.computeEditDistance(args[0], args[1]);
		end = System.nanoTime();

		long time2 = end - start;

		start = System.nanoTime();
		edistance = ed.computeEditDistance(args[0], args[1]);
		end = System.nanoTime();

		time = end - start;

		/*
		 * ---------------- Normal edit distance ------------------------------
		 */
		System.out
				.println("-------------Normal Edit Distance Algorithm----------------------");
		System.out.println("\nEdit Distance is : " + edistance);
		ed.printSequence();
		System.out.println("Time taken(nanosec): " + time);

		/*----------------------------
		 * Transposition edit distance
		 * ------------------------------------ */

		System.out
				.println("\n-------------Transposition Edit Distance Algorithm--------------------------");
		System.out.println("\nTransposition Edit Distance is : " + tedistance);
		ted.printSequence();
		System.out.println("Time taken(nanosec): " + time2);

		/*
		 * ---------------- Performance Compare ------------------------------
		 */
		System.out
				.println("\n-------------Performance comparison Edit Distance Algorithm-----------------");

		if (time2 < time)
			System.out
					.println("\nEdit Distance with Transposition is faster by (nanosec):"
							+ (time - time2));

		else
			System.out.println("\nNormal Edit Distance is faster by (nanosec):"
					+ (time2 - time));

		/*
		 * -------------Substring matching----------
		 */

		SubStringMatch ssm = new SubStringMatch();

		edistance = ssm.findMatch(args[0], args[1]);

		System.out
				.println("\n-------------Substring Match Algorithm version------------"
						+ "-------------------");
		System.out.print("\nString 1 : " + args[0]);

		System.out.println("\nString 2 : " + args[1]);

		ssm.printSequence();
		System.out.println();
		
		
		/*
		 * Code can be used anywhere to print matrix
		 * 
		 * for (int[] x : distance) { System.out.println(); for (int y : x) {
		 * System.out.print(y + " "); } }
		 */

	}

}