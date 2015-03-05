import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class GenerateTags {

	String firstModel = "firstModel.bin";
	String testFile = "testing.test";
	String predictions = "part-I-predictions.tsv";

	@SuppressWarnings("deprecation")
	public void doAllWork() throws Exception {

		InputStream modelIn = null;
		POSModel model = null;

		modelIn = new FileInputStream(firstModel);
		model = new POSModel(modelIn);

		POSTaggerME tagger = new POSTaggerME(model);
		BufferedReader br = new BufferedReader(new FileReader(testFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(predictions));
		String line = new String();
		StringBuilder newTag = new StringBuilder();
		StringBuilder oldTag = new StringBuilder();
		int id = 500;
		while ((line = br.readLine()) != null) {
			String[] sent = line.split(" ");
			StringBuilder tagline = new StringBuilder();
			for (String s : sent) {
				String words[] = s.split("_");
				String tag = tagger.tag(words[0]);

				String[] tt = tag.split("/");

				newTag.append(tt[tt.length - 1] + " ");

				oldTag.append(words[1] + " ");
				tagline.append(tag + " ");
			}
			bw.write(id + "\t" + line.replace('_', '/') + "\t" + tagline + "\n");

			id++;

		}

		String[] oldTags = oldTag.toString().split(" ");
		String[] newTags = newTag.toString().split(" ");

		int counter = 0;
		int len = newTags.length;

		HashSet<String> uniqueTags = new HashSet<String>();
		HashSet<String> uniqueTagsNew = new HashSet<String>();
		for (int i = 0; i < len; i++) {
			uniqueTags.add(oldTags[i]);
			uniqueTagsNew.add(newTags[i]);
			if (newTags[i].equals(oldTags[i])) {
				counter++;
			}
		}
		System.out.println(uniqueTags.size());
		System.out.println(uniqueTagsNew.size());
		int ir = 0;
		for (String s : uniqueTags) {
			if (uniqueTagsNew.contains(s))
				ir++;
			else
				System.out.print(s + " ");
		}
		System.out.println("\n" + ir);
		ir = 0;
		for (String s : uniqueTagsNew) {
			if (uniqueTags.contains(s))
				ir++;
			else
				System.out.print(s + " ");
		}
		System.out.println("\n" + ir);
		System.out.println(len);
		double totalAccuracy = (double) counter / len * 100;
		System.out.println("Accuracy : " + (double) counter / len * 100);

		int tagsCount = uniqueTags.size();

		int[][] confusionMatrix = new int[tagsCount][tagsCount];

		String[] alltags = new String[tagsCount];

		alltags = uniqueTags.toArray(alltags);

		for (int l = 0; l < tagsCount; l++) {
			String tag = alltags[l];
			for (int i = 0; i < len; i++) {
				if (oldTags[i].equals(tag)) {
					if (oldTags[i].equals(newTags[i]))
						confusionMatrix[l][l]++;
					else {
						for (int q = 0; q < alltags.length; q++)
							if (alltags[q].equals(newTags[i]))
								confusionMatrix[l][q]++;
					}
				}
			}
		}
		System.out.println(Arrays.toString(alltags));

		BufferedWriter bwr = new BufferedWriter(new FileWriter(
				"confusionMatrix.tsv"));
		for (String s : alltags)
			bwr.write("\t" + s);
		bwr.write("\n");
		for (int i = 0; i < tagsCount; i++) {
			bwr.write(alltags[i]);

			for (int j = 0; j < tagsCount; j++)
				bwr.write("\t" + confusionMatrix[i][j]);

			bwr.write("\n");
		}

		double perAcc, sum = 0;
		BufferedWriter bwrr = new BufferedWriter(new FileWriter(
				"perTagAccuracy.tsv"));
		bwrr.write("Original POS Category\tAccuracy\n");
		for (int i = 0; i < tagsCount; i++) {
			sum = 0;
			bwrr.write(alltags[i] + "\t");

			for (int j = 0; j < tagsCount; j++) {
				sum += confusionMatrix[i][j];
			}
			perAcc = confusionMatrix[i][i] / sum * 100;
			bwrr.write("" + Math.round(perAcc * 100.0) / 100.0);
			bwrr.write("\n");
		}
		bwrr.write("Overall Accuracy\t" + Math.round(totalAccuracy * 100.0)
				/ 100.0);
		bwrr.close();
		bwr.close();
		br.close();
		bw.close();

	}

	public static void main(String[] args) {
		GenerateTags generateTags = new GenerateTags();
		try {
			generateTags.doAllWork();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
