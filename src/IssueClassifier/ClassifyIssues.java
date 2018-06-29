package IssueClassifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import cc.mallet.classify.Classifier;
import cc.mallet.types.Labeling;
import edu.usc.softarch.arcade.relax.ClassifierUtils;

public class ClassifyIssues {
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Classifier classifier = ClassifierUtils.loadClassifier(new File("classifiers/relax.classifier"));
		
		File mainDir = new File("E:\\Android_Framework_data\\issue_content");
		
		String output = "issue_id, graphics , gui, io , networking , sound , sql \n";
		
		String[] labels = {"graphics","gui","io","networking", "sound", "sql"};
		for (File file : mainDir.listFiles()){
			if (file.isFile()){
				final Labeling labeling = classifier.classify(file).getLabeling();
				System.out.print(file.getName() +",");
//				for (int rank = 0; rank < labeling.numLocations(); rank++) {
//					System.out.print(labeling.getLabelAtRank(rank) + ":" + labeling.getValueAtRank(rank) + " ");
//				}
//				System.out.print("\n");
				output += file.getName().replace(".txt", "");
				for (int rank = 0; rank < labeling.numLocations(); rank++) {
					System.out.print(labels[rank] + ":" + labeling.valueAtLocation(rank) + " ");
					output += "," + labeling.valueAtLocation(rank);
				}
				output += "\n";
				System.out.print("\n");
			}
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("Issue_classified.csv"));
	    writer.append(output);	     
	    writer.close();
	}
}
