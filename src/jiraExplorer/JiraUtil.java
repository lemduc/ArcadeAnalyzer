package jiraExplorer;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import net.rcarz.jiraclient.Issue;

import com.thoughtworks.xstream.XStream;

import edu.usc.softarch.arcade.util.FileUtil;

public class JiraUtil {
	public static List<Issue> deserializeIssues(final File file_) {
		final XStream xstream = new XStream();
		String xml = null;
		xml = FileUtil.readFile(file_, StandardCharsets.UTF_8);
		final List<Issue> issuesList = (List<Issue>) xstream.fromXML(xml);
		return issuesList;
	}
}
