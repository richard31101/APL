import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TwentySix {
    public static String[] all_words;
    public static String[] non_stop_words = new String[0];
    public static Set<String> stop_words = new HashSet<>();
    public static Set<String> unique_words = new HashSet<>();
    public static Map<String, Integer> word_frequency = new HashMap<String, Integer>();
    public static TreeMap<String, Integer> sorted_frequencies = new TreeMap<String, Integer>();
    public static String[] all_columns = new String[]{"all_words", "stop_words", "non_stop_words", "unique_words", "counts", "sorted_data"};
    
    public static void main(String[] args) {
        if (args.length > 1 || args.length == 0){
            System.out.println("Error, usage: input format:java ClassName input file path.");
            System.exit(1);
        }
        all_words = all_words(args[0]);
        stop_words = stop_words();
        update();

        int i = 25;
        for (Map.Entry<String, Integer> entry : sorted_frequencies.entrySet()) {
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

    public static void update() {
        for (String c : all_columns){
            if (c.equals("non_stop_words")) {
                non_stop_words();
            } 
            else if (c.equals("unique_words")) {
                unique_words();
            } 
            else if (c.equals("counts")) {
                counts();
            } 
            else if (c.equals("sorted_data")) {
                sorted_data();
            } 
        }
    }
    public static String[] all_words(String path_to_file) {
        String text = "";
        try {
          text = new String(Files.readAllBytes(Paths.get(path_to_file)));
        } catch (IOException e) {
          e.printStackTrace();
        }
        text = text.replaceAll("[^a-zA-Z0-9\\s+]", " ").toLowerCase();
        return text.split("\\s+");
    }

    public static Set<String> stop_words() {
        Set<String> stop_words = new HashSet<>();
        String stopWords = "";
        try {
          stopWords = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
        } catch (IOException e) {
          e.printStackTrace();
        }
        String[] stopWordList = stopWords.split(",");
        for (String s : stopWordList){
            stop_words.add(s);
        }
        return stop_words;
    }

    public static void non_stop_words() {
        ArrayList<String> filter = new ArrayList<>();
        for (String s : all_words) {
            if (!stop_words.contains(s)) {
                filter.add(s);
            }
        }
        String[] noStopWords = new String[filter.size()];
        for (int i = 0; i < filter.size(); i++) {
            noStopWords[i] = filter.get(i);
        }
        non_stop_words = noStopWords;
    }

    public static void unique_words() {
        for (String s : non_stop_words) {
            unique_words.add(s);
        }
    }

    public static void counts() {
        for (String word : non_stop_words) {
            if (word_frequency.get(word) == null){
                word_frequency.put(word, 1);
            } 
            else {
                word_frequency.put(word, word_frequency.get(word) + 1);
            }
        }
    }

    public static void sorted_data() {
        SortMapByValueComparator bvc = new SortMapByValueComparator(word_frequency);
        sorted_frequencies = new TreeMap<String, Integer>(bvc);
        sorted_frequencies.putAll(word_frequency);
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