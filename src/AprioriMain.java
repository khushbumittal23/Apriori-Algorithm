import java.io.File;
import java.io.IOException;
import java.util.*;

public class AprioriMain {
    public HashMap<HashSet<String>, HashSet<Integer>> getFinalMap() {
        
        HashMap<HashSet<String>, HashSet<Integer>> finalMap = new HashMap<>();

        try {
            int support1 = 50;
            HashMap<HashSet<String>, HashSet<Integer>> dataMap = new HashMap<>();
            File f = new File("src/gene_expression.csv");
            ArrayList<String[]> allRows = Utility.parseDataSet(f.getAbsolutePath());
            dataMap = Utility.getDataInMap(allRows);
            dataMap = Utility.getModifiedMapForSingleItem(dataMap, support1);
            HashMap<HashSet<String>, HashSet<Integer>> modifiedMap = null;
            finalMap.putAll(dataMap);

            int count = 0;
            int pairSize = 2;
            do {
                System.out.println("dataMap: " + dataMap.size());
                count += dataMap.size();

                modifiedMap = new HashMap<>();
                ArrayList<HashSet<String>> keyList = new ArrayList<HashSet<String>>(dataMap.keySet());

                for (int i = 0; i < keyList.size(); i++) {
                    HashSet<String> primaryKeySet = new HashSet<>();
                    primaryKeySet = keyList.get(i);

                    for (HashSet<String> broKeySet : keyList.subList(i + 1, keyList.size())) {
                        HashSet<Integer> mergedValuesSet = new HashSet<>();
                        mergedValuesSet.addAll(dataMap.get(primaryKeySet));
                        mergedValuesSet.retainAll(dataMap.get(broKeySet));

                        if (mergedValuesSet.size() >= support1) {
                            HashSet<String> mergedKeySet = new HashSet<>();

                            mergedKeySet.addAll(primaryKeySet);
                            mergedKeySet.addAll(broKeySet);

                            if (mergedKeySet.size() == pairSize) {
                                modifiedMap.put(mergedKeySet, mergedValuesSet);
                            }
                        }
                    }
                }
                dataMap = modifiedMap;
                if (modifiedMap.size() > 0) {
                    finalMap.putAll(modifiedMap);
                }
                pairSize++;
            } while (dataMap.size() >= 1);
            count += dataMap.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return finalMap;
    }
}
