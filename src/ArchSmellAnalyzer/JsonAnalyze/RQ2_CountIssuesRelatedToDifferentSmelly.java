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
//		String mainFolder = "E:\\cxf_data\\";
//		String issue_json = mainFolder + "cxf_pkg_all.json";
//		String commit_freq = mainFolder + "cxf_freq.txt";
		
// 		camel
//		String mainFolder = "E:\\camel_data\\";
//		String issue_json = mainFolder + "camel_pkg_all.json";
//		String commit_freq = mainFolder + "camel_freq.txt";
		
// 		continuum
//		String mainFolder = "E:\\continuum_data\\";
//		String issue_json = mainFolder + "continuum_pkg_all.json";
//		String commit_freq = mainFolder + "continuum_freq.txt";

		
// 		nutch
//		String mainFolder = "E:\\nutch_data\\";
//		String issue_json = mainFolder + "nutch_pkg_all.json";
//		String commit_freq = mainFolder + "nutch_freq.txt";
		
// 		nutch
//		String mainFolder = "E:\\ignite_data\\";
//		String issue_json = mainFolder + "ignite_arc_all.json";
//		String commit_freq = mainFolder + "ignite_freq.txt";

		String mainFolder = "E:\\storm_data\\";
		String issue_json = mainFolder + "storm_acdc_all.json";
		String commit_freq = mainFolder + "storm_freq.txt";

//		String mainFolder = "E:\\zookeeper_data\\";
//		String issue_json = mainFolder + "zookeeper_acdc_all.json";
//		String commit_freq = mainFolder + "zookeeper_freq.txt";
		
// 		openjpa
//		String mainFolder = "E:\\openjpa_data\\";
//		String issue_json = mainFolder + "openjpa_acdc_all.json";
//		String commit_freq = mainFolder + "openjpa_freq.txt";


// 		wicket
//		String mainFolder = "E:\\wicket_data\\";
//		String issue_json = mainFolder + "wicket_arc_all.json";
//		String commit_freq = mainFolder + "wicket_freq.txt";
		
		
////		Struts
//		String mainFolder = "G:\\My Drive\\Research\\ICSE_2017\\data\\struts2\\";
//		String issue_json = mainFolder + "all_smells\\struts2_arc_all_filter_versions.json";
//		String commit_freq = mainFolder + "struts2_freq.txt";
		
// 		Hadoop	
//		String mainFolder = "G:\\My Drive\\Research\\ICSE_2017\\data\\hadoop\\";
//		String issue_json = mainFolder + "all_smells\\hadoop_arc_all_filter_versions.json";
//		String commit_freq = mainFolder + "hadoop_freq.txt";
		
//		String issue_json = "F:\\hadoop_data\\hadoop_pkg_full_shorted_removed_dc.json";
//		String issue_json = "F:\\ASE_2016_data\\Struts2\\all_smells\\Struts2_pkg_all.json";
//		String issue_json = "F:\\ASE_2016_data\\Lucene\\all_smells\\Lucene_arc_all.json";

// 		lucene
//		String mainFolder = "E:\\lucene_data\\";
//		String issue_json = mainFolder + "lucene_arc_all.json";
//		String commit_freq = mainFolder + "lucene_freq.txt";

