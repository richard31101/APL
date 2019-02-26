import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TwentySix {
    public static String all_words;
    public static Set<String> stop_words = new HashSet<>();
    public static String[] all_columns = new String[]{"all_words", "stop_words", "get_non_stop_words", "get_unique_words", "get_counts", "get_sorted_data"};
    public static String[] non_stop_words = new String[0];
    public static Set<String> unique_words = new HashSet<>();
    public static Map<String, Integer> frequency = new HashMap<String, Integer>();
    public static TreeMap<String, Integer> sorted_frequency = new TreeMap<String, Integer>();

    public static void main(String[] args) {
        // write your code here
        all_words = get_all_words(args[0]);
        stop_words = get_stop_words();
        update();

        int N = 25;
        for (Map.Entry<String, Integer> entry : sorted_frequency.entrySet()) {
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

    public static void update() {
        for (String s : all_columns) {
            if (s.equals("get_non_stop_words")) {
                get_non_stop_words();
            } else if (s.equals("get_unique_words")) {
                get_unique_words();
            } else if (s.equals("get_counts")) {
                get_counts();
            } else if (s.equals("get_sorted_data")) {
                get_sorted_data();
            } 
        }
    }
    public static String get_all_words(String path) {
        String book = "";
        try {
          book = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
          e.printStackTrace();
        }

        // filter_chars_and_normalize
        book = book.toLowerCase();
        book = book.replaceAll("[^a-zA-Z0-9\\s+]", " ");
        return book;
    }

    public static Set<String> get_stop_words() {
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
        return stop_words;
    }

    public static void get_non_stop_words() {
        String[] words = all_words.split("\\s+");

        List<String> temp = new ArrayList<>();
        for (String s : words) {
            if (!stop_words.contains(s)) {
                temp.add(s);
            }
        }

        String[] result = new String[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            result[i] = temp.get(i);
        }

        non_stop_words = result;

        return;
    }

    public static void get_unique_words() {
        for (String s : non_stop_words) {
            unique_words.add(s);
        }
        return;
    }

    public static void get_counts() {
        for (String s : non_stop_words) {
            if (frequency.containsKey(s)) {
                frequency.put(s, frequency.get(s) + 1);
            } else {
                frequency.put(s, 1);
            }
        }
    }

    public static void get_sorted_data() {
        SortMapByValueComparator bvc = new SortMapByValueComparator(frequency);
        sorted_frequency = new TreeMap<String, Integer>(bvc);
        sorted_frequency.putAll(frequency);
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