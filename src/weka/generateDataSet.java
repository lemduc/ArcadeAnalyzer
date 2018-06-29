package weka;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import weka.classifiers.Evaluation;
import weka.classifiers.rules.DecisionTable;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class generateDataSet {

	
	private static final int skipLine= 14; //14 for ARC, 11 for ACDC and PKG
//	private static final String view = "data/ACDC_Model_Combination/";
//	private static final String view = "data/PKG_Model_Combination/";
	private static final String view = "data/ARC_Model_Combination/";

//	private static final String DATA_MODELS = view + "models/";
	private static final String DATA_MODELS = view + "resized/";
//	private static final String DATA_MODELS = view + "enlarge/";


	public static void main(String[] args) throws Exception {
		File folder = new File(DATA_MODELS);
		File[] listOfFiles = folder.listFiles();

//	    for (int i = 0; i < listOfFiles.length; i++) {
//	      if (listOfFiles[i].isFile()) {
//	        System.out.println("File " + listOfFiles[i].getName());
//	      } else if (listOfFiles[i].isDirectory()) {
//	        System.out.println("Directory " + listOfFiles[i].getName());
//	      }
//	    }
		    
		// Randomly select two files as  test Data
//	    Random rand = new Random();
//		int n1 = rand.nextInt(6);
//		int n2 = n1;
//		while (n2 == n1)
//			n2 = rand.nextInt(6);
		
		System.out.println("System,Precision_mean, Precision_std, Recall_mean, Recall_std");
		for (int i = 0; i < 7; i++){
			System.out.print(listOfFiles[i].getName() + ",");
			// Get a DescriptiveStatistics instance
			DescriptiveStatistics p_stats = new DescriptiveStatistics();
			DescriptiveStatistics r_stats = new DescriptiveStatistics();

			for (int j = 0; j < 7; j++){
//				if (i==j)
//					continue;
				int filename = i*7+j;
				Instances trainSet = null; 
				Instances testSet1 = null; 
				StringBuffer trainData = null;
//				for (int n = j+1; n < 7; n++){
//					if (i==n)
//						continue;
//					if (j==n)
//						continue;
					for (int k = 0; k < 7; k++){
	
//						if (k==i)
//							continue;
//						else if (k==j)
//							continue;
//						else if (k==n)
//							continue;
	//					// merge file		
	//					else {
	//						System.out.print(k + " ");
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
	//				}
				
				FileReader reader = new FileReader(view + "model_2k_balanced.arff");
				BufferedReader bufferedReader = new BufferedReader(reader);
				String line;
				for (int c = 0; c < skipLine; c++)
					line = bufferedReader.readLine();
				while ((line = bufferedReader.readLine()) != null){
					trainData.append(line + "\n");
				}
				bufferedReader.close();
				
				// write to arff file
				FileWriter writer = new FileWriter("data/gen/" + filename + ".arff");
	            BufferedWriter bufferedWriter = new BufferedWriter(writer);
				
	            bufferedWriter.write(trainData.toString());
	            bufferedWriter.close();
	            
	            testSet1 = retriveData(listOfFiles, i);
	            
	            DataSource source = new DataSource("data/gen/" + filename + ".arff");
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
//			break;
			// print 
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
