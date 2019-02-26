import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TwentyEightThree {

    public static void main(String[] args) {
        // write your code here
        Queue<String> queue = new LinkedList<>();
        WordFrequencyManagerThree wordFrequencyManager = new WordFrequencyManagerThree();
        
        StopWordManagerThree stopWordManager = new StopWordManagerThree();
        send(queue, stopWordManager, wordFrequencyManager);

        DataStorageManagerThree dataStorageManager = new DataStorageManagerThree();
        send(queue, dataStorageManager, stopWordManager);
        
        WordFrequencyControllerThree wfcontroller = new WordFrequencyControllerThree();
        send(queue, wfcontroller, dataStorageManager);

        wfcontroller._init(args[0], queue);
        TreeMap<String, Integer> sorted_frequency = wfcontroller.run(queue);

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

    public static void send(Queue<String> queue, StopWordManagerThree swm, WordFrequencyManagerThree wfm) {
        queue.offer("swm");
        queue.offer("wfm");
    }

    public static void send(Queue<String> queue, DataStorageManagerThree dsm, StopWordManagerThree swm) {
        queue.offer("dsm");
        queue.offer("swm");
    }

    public static void send(Queue<String> queue, WordFrequencyControllerThree wfc, DataStorageManagerThree dsm) {
        queue.offer("wfc");
        queue.offer("dsm");
    }
}

class DataStorageManagerThree {
    public String[] words;
    public String text;

    public DataStorageManagerThree() {

    }

    public DataStorageManagerThree(String path) {
        // read_file
        try {
          text = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
          e.printStackTrace();
        }

        // filter_chars_and_normalize
        text = text.toLowerCase();
        text = text.replaceAll("[^a-zA-Z0-9\\s+]", " ");
        words = text.split("\\s+");
    }

    public String[] getEachWord() {
        return words;
    }

    public void run() {
        return;
    }
}

class StopWordManagerThree {
    public Set<String> stopWords = new HashSet<>();

    public StopWordManagerThree() {
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

    public void run() {
        return;
    }

    public boolean contains(String word) {
        return stopWords.contains(word);
    }
}

class WordFrequencyManagerThree {
    public Map<String, Integer> dict = new HashMap<String, Integer>();

    // frequencies
    public void addFrequency(String word) {
        if (dict.get(word) == null){
            dict.put(word, 1);
        } else {
            dict.put(word, dict.get(word) + 1);
        }
    }

    public void run() {
        return;
    }

    public TreeMap<String, Integer> getSortedMap() {
        SortMapByValueComparator bvc = new SortMapByValueComparator(dict);
        TreeMap<String, Integer> sorted_book = new TreeMap<String, Integer>(bvc);

        sorted_book.putAll(dict);
        return sorted_book;
    }
    
}

class WordFrequencyControllerThree {
    DataStorageManagerThree _storage_manager;
    StopWordManagerThree _stop_word_manager;
    WordFrequencyManagerThree _word_freq_manager;

    ThreadManager thread_storage_manager;
    ThreadManager thread_stop_word_manager; 
    ThreadManager thread_word_freq_manager;


    public TreeMap<String, Integer> run(Queue<String> queue) {
        if (queue.isEmpty()) {
            return null;
        }

        thread_storage_manager.start();
        thread_stop_word_manager.start();
        thread_word_freq_manager.start();
        return this._run();
    }

    public void _init(String path_to_file, Queue<String> queue) {
        _storage_manager = new DataStorageManagerThree(path_to_file);
        _stop_word_manager = new StopWordManagerThree();
        _word_freq_manager = new WordFrequencyManagerThree();

        thread_storage_manager = new ThreadManager( "_storage_manager");
        thread_stop_word_manager = new ThreadManager( "_stop_word_manager");
        thread_word_freq_manager = new ThreadManager( "_word_freq_manager");
    }

    public TreeMap<String, Integer> _run() {
        for (String word : _storage_manager.getEachWord()) {
            if (!_stop_word_manager.contains(word)) {
                _word_freq_manager.addFrequency(word);
            }
        }

        TreeMap<String, Integer> sorted_frequency = _word_freq_manager.getSortedMap();
        
        return sorted_frequency;
    }
}

class ThreadManagerThree extends Thread {
    private Thread t;
    
    private String threadName;
    public DataStorageManagerThree _storage_manager;
    public StopWordManagerThree _stop_word_manager;
    public WordFrequencyManagerThree _word_freq_manager;

    public ThreadManagerThree(String name) {
        threadName = name;
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
        
        if (threadName.equals("_storage_manager")) {
            _storage_manager = new DataStorageManagerThree();
            _storage_manager.run();
        } else if (threadName.equals("_stop_word_manager")) {
            _stop_word_manager = new StopWordManagerThree();
            _stop_word_manager.run();
        } else {
            _word_freq_manager = new WordFrequencyManagerThree();
            _word_freq_manager.run();
        }
    } 

    public void start () {
        if (t == null) {
            t = new Thread (this, threadName);
            t.start();
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