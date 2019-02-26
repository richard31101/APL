import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TwentySeven {

    public static void main(String[] args) {
        // write your code here

        int N = 25;
        for (Map.Entry<String, Integer> entry : count_and_sort(args[0]).entrySet()) {
            if (N == 0) {
                break;
            }
            // skip 's' because elizabeth's -> elizabeth and s, so must skip 's'
            if (entry.getKey().length() == 1) {
                continue;
            }
            System.out.println(entry.getKey() + "  -  " + entry.getValue());
            N--;
        }

    }

    public static TreeMap<String, Integer> count_and_sort(String path) {
        Map<String, Integer> frequency = new HashMap<String, Integer>();

        for (String s : non_stop_words(path)) {
            if (frequency.containsKey(s)) {
                frequency.put(s, frequency.get(s) + 1);
            } else {
                frequency.put(s, 1);
            }
        }

        return sorted(frequency);
    }

    public static String[] non_stop_words(String path) {
        Set<String> stop_words = new HashSet<>();
        String stopWords_str = "";
        try {
          stopWords_str = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
        } catch (IOException e) {
          e.printStackTrace();
        }

        String[] split = stopWords_str.split(",");
        for (String s : split){
            stop_words.add(s);
        }

        List<String> temp = new ArrayList<>();
        for (String s : all_words(path)) {
            if (!stop_words.contains(s)) {
                temp.add(s);
            }
        }

        String[] result = new String[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            result[i] = temp.get(i);
        }

        return result;
    }

    public static String[] all_words(String path) {
        List<String> temp = new ArrayList<>();

        try {
            File file = new File(path);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                
                for (String s : words(line)) {
                    if (s.equals("")) {
                        continue;
                    }
                    temp.add(s);
                }
            }
            
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] result = new String[temp.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = temp.get(i);
        }
        return result;
        
    }

    public static String[] words(String line) {
        line = line.toLowerCase();
        line = line.replaceAll("[^a-zA-Z0-9\\s+]", " ");
        return line.split("\\s+");
    }

    public static TreeMap<String, Integer> sorted(Map<String, Integer> frequency) {
        SortMapByValueComparator bvc = new SortMapByValueComparator(frequency);
        TreeMap<String, Integer> sorted_frequency = new TreeMap<String, Integer>(bvc);
        sorted_frequency.putAll(frequency);
        return sorted_frequency;
    }
}

class SortMapByValueComparator implements Comparator<String>{
 
    HashMap<String, Integer> map = new HashMap<String, Integer>();
 
    public SortMapByValueComparator(Map<String, Integer> map){
        this.map.putAll(map);
    }
 
    @Override
    public int compare(String s1, String s2) {
        if(map.get(s1) >= map.get(s2)){
            return -1;
        }else{
            return 1;
        }   
    }
}