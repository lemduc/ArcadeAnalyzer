package weka;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Sets;

public class createCompostions {

	public static int TOTAL_SYSTEMS = 3;
	public static int SUB_SET = 2;
	public static List<HashSet<Integer>> output;
	
	public static void main(String[] args) {
		// Print the list of compostion
		HashSet<Integer> values = new HashSet<Integer>();
		
		output = new LinkedList<HashSet<Integer>>();
		returnHashset(0, values	, output);
		
		System.out.println(output.size());	
	}
	
	public static void returnHashset(int cur, HashSet<Integer> currentSet, List<HashSet<Integer>> output) {
		if (SUB_SET == currentSet.size())
		{
			HashSet<Integer> newset = Sets.newHashSet(currentSet);
			output.add(newset);
			print(newset);
			return;
		}
		
		if (TOTAL_SYSTEMS - cur < SUB_SET - currentSet.size())
		{
			return;
		}
		for (int i = cur; TOTAL_SYSTEMS - i >= SUB_SET - currentSet.size(); i++) {
			//System.out.println(i);
			currentSet.add(i);
			returnHashset(i + 1, currentSet, output);
			currentSet.remove(i);
		}
	}
	
	public static void print(HashSet<Integer> input) {
		for (int i = 0; i < input.size(); i++)
			System.out.print(input.toArray()[i] + ",");
		System.out.println();
	}
}
