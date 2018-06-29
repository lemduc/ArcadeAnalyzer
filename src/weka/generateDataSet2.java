package weka;
import java.io.File;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.rules.DecisionTable;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class generateDataSet2 {
	
//	private static final String view = "data/ACDC_Model_Combination/";
//	private static final String view = "data/PKG_Model_Combination/";
	private static final String view = "data/ARC_Model_Combination/";

	private static final String DATA_MODELS = view + "models/";
//	private static final String DATA_MODELS = view + "resized/";
//	private static final String DATA_MODELS = view + "enlarge/";

	public static void main(String[] args) throws Exception {
		File folder = new File(DATA_MODELS);
		File[] listOfFiles = folder.listFiles();

		System.out.println("System,Precision,Recall");
		for (int i = 0; i < 7; i++){
			System.out.print(listOfFiles[i].getName() + ",");      
            Instances testSet1 = retriveData(listOfFiles, i);
			if (testSet1.classIndex() == -1)
				testSet1.setClassIndex(0);
				 
			//Test a model
//			J48 cls = new J48();
			DecisionTable cls = new DecisionTable();
			Evaluation eval1 = new Evaluation(testSet1);
			eval1.crossValidateModel(cls, testSet1, 10, new Random(1));
			
			System.out.println(eval1.weightedPrecision() + "," + eval1.weightedRecall());
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
