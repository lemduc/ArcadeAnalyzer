package ArchSmellAnalyzer.JsonAnalyze;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class RQ2_CountIssuesRelatedToSmellyFiles {
	
	/*
	 * Detect if one file has smell, then keep track the number of its related issues
	 * 
	 * 
	 */
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException{
// 		Continuum
//		String mainFolder = "F:\\continuum_data\\";
//		String issue_json = mainFolder + "continuum_acdc_all.json";
//		
// 		cxf
//		String mainFolder = "F:\\cxf_data\\";
//		String issue_json = mainFolder + "cxf_arc_all_filter.json";
		
//		Struts
//		String mainFolder = "F:\\USC Google Drive\\Research\\ICSE_2017\\data\\hadoop\\";
//		String issue_json = mainFolder + "all_smells\\hadoop_arc_all_filter_versions.json";
		
// 		Hadoop	
//		String mainFolder = "F:\\USC Google Drive\\Research\\ICSE_2017\\data\\hadoop\\";
//		String issue_json = mainFolder + "all_smells\\hadoop_pkg_all_filter_versions.json";
		
//		String issue_json = "F:\\hadoop_data\\hadoop_pkg_full_shorted_removed_dc.json";
//		String issue_json = "F:\\ASE_2016_data\\Struts2\\all_smells\\Struts2_pkg_all.json";
//		String issue_json = "F:\\ASE_2016_data\\Lucene\\all_smells\\Lucene_arc_all.json";
		
//		Continuum
//		String mainFolder = "F:\\continuum_data\\";
//		String issue_json = mainFolder + "continuum_pkg_all.json";
		
// 		Wicket
//		String mainFolder = "F:\\wicket_data\\";
//		String issue_json = mainFolder + "wicket_pkg_all_filter.json";

// 		caMEL
//		String mainFolder = "F:\\camel_data\\";
//		String issue_json = mainFolder + "camel_arc_all_filter.json";
		
//		nutch 
//		String mainFolder = "E:\\nutch_data\\";
//		String issue_json = mainFolder + "nutch_pkg_all_filter.json";
		
//		openjpa 
//		String mainFolder = "E:\\openjpa_data\\";
//		String issue_json = mainFolder + "openjpa_pkg_all_filter.json";
		
		String mainFolder = "E:\\lucene_data\\";
		String issue_json = mainFolder + "lucene_arc_all_filter.json";
		
		HashMap<String, Integer> countIssuesForSmelly = new HashMap<>();
		HashMap<String, Integer> countIssuesForNonSmelly = new HashMap<>();
		HashMap<String, HashMap<String, Integer>> countIssuesForASingleSmell = new HashMap<>(); 
		
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
						if (filename.endsWith(".java")){
							Integer counter = countIssuesForSmelly.get(filename);
							if (counter == null)
								counter = 1;
							else
								counter ++;
							countIssuesForSmelly.put(filename, counter);
						}
						
//						if ( (long) file.get("total_smell") == (long) 1){
//							String smellName = (String) smells.keySet().iterator().next();
//							HashMap<String, Integer> typePerFile = countIssuesForASingleSmell.get(smellName);
//							if (typePerFile == null)
//								typePerFile = new HashMap<>();
//							Integer counter = typePerFile.get(filename);
//							if (counter == null)
//								counter = 0;
//							counter++;
//							typePerFile.put(filename, counter);
//							countIssuesForASingleSmell.put(smellName, typePerFile);
//						}
					}
					else
					{
						if (!((String) issue.get("affect")).equals("")){
							String filename = (String) file.get("filename");
							if (filename.endsWith(".java")){
								Integer counter = countIssuesForNonSmelly.get(filename);
								if (counter == null)
									counter = 1;
								else
									counter ++;
								countIssuesForNonSmelly.put(filename, counter);
							}
						}
					}
				}
			}
		}
		
		/*
		File json_file = new File(merged_output_file+"_smell.json");
		// if file doesnt exists, then create it
		if (!json_file.exists()) {
			json_file.createNewFile();
		}
		FileWriter fw = new FileWriter(json_file.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(smell_issues.toJSONString());
		bw.close();
		
		json_file = new File(merged_output_file+"_non_smell.json");
		// if file doesnt exists, then create it
		if (!json_file.exists()) {
			json_file.createNewFile();
		}
		fw = new FileWriter(json_file.getAbsolutePath());
		bw = new BufferedWriter(fw);
		bw.write(non_smell_issues.toJSONString());
		bw.close();
		 
		System.out.println("Done");
		*/
		
		//count priority
		System.out.print("smell issues,");
		for (String file : countIssuesForSmelly.keySet()){
			System.out.print(countIssuesForSmelly.get(file) + ",");
		}	
//		System.out.println();
		//Count_Critical(smell_issues);
		//Count_Issues_Type(smell_issues);
		
		// Print number of issues
//		System.out.println("issues per smell");
//		for (String key : countIssuesForASingleSmell.keySet()){
//			System.out.print(key + ",");
//			for (String file : countIssuesForASingleSmell.get(key).keySet()){
//				System.out.print( + countIssuesForASingleSmell.get(key).get(file) + ",");
//			}
//			System.out.println();
//		}
		
		System.out.println();
		System.out.print("non_smell issues,");
		for (String file : countIssuesForNonSmelly.keySet()){
			System.out.print(countIssuesForNonSmelly.get(file) + ",");
		}
		//Count_Critical(non_smell_issues);
		//Count_Issues_Type(non_smell_issues);
	}
}
