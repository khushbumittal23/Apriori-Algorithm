/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;

public class AssociationRules {

    static double confidence = 0.6;
    static HashMap<HashSet<String>, HashSet<Integer>> finalMap;
    static HashMap<String, String> allRules = new HashMap<>();

    public static void main(String[] args) {

        AprioriMain am = new AprioriMain();

        finalMap = am.getFinalMap();
//        System.out.println(finalMap.size());
        generateAllRules();

        HashSet<String> items = new HashSet<>(); // ItemSet
        items.add("Gene1_UP");
//        items.add("Gene6_UP");
//        items.add("Gene8_UP");
        items.add("Gene10_Down");
//        items.add("Gene59_UP");
//        items.add("Gene72_UP");
//        items.add("Gene96_Down");
        String type = "one"; // any , one , none
        String rType = "B"; // R = Rule , B = Body , H = Head
        int ans = runQuery(items, type, rType);
        System.out.println("QueryResult:"+ans);
        
        // Template 2 Size
        int size = 3; // Query size >=
        int ans1 = typeSize(size, "R"); // R = Rule , B = Body , H = Head
        System.out.println("Size:"+ans1);
    }
    
    
    private static void generateAllRules(){
        
        for (Map.Entry<HashSet<String>, HashSet<Integer>> e : finalMap.entrySet()) {

            HashSet<String> key = e.getKey();
            HashSet<Integer> value = e.getValue();
            int keySize = key.size();
            if (keySize > 1) {
                HashMap<String, String> tempRules = new HashMap<>();
                if (keySize == 2) {
                    tempRules = genRulesForTwoItems(key, value);
                    allRules.putAll(tempRules);
                } else if (keySize == 3) {
                    tempRules = genRulesForThreeItems(key, value);;
                    allRules.putAll(tempRules);
                }

            }

        }
        
        System.out.println("AllRules:"+allRules);
        
    }
    
    
    private static HashMap<String, String> genRulesForTwoItems(HashSet<String> key, HashSet<Integer> value) {

        HashMap<String, String> res = new HashMap<>();
        Integer supportNumer = finalMap.get(key).size();

        ArrayList<String> list = new ArrayList(key);
        HashSet<String> ele1 = new HashSet<>();
        ele1.add(list.get(0));

        // First Key as Body
        Integer supportDenom1 = finalMap.get(ele1).size();
        double c1 = ((double) supportNumer / supportDenom1);
        if (c1 >= confidence) {
            res.put(list.get(0) + "->" + list.get(1), "11");
        }

        // Second Key as Body
        HashSet<String> ele2 = new HashSet<>();
        ele2.add(list.get(1));
        Integer supportDenom2 = finalMap.get(ele2).size();
        double c2 = ((double) supportNumer / supportDenom2);
        if (c2 >= confidence) {
            res.put(list.get(1) + "->" + list.get(0), "11");
        }

        return res;

    }

    private static HashMap<String, String> genRulesForThreeItems(HashSet<String> key, HashSet<Integer> value) {

        HashMap<String, String> res = new HashMap<>();
        String[] keys = key.toArray(new String[key.size()]);
        int len = keys.length;

        int supportNum = value.size();
        int supportDenom;
        double c;
        for (int i = 0; i < len; i++) {

            // 1 -> 2 rule
            HashSet<String> tempSet = new HashSet<>();
            tempSet.add(keys[i]);
            supportDenom = finalMap.get(tempSet).size();
            c = (double) supportNum / supportDenom;
            if (c >= confidence) {
                res.put(keys[i] + "->" + (keys[(i + 1) % len] + "," + keys[(i + 2) % len]), "12");
            }

            // 1 -> 2 rule
            HashSet<String> tempSet2 = new HashSet<>();
            tempSet2.add(keys[i]);
            tempSet2.add(keys[(i + 1) % 2]);
            supportDenom = finalMap.get(tempSet2).size();
            c = (double) supportNum / supportDenom;
            if (c >= confidence) {
                res.put((keys[i] + "," + keys[(i + 1) % len]) + "->" + keys[(i + 2) % len], "21");
            }

        }
        return res;
    }

    private static Integer runQuery(HashSet<String> items, String type, String ruleType) {
        Integer ans = 0;

        // B = Body , H = Head , R = Rule
        switch (type) {
            case "any":
                ans = typeAny(items, ruleType);
                // Statements
                break; // optional

            case "one":
                ans = typeOne(items, ruleType);
                // Statements
                break; // optional

            case "none":
                ans = typeNone(items, ruleType);
                // Statements
                break; // optional

            // You can have any number of case statements.
            default: // Optional
            // Statements
        }

        return ans;

    }

