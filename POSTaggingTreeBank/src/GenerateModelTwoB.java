

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.model.ModelType;

public class GenerateModelTwoB {
	String file = "treebank-sentences.txt";
	String trainFile = "trainingB.train";
	String testFile = "testingB.test";
	String modelFile = "secondModel.bin";


	public void divideFile() throws Exception {
		GenerateTagsTwoB gta = new GenerateTagsTwoB();
		BufferedReader brFile = new BufferedReader(new FileReader(file));
		BufferedWriter bwTrain = new BufferedWriter(new FileWriter(trainFile));
		BufferedWriter bwTest = new BufferedWriter(new FileWriter(testFile));

		String line = null;
		int j = 0;

		while ((line = brFile.readLine()) != null) {
			StringBuilder newLine = new StringBuilder();
			j++;
			String[] str = line.split("\t");
			String[] ss = str[1].split(" ");
			for (String s : ss) {
				String[] word = s.split("/");
				String tag = gta.doMapping(word[word.length - 1]);
				for (int i = 0; i < word.length - 1; i++)
					newLine.append(word[i] + "/");
				newLine.deleteCharAt(newLine.length() - 1);
				newLine.append("_" + tag + " ");
			}
			if (j <= 500) {
				bwTrain.write(newLine.toString() + "\n");
			} else {
				bwTest.write(newLine.toString() + "\n");
			}

		}
		brFile.close();
		bwTest.close();
		bwTrain.close();

		System.out.println("done generating testing and training files");

	}

	@SuppressWarnings("deprecation")
	public void generateModel() throws Exception {

		POSModel model = null;

		InputStream dataIn = null;

		dataIn = new FileInputStream(trainFile);
		ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn,
				"UTF-8");
		ObjectStream<POSSample> sampleStream = new WordTagSampleStream(
				lineStream);

		model = POSTaggerME.train("en", sampleStream, ModelType.MAXENT, null,
				null, 0, 5);

		OutputStream modelOut = null;

		modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
		model.serialize(modelOut);

		System.out.println("model generation done");
	}

	public static void main(String[] args) {

		GenerateModelTwoB gm = new GenerateModelTwoB();

		try {
			gm.divideFile();
			gm.generateModel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
