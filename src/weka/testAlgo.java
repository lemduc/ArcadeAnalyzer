package weka;

import weka.classifiers.Evaluation;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class testAlgo {
	
	public static String filename = String.valueOf(42);
	public static String system = "6.hadoop_acdc";

	public static void main(String[] args) throws Exception {
		DataSource source = new DataSource("data/gen/" + filename + ".arff");
		Instances trainSet =  source.getDataSet();
		if (trainSet.classIndex() == -1)
			trainSet.setClassIndex(0);
			 
		DataSource source2 = new DataSource("data/models/" + system + ".arff");
		Instances testSet1 =  source2.getDataSet();
		if (testSet1.classIndex() == -1)
			testSet1.setClassIndex(0);
			 
		
		//Test a model
		J48 cls = new J48();
//		DecisionTable cls = new DecisionTable();
		
		String[] options = {"-C", "0.25", "–M", "2"};
		cls.setOptions(options);
		
		cls.buildClassifier(trainSet);
		Evaluation eval1 = new Evaluation(testSet1);
		eval1.evaluateModel(cls, testSet1);
//		Evaluation eval2 = new Evaluation(trainSet);
//		eval2.evaluateModel(cls, testSet2);
		
		
		System.out.println(eval1.weightedPrecision() + "," + eval1.weightedRecall());
	}

}
