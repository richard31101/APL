import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TwentyNine {

    public static void main(String[] args) {
        // write your code here
        Queue<String> word_space = new LinkedList<>();
        Queue<Map<String, Integer>> freq_space = new LinkedList<>();

        Set<String> stopWords = getStopWord();

        String text = "";
        try {
          text = new String(Files.readAllBytes(Paths.get(args[0])));
        } catch (IOException e) {
          e.printStackTrace();
        }

        // filter_chars_and_normalize
        text = text.toLowerCase();
        text = text.replaceAll("[^a-zA-Z0-9\\s+]", " ");
        String[] words = text.split("\\s+");
        for (String word : words) {
            word_space.offer(word);
        }

        List<Worker> arr = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i < 4) {
                arr.add(new Worker("process_words", freq_space, word_space, stopWords));
            }
            if (i == 4) {
                arr.add(new Worker("merge_word_frequency", freq_space, word_space, stopWords));
            }
        }

        for (Worker worker : arr) {
            worker.start();
        }

        for (Worker worker : arr) {
            try {
                worker.join(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        TreeMap<String, Integer> sorted_frequency = Worker.getSortedFrequency();

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

    public static void process_words(Queue<Map<String, Integer>> freq_space, Queue<String> word_space, Set<String> stopWords) {
        Map<String, Integer> freq = new HashMap<>();

        while (!word_space.isEmpty()) {
            String word = word_space.poll();

            if (stopWords.contains(word)) {
                continue;
            }

            if (freq.containsKey(word)) {
                freq.put(word, freq.get(word) + 1);
            } else {
                freq.put(word, 1);
            }
        }

        freq_space.offer(freq);
        return;
    }

    public static HashSet<String> getStopWord() {
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

        return stopWords;
    }
}

class Worker extends Thread {
    private Thread t;
    private String method;
    public static TreeMap<String, Integer> sorted_frequency;
    public Queue<Map<String, Integer>> freq_space; 
    public Queue<String> word_space;
    public Set<String> stopWords;

    public Worker(String method, Queue<Map<String, Integer>> freq_space, Queue<String> word_space, Set<String> stopWords) {
        this.method = method;
        this.freq_space = freq_space;
        this.word_space = word_space;
        this.stopWords = stopWords;
    }

    public static TreeMap<String, Integer> getSortedFrequency() {
        return sorted_frequency;
    }

    public void run() {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                   sleep(5000); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }  
            }
        });

        t1.start();
        try {
            t1.join(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } 

    public void start() {
        if (method.equals("merge_word_frequency")) {
            process_words(freq_space, word_space, stopWords);
        } 
        if (t == null) {
            t = new Thread (this, method);
            t.start();
        }
    }

    public void process_words(Queue<Map<String, Integer>> freq_space, Queue<String> word_space, Set<String> stopWords) {
        Map<String, Integer> freq = new HashMap<>();

        while (!word_space.isEmpty()) {
            String word = word_space.poll();

            if (stopWords.contains(word)) {
                continue;
            }

            if (freq.containsKey(word)) {
                freq.put(word, freq.get(word) + 1);
            } else {
                freq.put(word, 1);
            }
        }

        freq_space.offer(freq);
        _sorted(freq);
        return;
    }

    public void _sorted(Map<String, Integer> dict) {
        SortMapByValueComparator bvc = new SortMapByValueComparator(dict);
        TreeMap<String, Integer> sorted_book = new TreeMap<String, Integer>(bvc);

        sorted_book.putAll(dict);
        sorted_frequency = sorted_book;
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