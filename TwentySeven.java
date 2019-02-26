import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TwentySeven {

    public static void main(String[] args) {
        if (args.length > 1 || args.length == 0){
            System.out.println("Error, usage: input format:java ClassName input file path.");
            System.exit(1);
        }
        System.out.println("---------------------------------");
        int i = 25;
        for (Map.Entry<String, Integer> entry : count_and_sort(args[0]).entrySet()) {
            if (i == 0) {
                break;
            }
            if (entry.getKey().length() == 1) {
                continue;
            }
            System.out.println(entry.getKey() + "  -  " + entry.getValue());
            i--;
        }
    }

    public static TreeMap<String, Integer> count_and_sort(String filename) {
        Map<String, Integer> word_frequency = new HashMap<String, Integer>();

        for (String word : non_stop_words(filename)) {
            if (word_frequency.get(word) == null){
                word_frequency.put(word, 1);
            } 
            else {
                word_frequency.put(word, word_frequency.get(word) + 1);
            }
        }
        return sorted(word_frequency);
    }

    public static String[] non_stop_words(String filename) {
        Set<String> stop_words = new HashSet<>();
        String stopWords = "";
        try {
          stopWords = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
        } catch (IOException e) {
          e.printStackTrace();
        }

        String[] splitWord = stopWords.split(",");
        for (String word : splitWord){
            stop_words.add(word);
        }

        List<String> nonStopWordList = new ArrayList<>();
        for (String word : all_words(filename)) {
            if (!stop_words.contains(word)) {
                nonStopWordList.add(word);
            }
        }

        String[] translate = new String[nonStopWordList.size()];
        for (int i = 0; i < nonStopWordList.size(); i++) {
            translate[i] = nonStopWordList.get(i);
        }
        return translate;
    }

    public static String[] all_words(String filename) {
        ArrayList<String> word = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine())!= null) {
                for (String s : transform(line)) {
                    if (s.equals("")) {
                        continue;
                    }
                    word.add(s);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] translate = new String[word.size()];
        for (int i = 0; i < translate.length; i++) {
            translate[i] = word.get(i);
        }
        return translate;
    }
    //avoid memory porblem
    public static String[] transform(String line) {
        line = line.replaceAll("[^a-zA-Z0-9\\s+]", " ").toLowerCase();
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