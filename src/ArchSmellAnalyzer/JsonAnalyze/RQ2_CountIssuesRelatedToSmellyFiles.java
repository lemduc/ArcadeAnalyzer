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
		String mainFolder = "F:\\USC Google Drive\\Research\\ICSE_2017\\data\\hadoop\\";
//		String issue_json = "F:\\hadoop_data\\hadoop_pkg_full_shorted_removed_dc.json";
		String issue_json = mainFolder + "all_smells\\hadoop_pkg_all_filter_versions.json";

//		String issue_json = "F:\\ASE_2016_data\\Struts2\\all_smells\\Struts2_pkg_all.json";
				
//		String issue_json = "F:\\ASE_2016_data\\Lucene\\all_smells\\Lucene_arc_all.json";
		
		HashMap<String, Integer> countIssuesForSmelly = new HashMap<>();
		HashMap<String, Integer> countIssuesForNonSmelly = new HashMap<>();
		
		
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
		//Count_Critical(smell_issues);
		//Count_Issues_Type(smell_issues);
		
		System.out.println();
		System.out.print("non_smell issues,");
		for (String file : countIssuesForNonSmelly.keySet()){
			System.out.print(countIssuesForNonSmelly.get(file) + ",");
		}
		//Count_Critical(non_smell_issues);
		//Count_Issues_Type(non_smell_issues);
	}
}
