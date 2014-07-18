package RefCrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.usc.softarch.arcade.util.FileListing;

public class RefCrawler {

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		String inputDirFileName = "M:\\Dropbox\\RefCralwer_output";
		List<File> fileList;
		Set<String> types = new HashSet<String>();
		
		//Get the list of refactoring types
		try {
			fileList = FileListing.getFileListing(new File(inputDirFileName));
			int i = 0;
			Set<File> orderedSerFiles = new TreeSet<File>();
			
			for (File file : fileList) {
					orderedSerFiles.add(file);
			}
			
			for (File file : orderedSerFiles){
				String fileName = file.getPath();
			//	System.out.println(fileName);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fileName);
				NodeList nList = doc.getElementsByTagName("refactoring");
				for (int temp = 0; temp < nList.getLength(); temp++) {
					 
					Node nNode = nList.item(temp);
			 
					//System.out.println("\nCurrent Element :" + nNode.getNodeName());
			 
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			 
						Element eElement = (Element) nNode;
			 
						types.add(eElement.getAttribute("name"));
					//	System.out.println("Refactoring type: " + eElement.getAttribute("name"));
//						System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
//						System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
//						System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
//						System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
			 
					}
				}		
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("source_major,source_minor,source_path,target_major,target_minor,target_path");
		for (String type: types){
			System.out.print(","+type);
		}
		System.out.println();
		
		
		// Get the number of problem
		try {
			fileList = FileListing.getFileListing(new File(inputDirFileName));
			int i = 0;
			Set<File> orderedSerFiles = new TreeSet<File>();
			Map<String, Integer> counter = new HashMap<String, Integer>();
			for (File file : fileList) {
					orderedSerFiles.add(file);
			}
			
			for (File file : orderedSerFiles){
				String fileName = file.getPath();
				for(String type: types){
					counter.put(type, 0);
				}
			//	System.out.println(fileName);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fileName);
				NodeList nList = doc.getElementsByTagName("refactoring");
				for (int temp = 0; temp < nList.getLength(); temp++) {
					 
					Node nNode = nList.item(temp);
			 
					//System.out.println("\nCurrent Element :" + nNode.getNodeName());
			 
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			 
						Element eElement = (Element) nNode;
						String tmp = eElement.getAttribute("name");
						int tmpInt = counter.get(tmp);
						tmpInt++;
						counter.put(tmp, tmpInt);
					//	System.out.println("Refactoring type: " + eElement.getAttribute("name"));
//						System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
//						System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
//						System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
//						System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
			 
					}
				}
				// Print out
				String[] temp = fileName.split("[\\\\|\\\\.j-]");
				//  System.out.print(temp[3]+","+temp[4]);
					System.out.print(temp[4]+","+temp[5]+","+temp[6]+","+temp[8]+","+temp[9]+","+temp[10]);
				for(String type: types){
					System.out.print(","+counter.get(type));
				}
				System.out.println();
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
