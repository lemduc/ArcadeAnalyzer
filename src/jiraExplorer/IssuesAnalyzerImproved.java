package jiraExplorer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.Version;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;


import com.google.common.base.Joiner;
import com.thoughtworks.xstream.XStream;

import edu.usc.softarch.arcade.util.FileListing;
import edu.usc.softarch.arcade.util.FileUtil;
import edu.usc.softarch.arcade.util.MapUtil;
import edu.usc.softarch.arcade.util.StopWatch;

public class IssuesAnalyzerImproved {
	static Logger logger = org.apache.logging.log4j.LogManager.getLogger(IssuesAnalyzerImproved.class);

	public static void main(final String[] args) throws FileNotFoundException {
		//PropertyConfigurator.configure(Config.getLoggingConfigFilename());
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		// The directory where the serialized issue files are stored
		final String issuesDir = args[0];

		final List<File> filesList = FileListing.getFileListing(FileUtil.checkDir(issuesDir, false, false));

		final List<Issue> allIssues = new ArrayList<Issue>();
		for (final File file : filesList) {
			if (file.getName().endsWith(".ser")) {
				final List<Issue> issues = JiraUtil.deserializeIssues(file);
				// System.out.println(Joiner.on("\n").join(issues));
				allIssues.addAll(issues);
				System.out.println("Loaded file: " + file.getName());
			}
		}

		final Map<String, Issue> issuesMap = new HashMap<String, Issue>();
		final List<Issue> allIssuesNoDupes = new ArrayList<Issue>();
		for (final Issue issue : allIssues) {
			if (!issuesMap.containsKey(issue.getKey())) {
				issuesMap.put(issue.getKey(), issue);
				allIssuesNoDupes.add(issue);
			}
		}
		System.out.println("allIssues size: " + allIssues.size());
		System.out.println("allIssuesNoDupes size: " + allIssuesNoDupes.size());

		// key: version number, value: count of issues for that version
		Map<String, HashMap<String, Integer>> versionToIssueCountMap = new HashMap<String,HashMap <String, Integer>>();
		for (Issue issue : allIssues) {
			for (Version version : issue.getVersions()) {
				HashMap<String, Integer> issueCount = versionToIssueCountMap.get(version.toString());
				if (issueCount != null) {
					String issueType = issue.getIssueType().getName();
					Integer counter = issueCount.get(issueType);
					if (counter != null) {
						counter ++;
						issueCount.put(issueType, counter);
					} else {
						issueCount.put(issueType, 1);
					}
				} else {
					String issueType = issue.getIssueType().getName();
					issueCount = new HashMap<>();
					issueCount.put(issueType, 1);
				}
				versionToIssueCountMap.put(version.toString(), issueCount);
			}
		}

		// You may need to change the line below so that sortbyKeyVersion works
		// for your project
		versionToIssueCountMap = MapUtil.sortByKeyVersion(versionToIssueCountMap);

		System.out.println(Joiner.on("\n").withKeyValueSeparator("=").join(versionToIssueCountMap));
		System.out.println("Running time: " + stopWatch.getElapsedTimeSecs());
		String csv = "Version, Bugs, New Feature, Improvement, Task, Subtask, Temp\n";
		for (String version : versionToIssueCountMap.keySet()) {
			HashMap<String, Integer> issue = versionToIssueCountMap.get(version);
			csv += version + "," + issue.get("Bug") + "," + issue.get("New Feature") + "," + issue.get("Improvement")
					+ "," + issue.get("Task") + "," + issue.get("Sub-task") + "," + issue.get("Temp") + "\n";
		}
		final String csvFilename = issuesDir + File.separatorChar + "issueCount.csv";
		try {
			csv.replace("null", "0");
			FileUtils.writeStringToFile(new File(csvFilename), csv);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// The filename that is generated based on the supplied issuesDir
		final String mapFilename = issuesDir + File.separatorChar + "version2issuecountmap.obj";
		final XStream xstream = new XStream();
		final String xml = xstream.toXML(versionToIssueCountMap);
		final PrintWriter writer = new PrintWriter(FileUtil.checkFile(mapFilename, false, false));
		writer.print(xml);
		writer.close();

	}

}
