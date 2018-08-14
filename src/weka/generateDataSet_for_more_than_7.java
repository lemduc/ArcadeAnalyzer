package weka;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import weka.classifiers.Evaluation;
import weka.classifiers.rules.DecisionTable;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class generateDataSet_for_more_than_7 {

	
	private static final int skipLine= 11; //14 for ARC, 11 for ACDC and PKG
	private static final String view = "data/ACDC_Model_Combination/";
//	private static final String view = "data/PKG_Model_Combination/";
//	private static final String view = "data/ARC_Model_Combination/";

//	private static final String DATA_MODELS = view + "models/";
	private static String DATA_MODELS = view + "resized_9/";
	private static int TOTAL_SYSTEMS = 9;
	private static int SUB_SET= 7;
//	private static final String DATA_MODELS = view + "enlarge/";


	public static void main(String[] args) throws Exception {
		File folder = new File(DATA_MODELS);
		File[] listOfFiles = folder.listFiles();
		
		// update total # of systems
		TOTAL_SYSTEMS = listOfFiles.length;

		createCompostions compo = new createCompostions();
		compo.TOTAL_SYSTEMS = TOTAL_SYSTEMS;
		compo.SUB_SET		= SUB_SET;
		compo.main(args);
		List<HashSet<Integer>> sets = compo.output;
		
		System.out.println("System,Precision_mean, Precision_std, Recall_mean, Recall_std");
		for (int i = 0; i < TOTAL_SYSTEMS; i++){
			System.out.print(listOfFiles[i].getName() + ",");
			// Get a DescriptiveStatistics instance
			DescriptiveStatistics p_stats = new DescriptiveStatistics();
			DescriptiveStatistics r_stats = new DescriptiveStatistics();

			for (HashSet<Integer> set : sets){
				if (set.contains(i))
					continue;

				Instances trainSet = null; 
				Instances testSet1 = null; 
				StringBuffer trainData = null;
				for (int k : set ){
					FileReader reader = new FileReader(DATA_MODELS + listOfFiles[k].getName());
					BufferedReader bufferedReader = new BufferedReader(reader);
					
					if (trainData == null){
						trainData = new StringBuffer();
						String line;
						while ((line = bufferedReader.readLine()) != null){
							trainData.append(line + "\n");
						}
						bufferedReader.close();
					} else {
						//skip first 11 lines for ACDC and PKG, 14 lines for ARC
						String line;
						for (int c = 0; c < skipLine; c++)
							line = bufferedReader.readLine();
						while ((line = bufferedReader.readLine()) != null){
							trainData.append(line + "\n");
						}
						bufferedReader.close();
					}
				}
				
				FileReader reader = new FileReader(view + "model_2k_balanced_new.arff");
				BufferedReader bufferedReader = new BufferedReader(reader);
				String line;
				for (int c = 0; c < skipLine; c++)
					line = bufferedReader.readLine();
				while ((line = bufferedReader.readLine()) != null){
					trainData.append(line + "\n");
				}
				bufferedReader.close();
				
				// write to arff file
				FileWriter writer = new FileWriter("data/gen/temp.arff");
	            BufferedWriter bufferedWriter = new BufferedWriter(writer);
				
	            bufferedWriter.write(trainData.toString());
	            bufferedWriter.close();
	            
	            testSet1 = retriveData(listOfFiles, i);
	            
	            DataSource source = new DataSource("data/gen/temp.arff");
				trainSet =  source.getDataSet();
				if (trainSet.classIndex() == -1)
					trainSet.setClassIndex(0);
					 
				//Test a model
//				J48 cls = new J48();
				DecisionTable cls = new DecisionTable();
				cls.buildClassifier(trainSet);
				Evaluation eval1 = new Evaluation(testSet1);
				eval1.evaluateModel(cls, testSet1);
//				Evaluation eval2 = new Evaluation(trainSet);
//				eval2.evaluateModel(cls, testSet2);
				
				
//				System.out.println(eval1.weightedPrecision() + "," + eval1.weightedRecall());
				p_stats.addValue(eval1.weightedPrecision());
				r_stats.addValue(eval1.weightedRecall());
//				System.out.println(eval1.toSummaryString("\nResults\n======\n", false));
//				System.out.println(eval2.toSummaryString("\nResults\n======\n", false));
//				break;
//				}
			}
			System.out.println(p_stats.getMean() + ", " + p_stats.getStandardDeviation() + ", " + r_stats.getMean() + ", " +r_stats.getStandardDeviation() + "," + p_stats.getValues().length);
			}

	}


	private static Instances retriveData(File[] listOfFiles, int i) throws Exception {
		DataSource source = new DataSource(DATA_MODELS + listOfFiles[i].getName());
		Instances data = source.getDataSet();
		if (data.classIndex() == -1)
			data.setClassIndex(0);
		return data;
	}
	
}
