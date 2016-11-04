package ArchSmellAnalyzer.JsonAnalyze;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.apache.poi.util.ArrayUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.omg.CORBA.VersionSpecHelper;

public class RQ4_CountCommitsRelated2LongTermSmelly {
	
	/*
	 * Detect if one file has smell, then keep track the number of its related commits
	 * 
	 * 
	 */
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException{

//		String issue_json = mainFolder + "struts2\\all_smells\\struts2_acdc_all_filter_versions.json";
//		String commit_freq = mainFolder + "struts2\\struts2_freq.txt";
		
//		String mainFolder = "F:\\USC Google Drive\\Research\\ICSE_2017\\data\\";
//		String issue_json = mainFolder + "hadoop\\all_smells\\hadoop_pkg_all_filter_versions.json";
//   		String commit_freq = mainFolder + "hadoop\\Hadoop_freq.txt";
   		
//   		Wicket
		String mainFolder = "F:\\wicket_data\\";
		String issue_json = mainFolder + "wicket_pkg_all_filter.json";

		
//		String issue_json = "F:\\ASE_2016_data\\Struts2\\all_smells\\Struts2_pkg_all.json";
   		
   		HashMap<String, HashMap<String, Integer>> countAffectedVersions = new HashMap<>();
   		HashMap<String, HashMap<String, Integer>> countAffectedVersionsNonSmelly = new HashMap<>();
		
		HashMap<String, Integer> countCommitsForSmelly = new HashMap<>();
		HashMap<String, Integer> countCommitsForNonSmelly = new HashMap<>();
		
		HashMap<String, Integer> countIssuesForSmelly = new HashMap<>();
		HashMap<String, Integer> countIssuesForNonSmelly = new HashMap<>();
		
		HashMap<String, Integer> countIssuesForVersion = new HashMap<>();
		HashMap<String, HashMap<String, Long>> countEffortForVersion = new HashMap<>();
		
		HashSet<String> versionList = new HashSet<>();
		
		HashMap<String, String> commitFreg = new HashMap<>();
		Vector<String> listFiles = new Vector<>();
		
		// Parse the frequency file
		BufferedReader br = null;
		String sCurrentLine;

		/*
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
		if (br != null)br.close();
		*/
		
		JSONParser parser = new JSONParser();
		JSONArray issues = (JSONArray) parser.parse(new FileReader(issue_json));
		
		for (int i = 0; i < issues.size(); i ++){
			JSONObject issue = (JSONObject) issues.get(i);
			boolean isSmell = false;		
			String affectedVersion = (String) issue.get("affect");
			Long fixingTime = (Long) issue.get("time");
			versionList.add(affectedVersion);
			HashSet<String> involvedFiles = new HashSet<>();
			boolean isSmellIssues = false;
			
			Integer c = countIssuesForVersion.get(affectedVersion);
			if (c==null)
				c=0;
			c++;
			countIssuesForVersion.put(affectedVersion, c);
			
			
			JSONArray commits = (JSONArray) issue.get("commits");
			
			for (int j = 0; j < commits.size(); j++){
				JSONObject commit = (JSONObject) commits.get(j);
				JSONArray files = (JSONArray) commit.get("files");
				for (int k = 0; k < files.size(); k ++){
					JSONObject file = (JSONObject) files.get(k);
					JSONObject smells = (JSONObject) file.get("smells");
					
					if (smells != null && smells.keySet() != null){
						isSmell = true;
						isSmellIssues = true;
					}
					
					if (isSmell){
						String filename = (String) file.get("filename");
						if (filename.endsWith("java")){
							involvedFiles.add(filename);
							Integer counter = countCommitsForSmelly.get(filename);
							if (counter == null)
								counter = 1;
							else
								counter ++;
							countCommitsForSmelly.put(filename, counter);
							
							//maping to affected versions
							HashMap affectedVersions = countAffectedVersions.get(filename);
							if (affectedVersions == null)
								affectedVersions = new HashMap<>();
							Integer countVersioin = (Integer) affectedVersions.get(affectedVersion);
							if (countVersioin == null)
								countVersioin = 0;
							countVersioin++;
							affectedVersions.put(affectedVersion, countVersioin);
							countAffectedVersions.put(filename, affectedVersions);

						}
					}
					else
					{
//						if (!((String) issue.get("affect")).equals("")){
							String filename = (String) file.get("filename");
							if (filename.endsWith("java")){
								Integer counter = countCommitsForNonSmelly.get(filename);
								if (counter == null)
									counter = 1;
								else
									counter ++;
								countCommitsForNonSmelly.put(filename, counter);
//							}
							
								//keep track non smelly files and their affected versions
							HashMap affectedVersions = countAffectedVersionsNonSmelly.get(filename);
							if (affectedVersions == null)
								affectedVersions = new HashMap<>();
							Integer countVersioin = (Integer) affectedVersions.get(affectedVersion);
							if (countVersioin == null)
								countVersioin = 0;
							countVersioin++;
							affectedVersions.put(affectedVersion, countVersioin);
							countAffectedVersionsNonSmelly.put(filename, affectedVersions);
						}
					}
				}
			}
			
			//Update fixing time
			for (String filename : involvedFiles){
				HashMap<String, Long> fixingPerVersion = countEffortForVersion.get(filename);
				if (fixingPerVersion == null)
					fixingPerVersion = new HashMap<>();
				Long totalfixingtime = fixingPerVersion.get(affectedVersion);
				if (totalfixingtime == null)
					totalfixingtime = (long) 0;
				totalfixingtime += fixingTime;
				fixingPerVersion.put(affectedVersion, totalfixingtime);
				countEffortForVersion.put(filename, fixingPerVersion);
			}
			
			
			if (isSmellIssues){
 				Integer count = countIssuesForSmelly.get(affectedVersion);
				if (count == null){
					count = 0;
				}
				count++;
				countIssuesForSmelly.put(affectedVersion, count);
			} else{
				Integer count = countIssuesForNonSmelly.get(affectedVersion);
				if (count == null){
					count = 0;
				}
				count++;
				countIssuesForNonSmelly.put(affectedVersion, count);
			}
			
		}
	
		// Count percentage
		ArrayList<String> sortList = new ArrayList<>(versionList);
		Collections.sort(sortList);
		
		for (String version: sortList){
			System.out.print(version+ ",") ;
			
			Integer sm;
			Integer nsm;
		
			
			sm = countIssuesForSmelly.get(version);
			if (sm == null)
				sm = 0;
			nsm = countIssuesForNonSmelly.get(version);
			if (nsm == null)
				nsm = 0;
			
			int total = sm + nsm;
			if (total != 0)
			{	
				System.out.print((float)sm/total + ",");
				System.out.println((float)nsm/total + ",");
			}
		}
		
		
		//
		System.out.println("Count Long lived file******************************");
		System.out.print("File name,");
		for (String v: sortList){
			System.out.print(v+ ",");
		}
		System.out.println();
		for (String file : countAffectedVersions.keySet()){
			if (countAffectedVersions.get(file).keySet().size() > 25){ //for hadoop is 20, struts is 7
				System.out.print(file + ",");// + countAffectedVersions.get(file).size());
				for (String v: sortList){
					Integer num = countAffectedVersions.get(file).get(v);
					if ( num == null)
						num = 0;
					System.out.print(num + ",");
				}
				System.out.println();
			}
		}
		System.out.print("All issues of version,");
		for (String v: sortList){
			System.out.print(countIssuesForVersion.get(v)+ ",");
		}
		System.out.println();
		
		//
		System.out.println("Count Long lived non-smelly file******************************");
		System.out.print("File name,");
		for (String v: sortList){
			System.out.print(v+ ",");
		}
		System.out.println();
		for (String file : countAffectedVersionsNonSmelly.keySet()){
			if (countAffectedVersionsNonSmelly.get(file).keySet().size() > 1){ //for hadoop is 20, struts is 7
				System.out.print(file + ",");// + countAffectedVersions.get(file).size());
				for (String v: sortList){
					Integer num = countAffectedVersionsNonSmelly.get(file).get(v);
					if ( num == null)
						num = 0;
					System.out.print(num + ",");
				}
				System.out.println();
			}
		}
//		System.out.print("All version,");
//		for (String v: sortList){
//			System.out.print(countIssuesForVersion.get(v)+ ",");
//		}
//		System.out.println();
		
		//
		System.out.println("Count Long lived file effort******************************");
		System.out.print("File name,");
		for (String v: sortList){
			System.out.print(v+ ",");
		}
		System.out.println();
		for (String file : countAffectedVersions.keySet()){
			if (countAffectedVersions.get(file).keySet().size() > 25){
				System.out.print(file + ",");// + countAffectedVersions.get(file).size());
				for (String v: sortList){
					// compute average fixing time
					Long fixingtime = countEffortForVersion.get(file).get(v);
					Integer num = countAffectedVersions.get(file).get(v);
					float avg_fix; 
					if ( num == null)
						avg_fix = 0;
					else
						avg_fix = fixingtime/num;
					System.out.print(avg_fix + ",");
				}
				System.out.println();
			}
		}
//		System.out.print("All version,");
//		for (String v: sortList){
//			System.out.print((float) countEffortForVersion.get(key)get(v)/countIssuesForVersion.get(v)+ ",");
//		}
//		System.out.println();
		
		//count commit freg
		System.out.print("smell issues,");
		for (String file : countCommitsForSmelly.keySet()){
			String orgFile = file;
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
//				freq = commitFreg.get(fullName);
				freq = String.valueOf(countCommitsForSmelly.get(orgFile));
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
			String orgFile = file;
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
//				freq = commitFreg.get(fullName);
				freq = String.valueOf(countCommitsForNonSmelly.get(orgFile));
			}
			if (freq != ""){
				System.out.print(freq + ",");
			}
		}
//		Find top smelly files
		
		
		  
		
		
	}
}