    private static Integer typeAny(HashSet<String> items, String ruleType) {

        HashMap<String, String> res = new HashMap<>();
        for (Map.Entry<String, String> e : allRules.entrySet()) {

            // 1->1 relation i.e A->B or B->C
            if (e.getValue().equals("11")) {
                HashSet<String> tempSet = new HashSet<>();
                String[] temp = e.getKey().split("->");
                if (ruleType.equals("B")) {
                    Collections.addAll(tempSet, temp[0]);
                } else if (ruleType.equals("H")) {
                    Collections.addAll(tempSet, temp[1]);
                } else {
                    Collections.addAll(tempSet, temp);
                }
                HashSet<String> intersection = new HashSet<>(tempSet);
                intersection.retainAll(items);
                if (intersection.size() > 0) {
                    res.put(e.getKey(), e.getValue());
                }

            } else if (e.getValue().equals("12")) {
                HashSet<String> tempSet = new HashSet<>();
                String[] temp = e.getKey().split("->");
//                System.out.println(e.getKey());
                String[] temp1 = temp[1].split(",");
                String[] ans = new String[3];
                if (ruleType.equals("B")) {
                    ans[0] = temp[0];
                } else if (ruleType.equals("H")) {
                    ans[0] = temp1[0];
                    ans[1] = temp1[1];
                } else {
                    ans[0] = temp[0];
                    ans[1] = temp1[0];
                    ans[2] = temp1[1];
                }

                Collections.addAll(tempSet, ans);
                HashSet<String> intersection = new HashSet<>(tempSet);
                intersection.retainAll(items);
                if (intersection.size() > 0) {
                    res.put(e.getKey(), e.getValue());
                }

            } else if (e.getValue().equals("21")) {
                HashSet<String> tempSet = new HashSet<>();
                String[] temp = e.getKey().split("->");
                String[] temp1 = temp[0].split(",");
                String[] ans = new String[3];
                if (ruleType.equals("B")) {
                    ans[0] = temp1[0];
                    ans[1] = temp1[1];
                } else if (ruleType.equals("H")) {
                    ans[0] = temp[1];
                } else {
                    ans[0] = temp[1];
                    ans[1] = temp1[0];
                    ans[2] = temp1[1];
                }
                Collections.addAll(tempSet, ans);
                HashSet<String> intersection = new HashSet<>(tempSet);
                intersection.retainAll(items);
                if (intersection.size() > 0) {
                    res.put(e.getKey(), e.getValue());
                }

            }

        }
        System.out.println("TypeAny:"+res);
        return res.size();

    }

    private static Integer typeOne(HashSet<String> items, String ruleType) {

        HashMap<String, String> res = new HashMap<>();
        for (Map.Entry<String, String> e : allRules.entrySet()) {

            // 1->1 relation i.e A->B or B->C
            if (e.getValue().equals("11")) {
                HashSet<String> tempSet = new HashSet<>();
                String[] temp = e.getKey().split("->");
                if (ruleType.equals("B")) {
                    Collections.addAll(tempSet, temp[0]);
                } else if (ruleType.equals("H")) {
                    Collections.addAll(tempSet, temp[1]);
                } else {
                    Collections.addAll(tempSet, temp);
                }
                HashSet<String> intersection = new HashSet<>(tempSet);
                intersection.retainAll(items);
                if (intersection.size() == 1) {
                    res.put(e.getKey(), e.getValue());
                }

            } else if (e.getValue().equals("12")) {
                HashSet<String> tempSet = new HashSet<>();
                String[] temp = e.getKey().split("->");
//                System.out.println(e.getKey());
                String[] temp1 = temp[1].split(",");
                String[] ans = new String[3];
                if (ruleType.equals("B")) {
                    ans[0] = temp[0];
                } else if (ruleType.equals("H")) {
                    ans[0] = temp1[0];
                    ans[1] = temp1[1];
                } else {
                    ans[0] = temp[0];
                    ans[1] = temp1[0];
                    ans[2] = temp1[1];
                }

                Collections.addAll(tempSet, ans);
                HashSet<String> intersection = new HashSet<>(tempSet);
                intersection.retainAll(items);
                if (intersection.size() == 1) {
                    res.put(e.getKey(), e.getValue());
                }

            } else if (e.getValue().equals("21")) {
                HashSet<String> tempSet = new HashSet<>();
                String[] temp = e.getKey().split("->");
                String[] temp1 = temp[0].split(",");
                String[] ans = new String[3];
                if (ruleType.equals("B")) {
                    ans[0] = temp1[0];
                    ans[1] = temp1[1];
                } else if (ruleType.equals("H")) {
                    ans[0] = temp[1];
                } else {
                    ans[0] = temp[1];
                    ans[1] = temp1[0];
                    ans[2] = temp1[1];
                }
                Collections.addAll(tempSet, ans);
                HashSet<String> intersection = new HashSet<>(tempSet);
                intersection.retainAll(items);
                if (intersection.size() == 1) {
                    res.put(e.getKey(), e.getValue());
                }

            }

        }
        System.out.println("TypeOne:"+res);
        return res.size();

    }

