import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Twelve {

    public static void main(String[] args) {
        // write your code here
        Map<String, Rapper> map = new HashMap<>();
        map.put("data_storage_obj", new Rapper("data_storage_obj"));
        map.put("stop_words_obj", new Rapper("stop_words_obj"));
        map.put("word_freqs_obj", new Rapper("word_freqs_obj"));
        
        map.get("data_storage_obj").data_storage_obj.init(args[0]);
        map.get("stop_words_obj").stop_words_obj.init();

        for (String word : map.get("data_storage_obj").data_storage_obj._words()) {
            if (!map.get("stop_words_obj").stop_words_obj._is_stop_word(word)) {
                map.get("word_freqs_obj").word_freqs_obj._increment_count(word);
            }
        }

        TreeMap<String, Integer> word_freqs = map.get("word_freqs_obj").word_freqs_obj._sorted();
        top25(word_freqs); 
        
    }

    public static void top25(TreeMap<String, Integer> word_freqs) {
        int N = 25;
        for (Map.Entry<String, Integer> entry : word_freqs.entrySet()) {
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
}

class Rapper {
    DataStorageManagerTwelve data_storage_obj;
    StopWordManagerTwelve stop_words_obj;
    WordFrequencyManagerTwelve word_freqs_obj;

    Rapper(String s) {
        if (s.equals("data_storage_obj")) {
            data_storage_obj = new DataStorageManagerTwelve();
        } else if (s.equals("stop_words_obj")) {
            stop_words_obj = new StopWordManagerTwelve();
        } else {
            word_freqs_obj = new WordFrequencyManagerTwelve();
        }
    }
}

class DataStorageManagerTwelve {
    public String[] words;
    public String text;

    public void init(String path) {
        // read_file
        try {
          text = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
          e.printStackTrace();
        }

        // filter_chars_and_normalize
        text = text.toLowerCase();
        text = text.replaceAll("[^a-zA-Z0-9\\s+]", " ");
        
    }

    public String[] _words() {
        // scan
        words = text.split("\\s+");
        return words;
    }
}

class StopWordManagerTwelve {
    public Set<String> stopWords = new HashSet<>();

    // remove_stop_words
    public void init() {
        String stopWords_str = "";
        try {
          stopWords_str = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
        } catch (IOException e) {
          e.printStackTrace();
        }

        String[] split = stopWords_str.split(",");
        for (String s : split){
            stopWords.add(s);
        }
    }

    public boolean _is_stop_word(String word) {
        return stopWords.contains(word);
    }
}

class WordFrequencyManagerTwelve {
    public Map<String, Integer> dict = new HashMap<String, Integer>();

    // frequencies
    public void _increment_count(String word) {
        if (dict.get(word) == null){
            dict.put(word, 1);
        } else {
            dict.put(word, dict.get(word) + 1);
        }
    }

    public TreeMap<String, Integer> _sorted() {
        SortMapByValueComparator bvc = new SortMapByValueComparator(dict);
        TreeMap<String, Integer> sorted_book = new TreeMap<String, Integer>(bvc);

        sorted_book.putAll(dict);
        return sorted_book;
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