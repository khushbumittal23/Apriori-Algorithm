import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import com.opencsv.CSVReader;

public class Utility {

	public static ArrayList<String[]> parseDataSet(String fileLocation) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(fileLocation), ',', '"', 0);
		ArrayList<String[]> allRows = (ArrayList<String[]>) reader.readAll();
		return allRows;
	}

	public static HashMap<HashSet<String>, HashSet<Integer>> getDataInMap(ArrayList<String[]> allRows) {
		HashMap<HashSet<String>, HashSet<Integer>> map = new HashMap<>(100);
		int length = allRows.size();
		for (int index = 0; index < length; index++) {
			String row[] = allRows.get(index);
			int rowLength = row.length;
			for (int column = 1; column < rowLength; column++) {
				String key = "Gene" + column + "_" + row[column];
				HashSet<String> keySet = new HashSet<>();
				keySet.add(key);
				if (map.containsKey(keySet)) {
					HashSet<Integer> sampleSet = map.get(keySet);
					sampleSet.add(index);
					map.put(keySet, sampleSet);
				} else {
					HashSet<Integer> sampleSet = new HashSet<Integer>();
					sampleSet.add(index);
					map.put(keySet, sampleSet);
				}
			}
		}
		return map;
	}
	
	public static HashMap<HashSet<String>, HashSet<Integer>> getModifiedMapForSingleItem(HashMap<HashSet<String>, HashSet<Integer>> map, int support) {
		HashMap<HashSet<String>, HashSet<Integer>> modifiedMap = new HashMap<>();
		
		map.entrySet().stream().filter(entry -> entry.getValue().size() >= support)
				.forEach(entry -> modifiedMap.put(entry.getKey(), entry.getValue()));
		return modifiedMap;
	}
}
