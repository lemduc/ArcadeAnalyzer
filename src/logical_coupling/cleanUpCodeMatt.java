package logical_coupling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class cleanUpCodeMatt {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		String inputDirFilename = args[0];
		File folder = new File(inputDirFilename);
		File[] files = folder.listFiles();
				
		for (File file : files) {
			if (file.isFile()){
			String sourceFile = file.getName();
			single(sourceFile, inputDirFilename);
			}
		}
		
	
	}

	private static void single(String sourceFile, String folder) {
		
		String targetFile = folder + "\\clean\\" + sourceFile.split("\\.")[0] + "_clean.csv";
		String output;
		try (BufferedReader br = new BufferedReader(new FileReader(folder + "\\" + sourceFile)))
		{
			String sCurrentLine;
			// skip the first line
			sCurrentLine = br.readLine();
			output = sCurrentLine ;
			while ((sCurrentLine = br.readLine()) != null) {
					String[] temp = sCurrentLine.split(",");
					if (temp[0].endsWith(".java")||temp[1].endsWith(".java")){
					String class1 = convertFile(temp[0]);
					String class2 = convertFile(temp[1]);
					if (class1 != null & class2 != null){
						output += "\n" + class1 + "," + class2 + "," + temp[2] + "," + temp[3];
					}
				}
			}
			
			PrintWriter out = new PrintWriter(targetFile);
			out.println(output);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String convertFile(String javaName){
		String output = javaName;
		try {
			output = "org.apache." + output.split("java\\/org\\/apache\\/")[1].replace("/", ".").replace(".java", "");
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
		return output;
	}
	
}
