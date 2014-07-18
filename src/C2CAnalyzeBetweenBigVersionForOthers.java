import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class C2CAnalyzeBetweenBigVersionForOthers {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String sourceFile = "G:\\JackRabbit_Analysis_Full_Result\\c2c_jackrabbit_acdc.log";
//		String sourceFile = "G:\\JackRabbit_Analysis_Full_Result\\c2c_jackrabbit_arc.log";
//		String sourceFile = "G:\\Struts_Analyze_result\\c2c_struts_acdc.log";
//		String sourceFile = "G:\\Struts_Analyze_result\\c2c_struts_arc.log";
//		String sourceFile = "G:\\Lucene\\Results\\ARC\\ARC C2C\\arc.txt";
//		String sourceFile = "G:\\XERCES\\ACDC\\ACDC_C2C\\xerces_acdc.txt";
//		String sourceFile = "G:\\XERCES\\ARC\\ARC_C2C\\xerces_arc.txt";
//		String sourceFile = "G:\\metrics.zipEder\\c2c clustering\\pdfbox c2c acdc.txt";
//		String sourceFile = "G:\\ivy-AWSD_SEA-C2C.out";
//		String sourceFile = "C:\\Users\\Duc Le\\Desktop\\Dropbox\\evolution\\subject_system\\cassandra\\arc\\simevol.log";
//		String sourceFile = "C:\\Users\\Duc Le\\Desktop\\Dropbox\\evolution\\subject_system\\chukwa\\arc\\chukwa_rel_c2c_arc.log";
//		String sourceFile = "C:\\Users\\Duc Le\\Desktop\\Dropbox\\evolution\\subject_system\\hadoop\\acdc\\simevol.log";
		String sourceFile = "G:\\activemq\\acdc\\simevol.log";
		String targetFile = "";
		try (BufferedReader br = new BufferedReader(new FileReader(sourceFile)))
		{
 
			String sCurrentLine;
			float MajortotalST = 0;
			float MajortotalTS = 0;
			int Majorcount = 0;
			float MinortotalST = 0;
			float MinortotalTS = 0;
			int Minorcount = 0;
			float PatchtotalST = 0;
			float PatchtotalTS = 0;
			int Patchcount = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.equals("")){
					sCurrentLine = br.readLine();
					sCurrentLine = br.readLine();
					if(sCurrentLine.equals("")){
						System.out.println("Avg major source to target = " + MajortotalST/Majorcount);
						System.out.println("Avg major target to source = " + MajortotalTS/Majorcount);
						
				
						System.out.println("Avg minor source to target = " + MinortotalST/Minorcount );
						System.out.println("Avg minor target to source= " + MinortotalTS/Minorcount );
						
						System.out.println("Avg patch source to target = " + PatchtotalST/Patchcount );
						System.out.println("Avg patch target to source= " + PatchtotalTS/Patchcount );
						
						break;
					} else
					{
						System.out.println(sCurrentLine);
						String[] fromVersion = sCurrentLine.split(" ")[7].split("\\.");
						String[] toVersion = sCurrentLine.split(" ")[9].split("\\.");
						//get metric from source to target
						float metricST = Float.parseFloat(sCurrentLine.split(":")[1].replaceAll(" ", ""));
						//get metric from target to source
						sCurrentLine = br.readLine();
						float metricTS = Float.parseFloat(sCurrentLine.split(":")[1].replaceAll(" ", ""));
						
						//
						if (!fromVersion[0].equals(toVersion [0])){
							MajortotalST += metricST;
							MajortotalTS += metricTS;
							Majorcount += 1;
						} else
						if (!fromVersion[1].equals(toVersion [1])){
							MinortotalST += metricST;
							MinortotalTS += metricTS;
							Minorcount += 1;
						}
						else {
							PatchtotalST += metricST;
							PatchtotalTS += metricTS;
							Patchcount += 1;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
