package DecayMetrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.usc.softarch.arcade.config.Config;
import edu.usc.softarch.arcade.util.FileListing;
import edu.usc.softarch.arcade.util.FileUtil;

public class BatchDecayMetricsAnalyzer {
	static Logger logger = Logger.getLogger(BatchDecayMetricsAnalyzer.class);

	public static void main(String[] args) throws IOException {
		PropertyConfigurator.configure(Config.getLoggingConfigFilename());
		
		String outputFile = args[2];
		// Directory containing all clustered rsf files
		String clustersDir = FileUtil.tildeExpandPath(args[0]);
		
		// Directory containing all deps rsf files
		String depsDir = FileUtil.tildeExpandPath(args[1]);
		
		List<File> clusterFiles = FileListing.getFileListing(new File(clustersDir));
		List<File> depsFiles = FileListing.getFileListing(new File(depsDir));
		
		clusterFiles = FileUtil.sortFileListByVersion(clusterFiles);
		
		Map<String,List<Double>> decayMetrics = new LinkedHashMap<String,List<Double>>();

		String versionSchemeExpr = "[0-9]+\\.[0-9]+(\\.[0-9]+)*";
		List<String> versionVals = new ArrayList<String>();
		List<Double> clusSizeVals = new ArrayList<Double>();
		List<Double> twoWayPairsVals = new ArrayList<Double>();
		for (File clusterFile : clusterFiles) {
			if (clusterFile.getName().endsWith(".rsf")) {
				String clusterVersion = FileUtil.extractVersionFromFilename(versionSchemeExpr, clusterFile.getName());
				versionVals.add(clusterVersion);
				// Identify appropriate deps file version
				for (File depsFile : depsFiles) {
					if (depsFile.getName().endsWith(".rsf")) {
						String depsVersion = FileUtil.extractVersionFromFilename(versionSchemeExpr, depsFile.getName());
						if (clusterVersion.equals(depsVersion)) {
							String[] dmaArgs = {clusterFile.getAbsolutePath(),depsFile.getAbsolutePath()};
							DecayMetricAnalyzer.main(dmaArgs);
							
							List<Double> rciVals = null; 
							if (decayMetrics.get("rci") != null) {
								rciVals = decayMetrics.get("rci");
							}
							else {
								rciVals = new ArrayList<Double>();
							}
							rciVals.add(DecayMetricAnalyzer.rciVal);
							decayMetrics.put("rci", rciVals);
							
							List<Double> twoWayRatios = null;
							if (decayMetrics.get("twoway") != null) {
								twoWayRatios = decayMetrics.get("twoway");
							}
							else {
								twoWayRatios = new ArrayList<Double>();
							}
							twoWayRatios.add(DecayMetricAnalyzer.twoWayPairRatio);
							decayMetrics.put("twoway", twoWayRatios);
							
							List<Double> stabilityVals = null;
							if (decayMetrics.get("stability") != null) {
								stabilityVals = decayMetrics.get("stability");
							}
							else {
								stabilityVals = new ArrayList<Double>();
							}
							stabilityVals.add(DecayMetricAnalyzer.avgStability);
							decayMetrics.put("stability", stabilityVals);
							
							List<Double> mqRatios = null;
							if (decayMetrics.get("mq") != null) {
								mqRatios = decayMetrics.get("mq");
							}
							else {
								mqRatios = new ArrayList<Double>();
							}
							mqRatios.add(DecayMetricAnalyzer.mqRatio);
							decayMetrics.put("mq", mqRatios);
							
							clusSizeVals.add(DecayMetricAnalyzer.numberOfCluster);
							twoWayPairsVals.add(DecayMetricAnalyzer.numberOfTwowayPair);
							
							break;
						}
					}
				}
				
			}
		}
		
		String statResult = "";
		
		for (String key : decayMetrics.keySet()) {
			List<Double> vals = decayMetrics.get(key);
			double[] valArr = ArrayUtils.toPrimitive(vals.toArray(new Double[vals.size()]));
			DescriptiveStatistics stats = new DescriptiveStatistics(valArr);
			String header = "stats for " + key;
			System.out.println(header);
			logger.info(header);
			logger.info(stats);
			System.out.println(stats);
			statResult += header + "\n";
			statResult += stats.toString() + "\n\n";
		}
		
		//print as csv file
		String csv = "";
		System.out.println("version, numOfClusters, numofTwoWayPair, rci, twoway, stabilit, mq,");
		csv += "version, numOfClusters, numofTwoWayPair, rci, twoway, stabilit, mq,\n";
		for (int i = 0; i < versionVals.size(); i++){
			System.out.print(versionVals.get(i)+", ");
			csv += versionVals.get(i)+", ";
			System.out.print(clusSizeVals.get(i)+", ");
			csv += clusSizeVals.get(i)+", ";
			System.out.print(twoWayPairsVals.get(i)+", ");
			csv += twoWayPairsVals.get(i)+", ";
			for (String key : decayMetrics.keySet()) {
				System.out.print(decayMetrics.get(key).get(i)+", ");
				csv += decayMetrics.get(key).get(i)+", ";
			}	
			System.out.println();
			csv += "\n";
		}
		writeToFile(csv + "\n" + statResult, outputFile );
	}
	
	
	private static void writeToFile(String content,String path) throws IOException {
		File file = new File(path);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();

		System.out.println("Done");
	}

}
