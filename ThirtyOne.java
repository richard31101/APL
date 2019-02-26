import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ThirtyOne {

    public static void main(String[] args) {
        // write your code here
        List<String> contents = partition(args[0], 200);
        List<String> split_contents = split_words(contents); 
        List<List<String>> splits_per_word = regroup(split_contents);
        TreeMap<String, Integer> sorted_frequency = sort(splits_per_word);

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

    public static TreeMap<String, Integer> sort(List<List<String>> splits_per_word) {
        Map<String, Integer> freq = new HashMap<>();

        for (List<String> group : splits_per_word) {
            for (String word : group) {
                if (freq.containsKey(word)) {
                    freq.put(word, freq.get(word) + 1);
                } else {
                    freq.put(word, 1);
                }
            }
        }

        SortMapByValueComparator bvc = new SortMapByValueComparator(freq);
        TreeMap<String, Integer> sorted_book = new TreeMap<String, Integer>(bvc);

        sorted_book.putAll(freq);
        return sorted_book;
    }

    public static List<List<String>> regroup(List<String> split_contents) {
        List<List<String>> result = new ArrayList<>();
        List<String> ae = new ArrayList<>();
        List<String> fj = new ArrayList<>();
        List<String> ko = new ArrayList<>();
        List<String> pt = new ArrayList<>();
        List<String> uz = new ArrayList<>();

        for (String word : split_contents) {
            word = word.toLowerCase();
            word = word.replaceAll("[^a-zA-Z0-9\\s+]", " ");
            if (word.length() == 0) {
                continue;
            }
            char c = word.charAt(0);
            if (c >= 'a' && c <= 'e') {
                ae.add(word);
            } else if (c >= 'f' && c <= 'j') {
                fj.add(word);
            } else if (c >= 'k' && c <= 'o') {
                ko.add(word);
            } else if (c >= 'p' && c <= 't') {
                pt.add(word);
            } else {
                uz.add(word);
            }
        }

        result.add(ae);
        result.add(fj);
        result.add(ko);
        result.add(pt);
        result.add(uz);

        return result;
    }

    public static List<String> split_words(List<String> contents) {
        List<String> result = new ArrayList<>();

        HashSet<String> stopWords = new HashSet<>();

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

        for (String text : contents) {
            text = text.toLowerCase();
            text = text.replaceAll("[^a-zA-Z0-9\\s+]", " ");
            String[] words = text.split("\\s+");

            for (String word : words) {
                if (!stopWords.contains(word)) {
                    result.add(word);
                }
            }
        }

        return result;
    }

    public static List<String> partition(String path, int numLines) {
        List<String> result = new ArrayList<>();

        Scanner sc = null;
        try {
            sc = new Scanner(new File(path));
            int count = 0;
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine());
                sb.append('\n');
                if (count % numLines == 0) {
                    result.add(sb.toString());
                    sb = new StringBuilder();
                    count++;
                }
            }

            if (sb.length() != 0) {
                result.add(sb.toString());
            }
            sc = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
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