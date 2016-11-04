package ArchSmellAnalyzer.JsonAnalyze;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class RQ1_DetectSmellIssues_PerVersion {
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException{
//		String issue_json = "F:\\hadoop_data\\hadoop_pkg_full_shorted_removed_dc.json";
//		String merged_output_file = "F:\\hadoop_data\\hadoop_pkg_full_shorted_removed_dc";
		
		//Manage issues
		HashMap<String, Integer> countBuggySmellyIssue = new HashMap<>();
		HashMap<String, Integer> countSmellyIssue = new HashMap<>();
		
		HashMap<String, Integer> countBuggyNonSmellyIssue = new HashMap<>();
		HashMap<String, Integer> countNonSmellyIssue = new HashMap<>();
		Set<String> allVersionNumber = new HashSet<>();

//		Camel 
		String mainFolder = "F:\\camel_data\\";
		String issue_json = mainFolder + "camel_arc_all_filter.json";
		String merged_output_file = mainFolder + "wicket_acdc_all";
		
// 		Wicket
//		String mainFolder = "F:\\wicket_data\\";
//		String issue_json = mainFolder + "wicket_arc_all_filter.json";
//		String merged_output_file = mainFolder + "wicket_acdc_all";
		
// 		Continuum
//		String mainFolder = "F:\\continuum_data\\";
//		String issue_json = mainFolder + "continuum_pkg_all.json";
//		String merged_output_file = mainFolder + "continuum_acdc_all";
		
		
// 		cxf
//		String mainFolder = "F:\\cxf_data\\";
//		String issue_json = mainFolder + "cxf_arc_all_filter.json";
//		String merged_output_file = mainFolder + "cxf_arc_all";
		
//		String issue_json = "F:\\Google Drive\\Research\\ICSE_2017\\data\\hadoop\\all_smells\\hadoop_acdc_all_filter_versions.json";
//		String merged_output_file = "F:\\Google Drive\\Research\\ICSE_2017\\data\\hadoop\\categorize\\hadoop_acdc_all";

//		String main_folder = "F:\\Google Drive\\Research\\ICSE_2017\\data\\struts2\\";
//		String issue_json = main_folder + "all_smells\\Struts2_acdc_all.json";
//		String merged_output_file = main_folder + "categorize\\Struts2_pkg_all";
				
//		String issue_json = "F:\\ASE_2016_data\\Lucene\\all_smells\\Lucene_arc_all.json";
//		String merged_output_file = "F:\\ASE_2016_data\\Lucene\\categorize\\Lucene_arc_all";
		
		JSONParser parser = new JSONParser();
		JSONArray issues = (JSONArray) parser.parse(new FileReader(issue_json));
		JSONArray non_smell_issues = new JSONArray();
		JSONArray smell_issues = new JSONArray();
		
		for (int i = 0; i < issues.size(); i ++){
			JSONObject issue = (JSONObject) issues.get(i);
			boolean isSmell = false;		
			JSONArray commits = (JSONArray) issue.get("commits");
			
			commitloop:
			for (int j = 0; j < commits.size(); j++){
				JSONObject commit = (JSONObject) commits.get(j);
				JSONArray files = (JSONArray) commit.get("files");
				for (int k = 0; k < files.size(); k ++){
					JSONObject file = (JSONObject) files.get(k);
					JSONObject smells = (JSONObject) file.get("smells");
					
					if (smells != null){
						isSmell = true;
						break commitloop;
					}
				}
			}
			
			if (isSmell)
				smell_issues.add(issue);
			else
				if (!((String) issue.get("affect")).equals("")){
					//System.out.println(issue.get("affect"));
					non_smell_issues.add(issue);
				}
				
		}
		
		/* don't need to write to file
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
		//System.out.println("smell issues");
		//Count_Critical(smell_issues);
		//Count_Issues_Type(smell_issues);
		Count_Buggy_Per_Version(smell_issues, countBuggySmellyIssue, countSmellyIssue, allVersionNumber);
		
		//System.out.println();
		//System.out.println("non_smell issues");
		//Count_Critical(non_smell_issues);
		//Count_Issues_Type(non_smell_issues);
		Count_Buggy_Per_Version(non_smell_issues, countBuggyNonSmellyIssue, countNonSmellyIssue, allVersionNumber);
		
		System.out.println("version, buggy smell, non_buggy smell");
		
		for (String s : allVersionNumber){
			float percentageSmell;
			float percentageNonSmell;
			
			Integer tmp = countBuggySmellyIssue.get(s);
			if (tmp==null)
				percentageSmell = 0;
			else
				percentageSmell = (float) tmp/countSmellyIssue.get(s);
			
			tmp = countBuggyNonSmellyIssue.get(s);
			if (tmp==null)
				percentageNonSmell = 0;
			else
				percentageNonSmell = (float) tmp/countNonSmellyIssue.get(s);
			
			
			
			System.out.printf(s + ", %.2f, %.2f\n", percentageSmell, percentageNonSmell);
		}
		
	}
	
	public static void Count_Critical(JSONArray issues){
		
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		Vector<String> Blocker = new Vector<String>();
		Vector<String> Critical = new Vector<String>();
		Vector<String> Major = new Vector<String>();
		Vector<String> Minor = new Vector<String>();
		Vector<String> Trivial = new Vector<String>();
		
		for (int i = 0; i < issues.size(); i ++){
			JSONObject issue = (JSONObject) issues.get(i);
			String priority = (String) issue.get("priority");
			String time = String.valueOf(((Long) issue.get("time")));
			
			switch (priority) {
			case "Blocker":
				Blocker.add(time);
				break;
				
			case "Critical":
				Critical.add(time);
				break;
	
			case "Major":
				Major.add(time);
				break;
			
			case "Minor":
				Minor.add(time);
				break;

			case "Trivial":
				Trivial.add(time);
				break;

			default:
				break;
			} 
			
			Integer tmp = counter.get(priority);
			if (tmp == null)
				tmp = 1;
			else
				tmp ++;
			
			counter.put(priority, tmp);
		}
		
		
		for (String priority : counter.keySet()){
			System.out.printf(priority + "," + counter.get(priority) + ",%.2f\n", (float) counter.get(priority)/issues.size());
		}
		
//		System.out.print("Blocker,");
//		for (String s : Blocker){
//			System.out.print(s+",");
//		}
//		System.out.println();
//		
//		System.out.print("Critical,");
//		for (String s : Critical){
//			System.out.print(s+",");
//		}
//		System.out.println();
//		
//		System.out.print("Major,");
//		for (String s : Major){
//			System.out.print(s+",");
//		}
//		System.out.println();
//		
//		System.out.print("Minor,");
//		for (String s : Minor){
//			System.out.print(s+",");
//		}
//		System.out.println();
//		
//		System.out.print("Trivial,");
//		for (String s : Trivial){
//			System.out.print(s+",");
//		}
//		System.out.println();
		
		long high_threshold = 90000;
		long log_threshold = 70000;
		countPercentage(Blocker, high_threshold, log_threshold);
		countPercentage(Critical, high_threshold, log_threshold);
		countPercentage(Major, high_threshold, log_threshold);
		countPercentage(Minor, high_threshold, log_threshold);
		countPercentage(Trivial, high_threshold, log_threshold);
	}
	
	public static void countPercentage(Vector<String> values, long high_threshold, long low_threshold) {
		int counter = 0;
		for (String s: values){
			long time = Long.valueOf(s);
			if (time < high_threshold && time > low_threshold){
				counter ++;
			}
		}
		System.out.printf("Larger than %d %.2f\n", high_threshold, (float) counter/values.size());
	}

	public static void Count_Issues_Type(JSONArray issues){
		
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		Vector<String> All = new Vector<String>();
		Vector<String> Bug = new Vector<String>();
		Vector<String> Improvement = new Vector<String>();
		Vector<String> Task = new Vector<String>();
		Vector<String> SubTask = new Vector<String>();
		Vector<String> NewFeature = new Vector<String>();
		
		for (int i = 0; i < issues.size(); i ++){
			JSONObject issue = (JSONObject) issues.get(i);
			String type = (String) issue.get("type");
			String time = String.valueOf(((Long) issue.get("time")));
			
			switch (type) {
			case "Bug":
				Bug.add(time);
				break;
				
			case "Improvement":
				Improvement.add(time);
				break;
	
			case "Task":
				Task.add(time);
				break;
			
			case "Sub-task":
				SubTask.add(time);
				break;

			case "New Feature":
				NewFeature.add(time);
				break;

			default:
				break;
			} 
			
			Integer tmp = counter.get(type);
			if (tmp == null)
				tmp = 1;
			else
				tmp ++;
			
			counter.put(type, tmp);
		}
		
		
		for (String type : counter.keySet()){
			System.out.printf(type + "," + counter.get(type) + ",%.2f\n", (float) counter.get(type)/issues.size());
		}
		
		All.addAll(Improvement);
		All.addAll(Bug);
		All.addAll(NewFeature);
		All.addAll(Task);
		All.addAll(SubTask);
		
//		long high_threshold = 90000;
//		long log_threshold = 85000;
//		countPercentage(Improvement, high_threshold, log_threshold);
//		countPercentage(Bug, high_threshold, log_threshold);
//		countPercentage(NewFeature, high_threshold, log_threshold);
//		countPercentage(Task, high_threshold, log_threshold);
//		countPercentage(SubTask, high_threshold, log_threshold);
//		countPercentage(All, high_threshold, log_threshold);
	}
	
	public static void Count_Buggy_Per_Version(JSONArray issues, HashMap<String, Integer> buggy, HashMap<String, Integer> all, Set<String> allVersionNumber){
		
		for (int i = 0; i < issues.size(); i ++){
			JSONObject issue = (JSONObject) issues.get(i);
			String type = (String) issue.get("type");
			String affected = fixVersionNumber((String) issue.get("affect"));
			
			if (affected.equals(""))
				continue;
			Integer tmp = all.get(affected);
			if(tmp ==null)
				tmp =1;
			else
				tmp++;
			all.put(affected, tmp);
			
			switch (type) {
			case "Bug":
				if (affected.equals(""))
					break;
				tmp = buggy.get(affected);
				if(tmp ==null)
					tmp =1;
				else
					tmp++;
				buggy.put(affected, tmp);
				allVersionNumber.add(affected);
				break;

			default:
				break;
			} 
			
		}
	}

	public static String fixVersionNumber(String versionNumber){
		String[] tmp = versionNumber.split("\\.");
		if (tmp.length == 4){
			return tmp[0] + "." + tmp[1] + "." + tmp[2];
		} else
		return versionNumber;
	}
}