// 		tika
//		String mainFolder = "E:\\tika_data\\";
//		String issue_json = mainFolder + "tika_arc_all.json";
//		String commit_freq = mainFolder + "tika_freq.txt";

		
		HashMap<String, Integer> countIssuesForSmelly = new HashMap<>();
		HashMap<String, Integer> countIssuesForNonSmelly = new HashMap<>();
		
		HashMap<String, HashMap<String, Integer>> countIssuesForASmell = new HashMap<>();
		
		HashMap<String, String> commitFreg = new HashMap<>();
		HashMap<String, String> priorityMap = new HashMap<>();
		HashMap<String, String> typeMap = new HashMap<>();
		
		Vector<String> listFiles = new Vector<>();
		
		// Parse the frequency file
		BufferedReader br = null;
		String sCurrentLine;

		br = new BufferedReader(new FileReader(commit_freq));
		// don't care the first line
		sCurrentLine = br.readLine();
				
		while ((sCurrentLine = br.readLine()) != null) {
			String[] tmp = sCurrentLine.split(" ");
			int size = tmp.length;
			String freg = sCurrentLine.split(" ")[size-2];
			String filename = sCurrentLine.split(" ")[size-1];
			listFiles.add(filename);
			commitFreg.put(filename, freg);
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
		
		System.out.println("Priority, Logical_Dependency, Clone_Comp, Link_Overload, Dependency_Cycle, Scattered_Parasitic_Functionality, Concern_Overload, Sloopy_Delegation, Unused_Interface, Unused_Brick, Lego_Syndome, Overload");
		
		for (int i = 0; i < issues.size(); i ++){
			JSONObject issue = (JSONObject) issues.get(i);
			boolean isSmell = false;		
			JSONArray commits = (JSONArray) issue.get("commits");
			String priority = (String) issue.get("priority");
			String bugtype = (String) issue.get("type");
		
			// concern
			boolean i_Concern_Overload = false;
			boolean i_Scattered_Parasitic_Functionality = false;
			
			// dependency
			boolean i_Link_Overload = false;
			boolean i_Dependency_Cycle = false;
			
			//coupling
			boolean i_Logical_Dependency = false;
			boolean i_Duplicate_Functionality = false;
			
			//interface
			boolean i_Sloopy_Delegation = false;
			boolean i_Unused_Interface = false;
			boolean i_Unused_Brick = false;
			boolean i_Lego_Syndome = false;
			boolean i_Brick_Functionality_Overload = false;
		
			
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
						if (smells.get("Logical_Dependency") != null) {
							i_Logical_Dependency = true;
							Logical_Dependency = true;
						} 
						
						if (smells.get("Clone_Comp") != null){
							i_Duplicate_Functionality = true;
							Duplicate_Functionality = true;
						}
						
						if (smells.get("Link_Overload") != null){
							i_Link_Overload = true;
							Link_Overload = true;
						}
						
						if (smells.get("Dependency_Cycle") != null){
							i_Dependency_Cycle = true;
							Dependency_Cycle = true;
						}
						
						if (smells.get("Scattered_Parasitic_Functionality") != null){
							i_Scattered_Parasitic_Functionality = true;
							Scattered_Parasitic_Functionality = true;
						}
						
						if (smells.get("Concern_Overload") != null){
							i_Concern_Overload = true;
							Concern_Overload = true;
						}
						
						if (smells.get("Sloopy_Delegation") != null){
							i_Sloopy_Delegation = true;
							Sloopy_Delegation = true;
						}
						
						if (smells.get("Unused_Interface") != null){
							i_Unused_Interface = true;
							Unused_Interface = true;
						}
						
						if (smells.get("Unused_Brick") != null){
							i_Unused_Brick = true;
							Unused_Brick = true;
						}
						
						if (smells.get("Lego_Syndome") != null){
							i_Lego_Syndome = true;
							Lego_Syndome = true;
						}
						
						if (smells.get("Overload") != null){
							i_Brick_Functionality_Overload = true;
							Brick_Functionality_Overload = true;
						}
						
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
							
							if(priority != null)
								priorityMap.put(filename, priority);
							if(bugtype != null)
								typeMap.put(filename, bugtype);
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
			
			// printout issue problem
//			if (isSmell){
//			System.out.print(priority + ",");
//			System.out.print(i_Logical_Dependency ? 1 + ",": 0 + ",");
//			System.out.print(i_Duplicate_Functionality ? 1 + ",": 0 + ",");
//			System.out.print(i_Link_Overload ? 1 + ",": 0 + ",");
//			System.out.print(i_Dependency_Cycle ? 1 + ",": 0 + ",");
//			System.out.print(i_Scattered_Parasitic_Functionality ? 1 + ",": 0 + ",");
//			System.out.print(i_Concern_Overload ? 1 + ",": 0 + ",");
//			System.out.print(i_Sloopy_Delegation ? 1 + ",": 0 + ",");
//			System.out.print(i_Unused_Interface ? 1 + ",": 0 + ",");
//			System.out.print(i_Unused_Brick ? 1 + ",": 0 + ",");
//			System.out.print(i_Lego_Syndome ? 1 + ",": 0 + ",");
//			System.out.print(i_Brick_Functionality_Overload ? 1 + "\n": 0 + "\n");
//			}
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
		
		System.out.println("\nPrediction");
		System.out.print("total issues,");
		for (String key : countIssuesForASmell.keySet()){
			System.out.print(key + ",");
		}
		System.out.print("change_freq,");
		System.out.print("prioprity,");
		System.out.print("type,");
//		System.out.print("time,");
		System.out.println("name");
		
		for (String file : countIssuesForSmelly.keySet()){
			System.out.print(countIssuesForSmelly.get(file) + ",");
			for (String key: countIssuesForASmell.keySet()){
				String value = (countIssuesForASmell.get(key).get(file) == null)? "0" : "1";
				System.out.print(value + ",");
			}
			System.out.print(commitFreg.get(getCleanedFileName(file)) + ",");
			System.out.print(priorityMap.get(file ) + ',');
			System.out.print(typeMap.get(file) + ',');
//			System.out.print(getCleanedFileName(file) + ",");
//			System.out.println(file);
			System.out.println();
		}
		
	}
	
	public static String getCleanedFileName(String name){
		String output;
		// process Struts
		if (name.startsWith("trunk/"))
			output = name.substring(6);
		else if (name.startsWith("branches/STRUTS_2_0_X/"))
			output = name.substring(22);
		else if (name.startsWith("branches/STRUTS_2_3_14_2_X/"))
			output = name.substring(27);
		else if (name.startsWith("branches/STRUTS_2_3_15_X/"))
			output = name.substring(25);
		// process Hadoop
		else if (name.startsWith("src/hdfs/"))
			output = name.substring(8);
		else if (name.startsWith("src/mapred/"))
			output = name.substring(10);
		// process open-jpa
		else if (name.startsWith("a/"))
			output = name.substring(2);
		else if (name.startsWith("b/"))
			output = name.substring(2);
		// process continuum
		else if (name.startsWith("branches/continuum-1.2.x/"))
			output = name.substring(25);
		else if (name.startsWith("branches/continuum-1.3.x/"))
			output = name.substring(25);
		else if (name.startsWith("continuum/branches/continuum-1.0.x/"))
			output = name.substring(35);
		else if (name.startsWith("continuum/trunk/"))
			output = name.substring(16);
		// process Camel
		else if (name.startsWith("branches/camel-2.11.x/"))
			output = name.substring(22);
		else if (name.startsWith("branches/camel-2.10.x/"))
			output = name.substring(22);
		else if (name.startsWith("branches/camel-2.9.x/"))
			output = name.substring(21);
		else if (name.startsWith("branches/camel-2.8.x/"))
			output = name.substring(21);
		else if (name.startsWith("branches/camel-2.7.x/"))
			output = name.substring(21);
		else if (name.startsWith("branches/camel-1.x/"))
			output = name.substring(19);
		else
			output = name;
		return output;
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
