import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class MojoAnalyzeBetweenBigVersionjspwikiACDC {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String sourceFile = "G:\\JackRabbit_Analysis_Full_Result\\MojoEvoAnalyzer_Acdc.txt";
//		String sourceFile = "G:\\JackRabbit_Analysis_Full_Result\\MojoEvoAnalyzer_Arc.txt";
//		String sourceFile = "G:\\Struts_Analyze_result\\Acdc_MojoFM.txt";
//		String sourceFile = "G:\\Struts_Analyze_result\\Arc_MojoFM.txt";
//		String sourceFile = "G:\\metrics.zipEder\\mojo distance\\mojo distance jspwiki acdc.rtf";
//		String sourceFile = "G:\\metrics.zipEder\\mojo distance\\mojo distance pdfbox acdc.rtf";
//		String sourceFile = "G:\\metrics.zipEder\\mojo distance\\mojo distance jspwiki arc.rtf";
//		String sourceFile = "G:\\Lucene\\Results\\ACDC\\ACDC Mojo\\output.txt";
//		String sourceFile = "G:\\XERCES\\ACDC\\ACDC_MOJO\\Mojo.txt";
//		String sourceFile = "G:\\XERCES\\ARC\\ARC_MOJO\\Mojo.txt";
//		String sourceFile = "G:\\Lucene\\Results\\ARC\\ARC Mojo\\mojo.txt";
//		String sourceFile = "G:\\ivy-AWSD_SEA-MEA.out";
//		String sourceFile = "G:\\ivy-BCE-MEA.out";
//		String sourceFile = "C:\\Users\\Duc Le\\Desktop\\Dropbox\\evolution\\subject_system\\jena\\acdc\\mojofm_acd_rel.log";
//		String sourceFile = "G:\\mojo distance pdfbox arc.txt";
		String sourceFile = "G:\\activemq\\arc\\mojoevol.log";
		String targetFile = "";
		try (BufferedReader br = new BufferedReader(new FileReader(sourceFile)))
		{
 
			String sCurrentLine;
			float Majoravg = 0;
			int Majorcount = 0;
			float Majortotal = 0;
			float Minoravg = 0;
			int Minorcount = 0;
			float Minortotal = 0;
			float Patchavg = 0;
			int Patchcount = 0;
			float Patchtotal = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.contains("compareTo")){
					continue;
				}
				if(sCurrentLine.contains("distance is: 1")){
					continue;
				}
				if(sCurrentLine.contains("MoJoFM from")){
					float metric = Float.parseFloat(sCurrentLine.split(":")[1].replaceAll(" ", ""));
					System.out.println(sCurrentLine);
					String[] temp = sCurrentLine.split("_| |mq-");
					String[] fromVersion = temp[3].split("\\.");
					String[] toVersion = temp[11].split("\\.|-");
					
					if (!fromVersion[0].equals(toVersion [0])){
						Majortotal += metric;
						Majorcount += 1;
					} else
					if (!fromVersion[1].equals(toVersion [1])){
						Minortotal += metric;
						Minorcount += 1;
					}
					else {
						Patchtotal += metric;
						Patchcount += 1;
					}
				}
				if(sCurrentLine.equals("")){
					System.out.println("Avg major = " + Majortotal/Majorcount );
					System.out.println("Avg minor = " + Minortotal/Minorcount );
					System.out.println("Avg patch = " + Patchtotal/Patchcount );
					break;
				}
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
