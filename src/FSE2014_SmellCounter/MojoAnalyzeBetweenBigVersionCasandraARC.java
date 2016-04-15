package FSE2014_SmellCounter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class MojoAnalyzeBetweenBigVersionCasandraARC {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String sourceFile = "G:\\JackRabbit_Analysis_Full_Result\\MojoEvoAnalyzer_Acdc.txt";
//		String sourceFile = "G:\\JackRabbit_Analysis_Full_Result\\MojoEvoAnalyzer_Arc.txt";
//		String sourceFile = "G:\\Struts_Analyze_result\\Acdc_MojoFM.txt";
//		String sourceFile = "G:\\Struts_Analyze_result\\Arc_MojoFM.txt";
		String sourceFile = "G:\\hadoop_cassandra\\cassandra\\acdc\\arc_mojofm.log";
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
					String[] temp = sCurrentLine.split("_")[0].split("-");
					String[] fromVersion = temp[1].split("\\.");
					temp = sCurrentLine.split("_")[5].split("-");
					String[] toVersion = temp[1].split("\\.");
					
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
