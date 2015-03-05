package shiftAND_KMP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShiftKMP {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("\nEnter Pattern : ");
		String pattern = br.readLine();
		System.out.print("\nEnter text File name : ");
		String file = br.readLine();
		BufferedReader br1 = new BufferedReader(new FileReader(new File(file)));
		String line, text = new String();
		while ((line = br1.readLine()) != null) 
			text += line + "\n";
		br1.close();
		char[] ctext = text.toCharArray();
		char[] cpattern = pattern.toCharArray();

		long start, end, t1, t2;
		start = System.nanoTime();
		System.out.println("\n---------------KMP Algorithm--------------");
		new ShiftKMP().KMP(text, pattern);
		end = System.nanoTime();
		t2 = end - start;
		System.out.println("Time Taken(nanosec) : " + t2+"\n");
		start = System.nanoTime();
		System.out.println("\n------------ShiftAND Algorithm------------");
		new ShiftKMP().ShiftAnd(ctext, cpattern);
		end = System.nanoTime();
		t1 = end - start;
		System.out.println("Time Taken(nanosec) : " + t1+"\n");
	}

	private void ShiftAnd(char[] S, char[] P) {
		int m = P.length;
		long D[] = new long[Character.MAX_VALUE + 1];
		long T = 0;
		int count = 0;

		for (int i = 0; i <= Character.MAX_VALUE; ++i)
			D[i] = 0;

		for (int i = 0; i < m; ++i)
			D[P[i]] += Math.pow(2, i);

		for (int i = 0; i < S.length; i++) {
			T <<= 1;
			T += 1;
			T &= D[S[i]];
			if ((T & (1L << (m - 1))) != 0) {
				System.out.println("\nPattern found at position : "
						+ (i - m + 1 + 1) + "\n");
				count++;
			}
		}

		if (count == 0)
			System.out.println("\nMatch Not Found\n");
		else
			System.out.println("Total Matches: " + count + "\n");
	}

	private int[] fail;

	private void buildAuto(String P) {
		int n = P.length();
		fail[0] = -1;
		for (int j = 1; j < n; j++) {
			int i = fail[j - 1];
			while ((P.charAt(j) != P.charAt(i + 1)) && i >= 0)
				i = fail[i];
			if (P.charAt(j) == P.charAt(i + 1))
				fail[j] = i + 1;
			else
				fail[j] = -1;
		}
	}

	private void KMP(String S, String P) {
		fail = new int[P.length()];
		buildAuto(P);

		int i = 0, j = 0;
		int len1 = S.length();
		int len2 = P.length();
		int count = 0;
		while (i < len1 && j < len2) {
			if (S.charAt(i) == P.charAt(j)) {
				i++;
				j++;
			} else if (j == 0)
				i++;
			else
				j = fail[j - 1] + 1;

			if (j == len2) {
				j = 0;
				count++;
				System.out.println("\nMatch found at index " + (i - len2 + 1));
			}
		}
		if (count == 0)
			System.out.println("\nMatch Not Found\n");
		else
			System.out.println("\nTotal Matches: " + count + "\n");
	}

}