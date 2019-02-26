import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TwentyFour {

    public static void main(String[] args) {
        TFQuarantine tfq = new TFQuarantine(get_input._f(args[0]));
        tfq.bind("extract_words");
        tfq.bind("remove_stop_words");
        tfq.bind("frequencies");
        tfq.bind("sort");
        tfq.bind("top25_freqs");
        tfq.execute();
    }
}

class get_input {
    public static String _f(String path_to_file) {
        return path_to_file;
    }
}

class extract_words {
    public static String[] _f(String path_to_file) {
        String text = "";
        try {
          text = new String(Files.readAllBytes(Paths.get(path_to_file)));
        } catch (IOException e) {
          e.printStackTrace();
        }
        text = text.replaceAll("[^a-zA-Z0-9\\s+]", " ").toLowerCase();
        return text.split("\\s+");
    }
}

class remove_stop_words {
    public static String[] _f(String[] words) {
        Set<String> stopWords = new HashSet<>();
        String stop = "";
        try {
          stop = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
        } catch (IOException e) {
          e.printStackTrace();
        }

        String[] splitword = stop.split(",");
        for (String word : splitword){
            stopWords.add(word);
        }
        
        for (int index = 0; index < words.length; index++){
            if (stopWords.contains(words[index])){
                words[index] = words[index].replace(words[index], "");
            }
        }
        return words;
    }
}

class frequencies {
    public static Map<String, Integer> _f(String[] word_list) {
        Map<String, Integer> _word_freqs = new HashMap<>();
        for (String word : word_list) {
            if (_word_freqs.get(word) == null){
                _word_freqs.put(word, 1);
            } 
            else {
                _word_freqs.put(word, _word_freqs.get(word) + 1);
            }
        }
        return _word_freqs;
    }
}

class sort {
    public static TreeMap<String, Integer> _f(Map<String, Integer> _word_freqs) {
        SortMapByValueComparator bvc = new SortMapByValueComparator(_word_freqs);

        TreeMap<String, Integer> sorted = new TreeMap<String, Integer>(bvc);
        sorted.putAll(_word_freqs);
        return sorted;
    }
}

class top25_freqs {
    public static void _f(TreeMap<String, Integer> sorted) {
        int i = 25;
        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            if (i == 0) {
                break;
            }
            if (entry.getKey().length() <= 1) {
                continue;
            }
            System.out.println(entry.getKey() + "  -  " + entry.getValue());
            i--;
        }
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

class TFQuarantine {
    String path_to_file;
    String[] words;
    String[] afterRemove;
    Map<String, Integer> wordFrequency;
    TreeMap<String, Integer> sorted;

    ArrayList<String> funcs = new ArrayList<>();

    public TFQuarantine(String path_to_file) {
       this.path_to_file = path_to_file;
    }

    public void bind(String func) {
        funcs.add(func);
    }

    public void execute() {
        for (String func : funcs) {
            if (func.equals("extract_words")) {
                words = extract_words._f(path_to_file);
            } 
            else if (func.equals("remove_stop_words")) {
                afterRemove = remove_stop_words._f(words);
            } 
            else if (func.equals("frequencies")) {
                wordFrequency = frequencies._f(afterRemove);
            } 
            else if (func.equals("sort")) {
                sorted = sort._f(wordFrequency);
            } 
            else {
                top25_freqs._f(sorted);
            }
        }
    }
}
