package malletComparison;

import java.util.HashMap;
import java.util.Map;

public class Topic {
	public int id;
	public Map<String,Double> wordWeights = new HashMap<String,Double>();
	public Map<String,Double> wordNormWeights = new HashMap<String,Double>();
	public double minNormWeight = -1;

}