    private static Integer typeNone(HashSet<String> items, String ruleType) {
        HashMap<String, String> res = new HashMap<>();
        for (Map.Entry<String, String> e : allRules.entrySet()) {

            // 1->1 relation i.e A->B or B->C
            if (e.getValue().equals("11")) {
                HashSet<String> tempSet = new HashSet<>();
                String[] temp = e.getKey().split("->");
                if (ruleType.equals("B")) {
                    Collections.addAll(tempSet, temp[1]);
                } else if (ruleType.equals("H")) {
                    Collections.addAll(tempSet, temp[0]);
                } else {
                    Collections.addAll(tempSet, temp);
                }
                HashSet<String> intersection = new HashSet<>(tempSet);
                intersection.retainAll(items);
                if (intersection.isEmpty()) {
                    res.put(e.getKey(), e.getValue());
                }

            } else if (e.getValue().equals("12")) {
                HashSet<String> tempSet = new HashSet<>();
                String[] temp = e.getKey().split("->");
//                System.out.println(e.getKey());
                String[] temp1 = temp[1].split(",");
                String[] ans = new String[3];
                if (ruleType.equals("B")) {
                    ans[0] = temp1[0];
                    ans[1] = temp1[1];
                } else if (ruleType.equals("H")) {
                    ans[0] = temp[0];
                } else {
                    ans[0] = temp[0];
                    ans[1] = temp1[0];
                    ans[2] = temp1[1];
                }

                Collections.addAll(tempSet, ans);
                HashSet<String> intersection = new HashSet<>(tempSet);
                intersection.retainAll(items);
                if (intersection.isEmpty()) {
                    res.put(e.getKey(), e.getValue());
                }

            } else if (e.getValue().equals("21")) {
                HashSet<String> tempSet = new HashSet<>();
                String[] temp = e.getKey().split("->");
                String[] temp1 = temp[0].split(",");
                String[] ans = new String[3];
                if (ruleType.equals("B")) {
                    ans[0] = temp[1];
                } else if (ruleType.equals("H")) {
                    ans[0] = temp1[0];
                    ans[1] = temp1[1];
                } else {
                    ans[0] = temp[1];
                    ans[1] = temp1[0];
                    ans[2] = temp1[1];
                }
                Collections.addAll(tempSet, ans);
                HashSet<String> intersection = new HashSet<>(tempSet);
                intersection.retainAll(items);
                if (intersection.isEmpty()) {
                    res.put(e.getKey(), e.getValue());
                }

            }

        }
        System.out.println("TypeNone:"+res);
        return res.size();

    }

    private static Integer typeSize(Integer size, String ruleType) {

        HashMap<String, String> res = new HashMap<>();
        for (Map.Entry<String, String> e : allRules.entrySet()) {

            if (e.getValue().equals("11")) {
                HashSet<String> tempSet = new HashSet<>();
                String[] temp = e.getKey().split("->");
                if (ruleType.equals("B")) {
                    Collections.addAll(tempSet, temp[0]);
                } else if (ruleType.equals("H")) {
                    Collections.addAll(tempSet, temp[1]);
                } else {
                    Collections.addAll(tempSet, temp);
                }
                if (tempSet.size() >= (size+1)) {
                    res.put(e.getKey(), e.getValue());
                }

            } else if (e.getValue().equals("12")) {
                HashSet<String> tempSet = new HashSet<>();
                String[] temp = e.getKey().split("->");
//                System.out.println(e.getKey());
                String[] temp1 = temp[1].split(",");
                String[] ans;
                if (ruleType.equals("B")) {
                    ans = new String[1];
                    ans[0] = temp[0];
                } else if (ruleType.equals("H")) {
                    ans = new String[2];
                    ans[0] = temp1[0];
                    ans[1] = temp1[1];
                } else {
                    ans = new String[3];
                    ans[0] = temp[0];
                    ans[1] = temp1[0];
                    ans[2] = temp1[1];
                }

                Collections.addAll(tempSet, ans);
                if (tempSet.size() >= (size)) {
                    res.put(e.getKey(), e.getValue());
                }

            } else if (e.getValue().equals("21")) {
                HashSet<String> tempSet = new HashSet<>();
                String[] temp = e.getKey().split("->");
                String[] temp1 = temp[0].split(",");
                String[] ans = new String[3];
                if (ruleType.equals("B")) {
                    ans = new String[2];
                    ans[0] = temp1[0];
                    ans[1] = temp1[1];
                } else if (ruleType.equals("H")) {
                    ans = new String[1];
                    ans[0] = temp[1];
                } else {
                    ans = new String[3];
                    ans[0] = temp[1];
                    ans[1] = temp1[0];
                    ans[2] = temp1[1];
                }
                Collections.addAll(tempSet, ans);
                if (tempSet.size() >= (size)) {
                    res.put(e.getKey(), e.getValue());
                }

            }
        }
        System.out.println("TypeSize:"+res);
        return res.size();
    }

}
