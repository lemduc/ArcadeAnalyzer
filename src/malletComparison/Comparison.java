package malletComparison;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.CharSequenceReplace;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.Maths;
import edu.usc.softarch.arcade.config.Config;
import edu.usc.softarch.arcade.topics.CamelCaseSeparatorPipe;
import edu.usc.softarch.arcade.topics.DocTopicItem;
import edu.usc.softarch.arcade.topics.StemmerPipe;
import edu.usc.softarch.arcade.topics.TopicItem;
import edu.usc.softarch.arcade.topics.TopicUtil;
import edu.usc.softarch.arcade.util.FileListing;
import edu.usc.softarch.arcade.util.FileUtil;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Comparison {

	public static void main(String[] args) throws IOException {
		String src = args[2];
		int numTopics = 175;
		compareTopicWords(args, src);
		// compareDocTopics(args, src, topics);
	}

	private static void compareTopicWords(String[] args, String src) {
		ParallelTopicModel modelP = getModel(src, args[0]);
		ParallelTopicModel modelQ = getModel(src, args[1]);

		Map<Integer, Topic> pTopics = getWordTopicDistributions(modelP);
		Map<Integer, Topic> qTopics = getWordTopicDistributions(modelQ);

		assert (pTopics.get(0).wordNormWeights.keySet().size() == qTopics
				.get(0).wordNormWeights.keySet().size());

		TreeSet<String> sortedWords = new TreeSet(
				pTopics.get(0).wordNormWeights.keySet());
		double[] minJsDivs = new double[pTopics.keySet().size()];
		for (int pTopicId = 0; pTopicId < pTopics.keySet().size(); pTopicId++) {
			double minJsDiv = 1;
			int minQTopicId = -1;
			for (int qTopicId = 0; qTopicId < qTopics.keySet().size(); qTopicId++) {
				double[] sortedPWordNormWeights = new double[sortedWords.size()];
				double[] sortedQWordNormWeights = new double[sortedWords.size()];
				int wordIdx = 0;
				for (String sortedWord : sortedWords) {
					sortedPWordNormWeights[wordIdx] = pTopics.get(pTopicId).wordNormWeights
							.get(sortedWord);
					sortedQWordNormWeights[wordIdx] = qTopics.get(qTopicId).wordNormWeights
							.get(sortedWord);
					wordIdx++;
				}

				double jsDiv = Maths.jensenShannonDivergence(
						sortedPWordNormWeights, sortedQWordNormWeights);
				System.out.println("jsDiv: " + jsDiv + " for p topic "
						+ pTopicId + " and q topic " + qTopicId);
				if (jsDiv < minJsDiv) {
					minJsDiv = jsDiv;
					minQTopicId = qTopicId;
				}
			}
			System.out
					.println("\t minimum jsDiv for p topic " + pTopicId
							+ " is q topic " + minQTopicId + " with jsDiv: "
							+ minJsDiv);
			minJsDivs[pTopicId] = minJsDiv;
		}
		DescriptiveStatistics stats = new DescriptiveStatistics(minJsDivs);
		System.out.println(stats);

	}

	public static Map<Integer, Topic> getWordTopicDistributions(
			ParallelTopicModel model) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		try {

			Map<Integer, Topic> topics = new HashMap<Integer, Topic>();

			for (int topicIdx = 0; topicIdx < model.getNumTopics(); topicIdx++) {
				Topic topic = new Topic();
				topic.id = topicIdx;
				topics.put(topicIdx, topic);
			}
			model.printTopicWordWeights(writer);
			// System.out.println(stringWriter.toString());

			BufferedReader bufReader = new BufferedReader(new StringReader(
					stringWriter.toString()));
			String line = null;
			while ((line = bufReader.readLine()) != null) {
				// System.out.println(line);
				String[] tokens = line.trim().split("\\s");
				int topicNum = Integer.parseInt(tokens[0]);
				String word = tokens[1].trim();
				double weight = Double.parseDouble(tokens[2].trim());
				if (weight > model.beta) {
					System.out.println(topicNum + " " + word + " " + weight);
				}

				Topic currTopic = topics.get(topicNum);
				currTopic.wordWeights.put(word, weight);
			}

			for (Topic topic : topics.values()) {
				double weightSum = 0;
				for (String word : topic.wordWeights.keySet()) {
					double weight = topic.wordWeights.get(word);
					weightSum += weight;
				}

				for (String word : topic.wordWeights.keySet()) {
					double weight = topic.wordWeights.get(word);
					double normWeight = weight / weightSum;
					topic.minNormWeight = model.beta / weightSum;
					topic.wordNormWeights.put(word, normWeight);
				}
			}

			System.out.println("-- normalized word weights per topic --");
			for (Topic topic : topics.values()) {
				for (String word : topic.wordWeights.keySet()) {
					double normWeight = topic.wordNormWeights.get(word);
					if (normWeight > topic.minNormWeight) {
						System.out.println(topic.id + " " + word + " "
								+ normWeight);
					}
				}
			}
			return topics;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static ParallelTopicModel getModel(String src,
			String topicModelFilename) {
		ParallelTopicModel model = null;
		File topicModelFile = new File(topicModelFilename);
		if (topicModelFile.exists()) {
			try {
				model = ParallelTopicModel.read(topicModelFile);
				return model;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void compareDocTopics(String[] args, String src, int topics)
			throws FileNotFoundException, IOException {
		ArrayList<DocTopicItem> pDocTopicItems = DocTopics(src, topics, args[0]);
		ArrayList<DocTopicItem> qDocTopicItems = DocTopics(src, topics, args[1]);

		System.out.println(pDocTopicItems.size());
		System.out.println(qDocTopicItems.size());

		for (DocTopicItem pDocTopicItem : pDocTopicItems) {
			double minD = 0;
			DocTopicItem minQDocTopicItem = new DocTopicItem();
			for (DocTopicItem qDocTopicItem : qDocTopicItems) {
				// for (int i = 0; i < topics; i++) {
				// DocTopicItem pDocTopicItem = pDocTopicItems.get(i);
				// DocTopicItem qDocTopicItem = qDocTopicItems.get(i);

				if (pDocTopicItem.topics.size() != qDocTopicItem.topics.size()) {
					// logger.error("P size: " + pDocTopicItem.topics.size());
					// logger.error("Q size: " + qDocTopicItem.topics.size());
					// logger.error("P and Q for Jensen Shannon Divergence not the same size...exiting");
					// System.exit(0);
					System.out.println("not the same size");
				}

				double[] sortedP = new double[pDocTopicItem.topics.size()];
				double[] sortedQ = new double[qDocTopicItem.topics.size()];

				for (TopicItem pTopicItem : pDocTopicItem.topics) {
					sortedP[pTopicItem.topicNum] = pTopicItem.proportion;
				}

				for (TopicItem qTopicItem : qDocTopicItem.topics) {
					sortedQ[qTopicItem.topicNum] = qTopicItem.proportion;
				}

				// divergence = jsDivergence(sortedP, sortedQ);
				double divergence = Maths.jensenShannonDivergence(sortedP,
						sortedQ);
				if (divergence < minD) {
					minD = divergence;
					minQDocTopicItem = qDocTopicItem;
				}
				// System.out.println("minD: " + minD);
				List<Double> listP = Arrays
						.asList(ArrayUtils.toObject(sortedP));
				List<Double> listQ = Arrays
						.asList(ArrayUtils.toObject(sortedQ));
				// System.out.println("p: " + printList(listP));
				// System.out.println("q: " + printList(listQ));
				// divergence = 0;
			}
			System.out.println(minD + " for " + pDocTopicItem.source + " and "
					+ minQDocTopicItem.source);
		}
	}

	private static <T> String printList(List<T> list) {
		String buffer = "";
		for (T t : list) {
			buffer += t + ",";
		}
		return buffer;
	}

	private static ArrayList<DocTopicItem> getTopicModel(String malletFile)
			throws IOException {
		ArrayList<DocTopicItem> dtItemList = new ArrayList<DocTopicItem>();
		int numTopics = 175;
		String topicModelFilename = malletFile;
		// String topicModelFilename_new = args[1];

		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: alphanumeric only, camel case separation, lowercase, tokenize,
		// remove stopwords english, remove stopwords java, stem, map to
		// features
		pipeList.add(new CharSequenceReplace(Pattern.compile("[^A-Za-z]"), " "));
		pipeList.add(new CamelCaseSeparatorPipe());
		pipeList.add(new CharSequenceLowercase());
		pipeList.add(new CharSequence2TokenSequence(Pattern
				.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add(new TokenSequenceRemoveStopwords(new File(
				"stoplists/en.txt"), "UTF-8", false, false, false));

		if (Config.getSelectedLanguage().equals(Config.Language.c)) {
			pipeList.add(new TokenSequenceRemoveStopwords(new File(
					"res/ckeywords"), "UTF-8", false, false, false));
			pipeList.add(new TokenSequenceRemoveStopwords(new File(
					"res/cppkeywords"), "UTF-8", false, false, false));
		} else {
			pipeList.add(new TokenSequenceRemoveStopwords(new File(
					"res/javakeywords"), "UTF-8", false, false, false));
		}
		pipeList.add(new StemmerPipe());
		pipeList.add(new TokenSequence2FeatureSequence());
		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		// Read the input mallet files
		double alpha = (double) 50 / (double) numTopics;
		double beta = .01;
		ParallelTopicModel model = null;
		File topicModelFile = new File(topicModelFilename);
		if (topicModelFile.exists()) {
			try {
				model = ParallelTopicModel.read(topicModelFile);
				// instances = model.
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			model = new ParallelTopicModel(numTopics, alpha, beta);
			model.addInstances(instances);

			// Use two parallel samplers, which each look at one half the corpus
			// and
			// combine
			// statistics after every iteration.
			model.setNumThreads(4);

			// Run the model for 50 iterations and stop (this is for testing
			// only,
			// for real applications, use 1000 to 2000 iterations)
			int numIterations = 1000;
			model.setNumIterations(numIterations);
			model.estimate();
			model.write(topicModelFile);
		}

		for (int instIndex = 0; instIndex < instances.size(); instIndex++) {
			DocTopicItem dtItem = new DocTopicItem();
			dtItem.doc = instIndex;
			dtItem.source = (String) instances.get(instIndex).getName();

			dtItem.topics = new ArrayList<TopicItem>();

			double[] topicDistribution = model.getTopicProbabilities(instIndex);
			for (int topicIdx = 0; topicIdx < numTopics; topicIdx++) {
				TopicItem t = new TopicItem();
				t.topicNum = topicIdx;
				t.proportion = topicDistribution[topicIdx];
				dtItem.topics.add(t);
			}
			dtItemList.add(dtItem);

		}
		return dtItemList;
	}

	public static ArrayList<DocTopicItem> DocTopics(String srcDir,
			int numTopics, String topicModelFilename)
			throws FileNotFoundException, IOException {
		// Begin by importing documents from text to feature sequences
		ArrayList<DocTopicItem> dtItemList = new ArrayList<DocTopicItem>();
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: alphanumeric only, camel case separation, lowercase, tokenize,
		// remove stopwords english, remove stopwords java, stem, map to
		// features
		pipeList.add(new CharSequenceReplace(Pattern.compile("[^A-Za-z]"), " "));
		pipeList.add(new CamelCaseSeparatorPipe());
		pipeList.add(new CharSequenceLowercase());
		pipeList.add(new CharSequence2TokenSequence(Pattern
				.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add(new TokenSequenceRemoveStopwords(new File(
				"stoplists/en.txt"), "UTF-8", false, false, false));

		if (Config.getSelectedLanguage().equals(Config.Language.c)) {
			pipeList.add(new TokenSequenceRemoveStopwords(new File(
					"res/ckeywords"), "UTF-8", false, false, false));
			pipeList.add(new TokenSequenceRemoveStopwords(new File(
					"res/cppkeywords"), "UTF-8", false, false, false));
		} else {
			pipeList.add(new TokenSequenceRemoveStopwords(new File(
					"res/javakeywords"), "UTF-8", false, false, false));
		}
		pipeList.add(new StemmerPipe());
		pipeList.add(new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		String testDir = srcDir;
		for (File file : FileListing.getFileListing(new File(testDir))) {
			System.out.println(file.getName());
			if (file.isFile() && file.getName().endsWith(".java")) {
				String shortClassName = file.getName().replace(".java", "");
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = null;
				String fullClassName = "";
				while ((line = reader.readLine()) != null) {
					String packageName = FileUtil.findPackageName(line);
					if (packageName != null) {
						fullClassName = packageName + "." + shortClassName;
						System.out.println("\t" + fullClassName);
					}
				}
				reader.close();
				String data = FileUtil.readFile(file,
						Charset.defaultCharset());
				Instance instance = new Instance(data, "X", fullClassName,
						file.getAbsolutePath());
				instances.addThruPipe(instance);
			}
			Pattern p = Pattern.compile("\\.(c|cpp|cc|s|h|hpp|icc|ia|tbl|p)$");
			// if we found a c or c++ file
			if (p.matcher(file.getName()).find()) {
				// logger.debug("Current file in DocTopics: " + file);
				String depsStyleFilename = file.getAbsolutePath().replace(
						testDir, "");
				String data = FileUtil.readFile(file,
						Charset.defaultCharset());
				Instance instance = new Instance(data, "X", depsStyleFilename,
						file.getAbsolutePath());
				instances.addThruPipe(instance);
			}
		}

		/*
		 * Reader fileReader = new InputStreamReader(new FileInputStream(new
		 * File( args[0])), "UTF-8"); instances.addThruPipe(new
		 * CsvIterator(fileReader, Pattern
		 * .compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1)); // data, //
		 * label, // name // fields
		 */
		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		// Note that the first parameter is passed as the sum over topics, while
		// the second is
		// int numTopics = 40;
		double alpha = (double) 50 / (double) numTopics;
		double beta = .01;
		ParallelTopicModel model = null;
		File topicModelFile = new File(topicModelFilename);
		if (topicModelFile.exists()) {
			try {
				model = ParallelTopicModel.read(topicModelFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			model = new ParallelTopicModel(numTopics, alpha, beta);
			model.addInstances(instances);

			// Use two parallel samplers, which each look at one half the corpus
			// and
			// combine
			// statistics after every iteration.
			model.setNumThreads(4);

			// Run the model for 50 iterations and stop (this is for testing
			// only,
			// for real applications, use 1000 to 2000 iterations)
			int numIterations = 1000;
			model.setNumIterations(numIterations);
			model.estimate();
			model.write(topicModelFile);
		}

		for (int instIndex = 0; instIndex < instances.size(); instIndex++) {
			DocTopicItem dtItem = new DocTopicItem();
			dtItem.doc = instIndex;
			dtItem.source = (String) instances.get(instIndex).getName();

			dtItem.topics = new ArrayList<TopicItem>();

			double[] topicDistribution = model.getTopicProbabilities(instIndex);
			for (int topicIdx = 0; topicIdx < numTopics; topicIdx++) {
				TopicItem t = new TopicItem();
				t.topicNum = topicIdx;
				t.proportion = topicDistribution[topicIdx];
				dtItem.topics.add(t);
			}
			dtItemList.add(dtItem);
		}
		return dtItemList;

	}
}
