package malletComparison;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.AbstractList;
import java.util.ArrayList;
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

public class Comparison {

	public static void main(String[] args) throws IOException {
		String src = args[2];
		int topics = 175;
		ArrayList<DocTopicItem> pDocTopicItems = DocTopics(src, topics, args[0]);
		ArrayList<DocTopicItem> qDocTopicItems = DocTopics(src, topics, args[1]);
		
		System.out.println(pDocTopicItems.size());
		System.out.println(qDocTopicItems.size());

		for (DocTopicItem pDocTopicItem: pDocTopicItems){
			double minD = 1;
			for (DocTopicItem qDocTopicItem: qDocTopicItems){
//		for (int i = 0; i < topics; i++) {
//			DocTopicItem pDocTopicItem = pDocTopicItems.get(i);
//			DocTopicItem qDocTopicItem = qDocTopicItems.get(i);

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
			double divergence = Maths.jensenShannonDivergence(sortedP, sortedQ);
			if (divergence<minD)
				minD = divergence;
			// divergence = 0;
			}
			System.out.println(minD);
		}
	}

	private static ArrayList<DocTopicItem> getTopicModel(String malletFile) throws IOException {
		ArrayList<DocTopicItem> dtItemList = new ArrayList<DocTopicItem>();
		int numTopics = 175;
		String topicModelFilename = malletFile;
//		String topicModelFilename_new = args[1];
		
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
		}
		else {
			pipeList.add(new TokenSequenceRemoveStopwords(new File(
					"res/javakeywords"), "UTF-8", false, false, false));
		}
		pipeList.add(new StemmerPipe());
		pipeList.add(new TokenSequence2FeatureSequence());
		InstanceList instances = new InstanceList(new SerialPipes(pipeList));
		
		// Read the input mallet files
		double alpha = (double) 50 / (double) numTopics;
		double beta = .01;
		ParallelTopicModel model = null ;
		File topicModelFile = new File(topicModelFilename);
		if (topicModelFile.exists()) {
			try {
				model = ParallelTopicModel.read(topicModelFile);
//				instances = model.
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			model = new ParallelTopicModel(numTopics, alpha, beta);
			model.addInstances(instances);

			// Use two parallel samplers, which each look at one half the corpus and
			// combine
			// statistics after every iteration.
			model.setNumThreads(4);

			// Run the model for 50 iterations and stop (this is for testing only,
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

	public static ArrayList<DocTopicItem> DocTopics(String srcDir, int numTopics, String topicModelFilename) throws FileNotFoundException, IOException {
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
		}
		else {
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
				String data = FileUtil.readFile(file.getAbsolutePath(),
						Charset.defaultCharset());
				Instance instance = new Instance(data, "X", fullClassName,
						file.getAbsolutePath());
				instances.addThruPipe(instance);
			}
			Pattern p = Pattern.compile("\\.(c|cpp|cc|s|h|hpp|icc|ia|tbl|p)$");
			// if we found a c or c++ file
			if ( p.matcher(file.getName()).find() ) {
//				logger.debug("Current file in DocTopics: " + file);
				String depsStyleFilename = file.getAbsolutePath().replace(testDir, "");
				String data = FileUtil.readFile(file.getAbsolutePath(),
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
		//int numTopics = 40;
		double alpha = (double) 50 / (double) numTopics;
		double beta = .01;
		ParallelTopicModel model = null ;
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

			// Use two parallel samplers, which each look at one half the corpus and
			// combine
			// statistics after every iteration.
			model.setNumThreads(4);

			// Run the model for 50 iterations and stop (this is for testing only,
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
