package ArchSmellAnalyzer.JsonAnalyze;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class RQ2_CountIssuesRelatedToDifferentSmelly {
	
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
//		String issue_json = mainFolder + "cxf_acdc_all.json";
		
//		Struts
		String mainFolder = "F:\\Google Drive\\Research\\ICSE_2017\\data\\struts2\\";
		String issue_json = mainFolder + "all_smells\\struts2_arc_all_filter_versions.json";
		
// 		Hadoop	
//		String mainFolder = "F:\\USC Google Drive\\Research\\ICSE_2017\\data\\hadoop\\";
//		String issue_json = mainFolder + "all_smells\\hadoop_pkg_all_filter_versions.json";
		
//		String issue_json = "F:\\hadoop_data\\hadoop_pkg_full_shorted_removed_dc.json";
//		String issue_json = "F:\\ASE_2016_data\\Struts2\\all_smells\\Struts2_pkg_all.json";
//		String issue_json = "F:\\ASE_2016_data\\Lucene\\all_smells\\Lucene_arc_all.json";
		
		HashMap<String, Integer> countIssuesForSmelly = new HashMap<>();
		HashMap<String, Integer> countIssuesForNonSmelly = new HashMap<>();
		
		HashMap<String, HashMap<String, Integer>> countIssuesForASmell = new HashMap<>();
		
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
					// concern
					boolean Concern_Overload = false;
					boolean Scattered_Parasitic_Functionality = false;
					
					// dependency
					boolean Link_Overload = false;
					boolean Dependency_Cycle = false;
					
					//coupling
					boolean Logical_Dependency = false;
					boolean Duplicate_Functionality = false;
					
					//interface
					boolean Sloopy_Delegation = false;
					boolean Unused_Interface = false;
					boolean Unused_Brick = false;
					boolean Lego_Syndome = false;
					boolean Brick_Functionality_Overload = false;
					
					if (smells != null && smells.keySet() != null){
						isSmell = true;
						if (smells.get("Logical_Dependency") != null)
							Logical_Dependency = true;
						if (smells.get("Clone_Comp") != null)
							Duplicate_Functionality = true;
						
						if (smells.get("Link_Overload") != null)
							Link_Overload = true;
						if (smells.get("Dependency_Cycle") != null)
							Dependency_Cycle = true;
						
						if (smells.get("Scattered_Parasitic_Functionality") != null)
							Scattered_Parasitic_Functionality = true;
						if (smells.get("Concern_Overload") != null)
							Concern_Overload = true;
						
						if (smells.get("Sloopy_Delegation") != null)
							Sloopy_Delegation = true;
						if (smells.get("Unused_Interface") != null)
							Unused_Interface = true;
						if (smells.get("Unused_Brick") != null)
							Unused_Brick = true;
						if (smells.get("Lego_Syndome") != null)
							Lego_Syndome = true;
						if (smells.get("Overload") != null)
							Brick_Functionality_Overload = true;
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
							
							if(Logical_Dependency)
								addToHashMap("Logical_Dependency", filename, countIssuesForASmell);
							if(Duplicate_Functionality)
								addToHashMap("Clone_Comp", filename, countIssuesForASmell);
							
							if(Link_Overload)
								addToHashMap("Link_Overload", filename, countIssuesForASmell);
							if(Dependency_Cycle)
								addToHashMap("Dependency_Cycle", filename, countIssuesForASmell);
							
							if(Scattered_Parasitic_Functionality)
								addToHashMap("Scattered_Parasitic_Functionality", filename, countIssuesForASmell);
							if(Concern_Overload)
								addToHashMap("Concern_Overload", filename, countIssuesForASmell);
							
							
							if(Sloopy_Delegation)
								addToHashMap("Sloopy_Delegation", filename, countIssuesForASmell);
							if(Unused_Interface)
								addToHashMap("Unused_Interface", filename, countIssuesForASmell);
							if(Unused_Brick)
								addToHashMap("Unused_Brick", filename, countIssuesForASmell);
							if(Lego_Syndome)
								addToHashMap("Lego_Syndome", filename, countIssuesForASmell);
							if(Brick_Functionality_Overload)
								addToHashMap("Brick_Functionality_Overload", filename, countIssuesForASmell);
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
		System.out.println();
		//Count_Critical(smell_issues);
		//Count_Issues_Type(smell_issues);
		
		for (String key : countIssuesForASmell.keySet()){
			System.out.print(key + ",");
			for (String file : countIssuesForASmell.get(key).keySet()){
				System.out.print(countIssuesForASmell.get(key).get(file) + ",");
			}
			System.out.println();
		}
		System.out.print("non_smell issues,");
		for (String file : countIssuesForNonSmelly.keySet()){
			System.out.print(countIssuesForNonSmelly.get(file) + ",");
		}
		//Count_Critical(non_smell_issues);
		//Count_Issues_Type(non_smell_issues);
		
		System.out.println("Prediction");
		System.out.print("total issues,");
		for (String key : countIssuesForASmell.keySet()){
			System.out.print(key + ",");
		}
		System.out.println("end");
		for (String file : countIssuesForSmelly.keySet()){
			System.out.print(countIssuesForSmelly.get(file) + ",");
			for (String key: countIssuesForASmell.keySet()){
				String value = (countIssuesForASmell.get(key).get(file) == null)? "0" : "1";
				System.out.print(value + ",");
			}
			System.out.println("end");
		}
		
	}

	private static void addToHashMap(String smellType, String filename, HashMap<String, HashMap<String, Integer>> countIssuesForASmell) {
		HashMap<String, Integer> countIssuesForSmelly = countIssuesForASmell.get(smellType); 
		if (countIssuesForSmelly == null)
			countIssuesForSmelly = new HashMap<String, Integer>();
		Integer counter = countIssuesForSmelly.get(filename);
		if (counter == null)
			counter = 1;
		else
			counter ++;
		countIssuesForSmelly.put(filename, counter);
		countIssuesForASmell.put(smellType, countIssuesForSmelly);
	}
}
