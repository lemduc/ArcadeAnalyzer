package ArchSmellAnalyzer.JsonAnalyze;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RQ6_CountFileSizeRelatedToSmellyFiles {
	
	/*
	 * Detect if one file has smell, then keep track the number of its related commits
	 * 
	 * 
	 */
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException{

// 		Camel
//		String mainFolder = "E:\\camel_data\\";
//		String issue_json = mainFolder + "camel_arc_all_filter.json";
//		String commit_freq = mainFolder + "filesize.txt";		
		
		
//		Hadoop
//		String mainFolder = "E:\\hadoop_data\\";
//		String issue_json = mainFolder + "hadoop_pkg_full_shorted.json";
//		String commit_freq = mainFolder + "filesize.txt";
		
// 		Wicket
//		String mainFolder = "F:\\wicket_data\\";
//		String issue_json = mainFolder + "wicket_arc_all_filter.json";
//		String commit_freq = mainFolder + "wicket_freq.txt";
		
// 		Continuum
//		String mainFolder = "F:\\continuum_data\\";
//		String issue_json = mainFolder + "continuum_arc_all.json";
//		String commit_freq = mainFolder + "continuum_freq.txt";
//		
// 		cxf
//		String mainFolder = "F:\\cxf_data\\";
//		String issue_json = mainFolder + "cxf_arc_all.json";
//		String commit_freq = mainFolder + "cxf_freq.txt";
		
//		Struts
//		String mainFolder = "F:\\USC Google Drive\\Research\\ICSE_2017\\data\\struts2\\";
//		String issue_json = mainFolder + "all_smells\\struts2_pkg_all_filter_versions.json";
//		String commit_freq = mainFolder + "struts2_freq.txt";
		
// 		Hadoop	
//		String mainFolder = "F:\\USC Google Drive\\Research\\ICSE_2017\\data\\hadoop\\";
//		String issue_json = mainFolder + "all_smells\\hadoop_acdc_all_filter_versions.json";
//		String commit_freq = mainFolder + "Hadoop_freq.txt";
		
//		Struts
		String mainFolder = "E:\\data\\struts2\\";
		String commit_freq = mainFolder + "struts2_freq.txt";
		String issue_json = mainFolder +"all_smells\\Struts2_pkg_all.json";
	
		
		HashMap<String, Integer> countCommitsForSmelly = new HashMap<>();
		HashMap<String, Integer> countCommitsForNonSmelly = new HashMap<>();
		
		
		HashMap<String, String> commitFreg = new HashMap<>();
		Vector<String> listFiles = new Vector<>();
		
		// Parse the frequency file
		BufferedReader br = null;
		String sCurrentLine;

		br = new BufferedReader(new FileReader(commit_freq));
		// don't care the first line
		sCurrentLine = br.readLine();
				
		while ((sCurrentLine = br.readLine()) != null) {
			String[] tmp = sCurrentLine.split(" |\t");
			int size = tmp.length;
			String file_size = tmp[size-2];
			String filename = tmp[size-1];
			listFiles.add(filename);
			commitFreg.put(filename, file_size);
//			System.out.println(sCurrentLine);
//			if (filename.contains(".java") && filename.contains("org"))
//			{
//				String orgFormat = StringUtil.dir2pkg(filename);
//				String match = "src/java/"+orgFormat.replace(".", "/")+".java";
//				System.out.println(match);
//				commitFreg.put(match, freg);
//			}
		}
		if (br != null)
			br.close();
		
		JSONParser parser = new JSONParser();
		JSONArray issues = (JSONArray) parser.parse(new FileReader(issue_json));
		
		for (int i = 0; i < issues.size(); i ++){
			JSONObject issue = (JSONObject) issues.get(i);
			boolean isSmell = false;		
			JSONArray commits = (JSONArray) issue.get("commits");
			
			for (int j = 0; j < commits.size(); j++){
				JSONObject commit = (JSONObject) commits.get(j);
				JSONArray files = (JSONArray) commit.get("files");
				for (int k = 0; k < files.size(); k ++){
					JSONObject file = (JSONObject) files.get(k);
					JSONObject smells = (JSONObject) file.get("smells");
					
					if (smells != null && smells.keySet() != null){
						isSmell = true;
					}
					
					if (isSmell){
						String filename = (String) file.get("filename");
						if (filename.endsWith("java")){
							Integer counter = countCommitsForSmelly.get(filename);
							if (counter == null)
								counter = 1;
							else
								counter ++;
							countCommitsForSmelly.put(filename, counter);
						}
					}
					else
					{
						if (!((String) issue.get("affect")).equals("")){
							String filename = (String) file.get("filename");
							if (filename.endsWith("java")){
								Integer counter = countCommitsForNonSmelly.get(filename);
								if (counter == null)
									counter = 1;
								else
									counter ++;
								countCommitsForNonSmelly.put(filename, counter);
							}
						}
					}
				}
			}
		}
		
		
		//count commit freg
		System.out.print("smell issues,");
		for (String file : countCommitsForSmelly.keySet()){
			/* only for struts
			if (file.startsWith("trunk/"))
				file = file.replace("trunk/", "");
			if (file.startsWith("branches/STRUTS_2_0_X/"))
				file = file.replace("branches/STRUTS_2_0_X/", "");
			if (file.startsWith("src/mapred"))
				file = file.replace("src/mapred", "src/java");
			if (file.startsWith("src/hdfs"))
				file = file.replace("src/hdfs", "src/java");
			if (file.startsWith("src/core"))
				file = file.replace("src/core", "src/main/java");
			*/
			String fullName = "";
			for (String s : listFiles){
				if (s.endsWith(file)) {
					fullName = s;
				}
			}
			
			String freq = "";
			if (fullName != ""){
				freq = commitFreg.get(fullName);
			}
//			if (freq == null){
//				System.out.print(file + ",");
//			}
			if (freq != ""){
				System.out.print(freq + ",");
			}
		}
		
		System.out.println();
		System.out.print("non_smell issues,");
		for (String file : countCommitsForNonSmelly.keySet()){
			if (file.startsWith("trunk/"))
				file = file.replace("trunk/", "");
			if (file.startsWith("branches/STRUTS_2_0_X/"))
				file = file.replace("branches/STRUTS_2_0_X/", "");
			if (file.startsWith("src/mapred"))
				file = file.replace("src/mapred", "src/java");
			if (file.startsWith("src/hdfs"))
				file = file.replace("src/hdfs", "src/java");
			if (file.startsWith("src/core"))
				file = file.replace("src/core", "src/main/java");
			String fullName = "";
			for (String s : listFiles){
				if (s.endsWith(file)) {
					fullName = s;
				}
			}
			String freq = "";
			if (fullName != ""){
				freq = commitFreg.get(fullName);
			}
			if (freq != ""){
				System.out.print(freq + ",");
			}
		}
	}
}
