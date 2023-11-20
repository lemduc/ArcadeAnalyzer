import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class MojoDistanceMinimumWithinOneMajor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String sourceFile = "G:\\JackRabbit_Analysis_Full_Result\\MojoEvoAnalyzer_Acdc.txt";
//		String sourceFile = "G:\\JackRabbit_Analysis_Full_Result\\MojoEvoAnalyzer_Arc.txt";
//		String sourceFile = "G:\\Struts_Analyze_result\\Acdc_MojoFM.txt";
//		String sourceFile = "G:\\Struts_Analyze_result\\Arc_MojoFM.txt";
//		String sourceFile = "G:\\metrics.zipEder\\mojo distance\\mojo distance jspwiki acdc.rtf";
//		String sourceFile = "G:\\metrics.zipEder\\mojo distance\\mojo distance pdfbox acdc.rtf";
//		String sourceFile = "G:\\metrics.zipEder\\mojo distance\\mojo distance jspwiki arc.rtf";
//		String sourceFile = "G:\\Lucene\\Results\\AC4DC\\ACDC Mojo\\output.txt";
//		String sourceFile = "G:\\XERCES\\ACDC\\ACDC_MOJO\\Mojo.txt";
//		String sourceFile = "G:\\XERCES\\ARC\\ARC_MOJO\\Mojo.txt";
//		String sourceFile = "G:\\Lucene\\Results\\ARC\\ARC Mojo\\mojo.txt";
//		String sourceFile = "G:\\Lucene\\Results\\ACDC\\ACDC Mojo\\output.txt";
//		String sourceFile = "G:\\hadoop_cassandra\\cassandra\\acdc\\acdc_mojofm.log";
//		String sourceFile = "G:\\hadoop_cassandra\\cassandra\\acdc\\arc_mojofm.log";
//		String sourceFile = "G:\\hadoop_cassandra\\hadoop_svn\\acdc\\mojoevol_acdc_hadoop.log";
//		String sourceFile = "G:\\hadoop_cassandra\\hadoop_svn\\arc\\mojoevol_arc_hadoop.log";
		String sourceFile = "G:\\ivy-AWSD_SEA-MEA.out";
//		String sourceFile = "G:\\ivy-BCE-MEA.out";
//		String sourceFile = "C:\\Users\\Duc Le\\Desktop\\Dropbox\\evolution\\subject_system\\jena\\acdc\\mojofm_acd_rel.log";
//		String sourceFile = "C:\\Users\\Duc Le\\Desktop\\Dropbox\\evolution\\subject_system\\jena\\arc\\mojofm_arc_rel.log";
//		String sourceFile = "C:\\Users\\Duc Le\\Desktop\\Dropbox\\evolution\\subject_system\\chukwa\\arc\\chukwa_rel_mojarc.log";
//		String sourceFile = "C:\\Users\\Duc Le\\Desktop\\Dropbox\\evolution\\subject_system\\chukwa\\acdc\\chukwa_rel_mojacd.log";
//		String sourceFile = "G:\\mojo distance pdfbox acdc.txt";
//		String sourceFile = "G:\\activemq\\acdc\\mojoevol.log";
		String targetFile = "";
		try (BufferedReader br = new BufferedReader(new FileReader(sourceFile)))
		{
 
			String sCurrentLine;
			float threshold = 100;
			int distance = 0;
			int count = 0;
			float avg = 0;
			float total = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.contains("compareTo"))
					continue;
				if(sCurrentLine.contains("distance")){
					if (count != 0){
						avg = total/count;
//						System.out.println("Count :" + count);
						System.out.println("Distance "+distance+" has average: " + avg);
					}
					distance = Integer.parseInt(sCurrentLine.split(":")[1].replace(" ", ""));
					count = 0;
					total = 0;
				}
				if(sCurrentLine.contains("MoJoFM from")){
					System.out.println(sCurrentLine);
					float mojo = Float.parseFloat(sCurrentLine.split(":")[1]);
					String[] temp = sCurrentLine.split("_| |-");
					String[] fromVersion = temp[2].split("\\.");
					String[] toVersion = temp[7].split("\\.|-");
					
					
					if (fromVersion[0].equals(toVersion [0])){
						if (mojo < threshold )
						{
							threshold = mojo;
							System.out.println(fromVersion[0] +"->"+toVersion[0]+"="+mojo);
						}
					} 
				}
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}