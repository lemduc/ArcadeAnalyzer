package Android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;





import edu.usc.softarch.arcade.facts.driver.MakeDepReader;
import edu.usc.softarch.arcade.util.FileListing;

public class converDep2SrfAllFolder {
	
	public static void main(String[] args) throws FileNotFoundException {
		
	String inputDirFileName = "G:\\output";
	List<File> fileList = FileListing.getFileListing(new File(inputDirFileName));
	
	int i = 0;
	Set<File> orderedSerFiles = new TreeSet<File>();
	
	for (File file : fileList) {
			orderedSerFiles.add(file);
	}
	
	for (File file : orderedSerFiles){
		String fileName = file.getPath();
		MakeDepReader.main(new String[]{fileName,fileName.replace("dep","rsf")});
	}
	}
}
