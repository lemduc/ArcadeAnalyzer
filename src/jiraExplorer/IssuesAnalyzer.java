package jiraExplorer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.Version;

import org.apache.logging.log4j.Logger;


import com.google.common.base.Joiner;
import com.thoughtworks.xstream.XStream;

import edu.usc.softarch.arcade.util.FileListing;
import edu.usc.softarch.arcade.util.FileUtil;
import edu.usc.softarch.arcade.util.MapUtil;
import edu.usc.softarch.arcade.util.StopWatch;

public class IssuesAnalyzer {
	static Logger logger = org.apache.logging.log4j.LogManager.getLogger(IssuesAnalyzer.class);

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
		Map<String, Integer> versionToIssueCountMap = new HashMap<String, Integer>();
		for (final Issue issue : allIssues) {
			for (final Version version : issue.getVersions()) {
				Integer issueCount = versionToIssueCountMap.get(version.toString());
				if (issueCount != null) {
					issueCount = issueCount + 1;
					versionToIssueCountMap.put(version.toString(), issueCount);
				} else {
					versionToIssueCountMap.put(version.toString(), 1);
				}
			}
			for (final Version version : issue.getFixVersions()) {
				Integer issueCount = versionToIssueCountMap.get(version.toString());
				if (issueCount != null) {
					issueCount = issueCount + 1;
					versionToIssueCountMap.put(version.toString(), issueCount);
				} else {
					versionToIssueCountMap.put(version.toString(), 1);
				}
			}
		}

		// You may need to change the line below so that sortbyKeyVersion works
		// for your project
		versionToIssueCountMap = MapUtil.sortByKeyVersion(versionToIssueCountMap);

		System.out.println(Joiner.on("\n").withKeyValueSeparator("=").join(versionToIssueCountMap));

		System.out.println("Running time: " + stopWatch.getElapsedTimeSecs());

		// The filename that is generated based on the supplied issuesDir
		final String mapFilename = issuesDir + File.separatorChar + "version2issuecountmap.obj";
		final XStream xstream = new XStream();
		final String xml = xstream.toXML(versionToIssueCountMap);
		final PrintWriter writer = new PrintWriter(FileUtil.checkFile(mapFilename, false, false));
		writer.print(xml);
		writer.close();

	}

}
