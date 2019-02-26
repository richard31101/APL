import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TwentyFour {

    public static void main(String[] args) {
        // write your code here
        TFQuarantine tfq = new TFQuarantine(get_input._f(args));
        tfq.bind("extract_words");
        tfq.bind("remove_stop_words");
        tfq.bind("frequencies");
        tfq.bind("sort");
        tfq.bind("top25_freqs");
        tfq.execute();
    }
}

class TFQuarantine {
    String path;
    String[] word_list;
    String[] after_remmoval;
    Map<String, Integer> frequency;
    TreeMap<String, Integer> sorted_frequency;

    ArrayList<String> funcs = new ArrayList<>();

    public TFQuarantine(String path) {
        this.path = path;
    }

    public void bind(String func) {
        funcs.add(func);
    }

    public void execute() {
        for (String func : funcs) {
            if (func.equals("extract_words")) {
                word_list = extract_words._f(path);
            } else if (func.equals("remove_stop_words")) {
                after_remmoval = remove_stop_words._f(word_list);
            } else if (func.equals("frequencies")) {
                frequency = frequencies._f(after_remmoval);
            } else if (func.equals("sort")) {
                sorted_frequency = sort._f(frequency);
            } else {
                top25_freqs._f(sorted_frequency);
            }
        }
    }
}

class get_input {
    public static String _f(String[] args) {
        return args[0];
    }
}

class extract_words {
    public static String[] _f(String path) {
        String book = "";
        try {
          book = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
          e.printStackTrace();
        }

        // filter_chars_and_normalize
        book = book.toLowerCase();
        book = book.replaceAll("[^a-zA-Z0-9\\s+]", " ");
        return book.split("\\s+");
    }
}

class remove_stop_words {
    public static String[] _f(String[] word_list) {
        Set<String> stopWords = new HashSet<>();
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

        List<String> temp = new ArrayList<>();
        for (String word : word_list) {
            if (!stopWords.contains(word)) {
                temp.add(word);
            }
        }

        return temp.toArray(new String[temp.size()]);
    }
}

class frequencies {
    public static Map<String, Integer> _f(String[] word_list) {
        Map<String, Integer> frequency = new HashMap<>();
        for (String word : word_list) {
            if (frequency.containsKey(word)) {
                frequency.put(word, frequency.get(word) + 1);
            } else {
                frequency.put(word, 1);
            }
        }
        return frequency;
    }
}

class sort {
    public static TreeMap<String, Integer> _f(Map<String, Integer> frequency) {
        SortMapByValueComparator bvc = new SortMapByValueComparator(frequency);

        TreeMap<String, Integer> sorted_frequency = new TreeMap<String, Integer>(bvc);
        sorted_frequency.putAll(frequency);
        return sorted_frequency;
    }
}

class top25_freqs {
    public static void _f(TreeMap<String, Integer> sorted_frequency) {
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