package CodeSmellAnalyzer;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

public class JsonCounter {
	public static void main(String[] args) throws JsonParseException, IOException{
		String testInput = "F:\\code_smell\\output-apache-activemq\\apache-activemq- (tag_ activemq-4.0)-anomalies.json";
		File file = new File(testInput);
		
		JsonFactory jsonFactory = new JsonFactory(); // or, for data binding, org.codehaus.jackson.mapper.MappingJsonFactory 
		JsonParser jp = jsonFactory.createJsonParser(file); // or URL, Stream, Reader, String, byte[]
		
		jp.nextToken();
	}
}
