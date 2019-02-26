import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TwentyEight {

    public static void main(String[] args) {
        // write your code here
        Queue<String> queue = new LinkedList<>();
        WordFrequencyManager wordFrequencyManager = new WordFrequencyManager();
        
        StopWordManager stopWordManager = new StopWordManager();
        send(queue, stopWordManager, wordFrequencyManager);

        DataStorageManager dataStorageManager = new DataStorageManager();
        send(queue, dataStorageManager, stopWordManager);
        
        WordFrequencyController wfcontroller = new WordFrequencyController();
        send(queue, wfcontroller, dataStorageManager);

        wfcontroller.dispatch(new String[]{"init", args[0]}, queue);
        TreeMap<String, Integer> sorted_frequency = wfcontroller.dispatch(new String[]{"run"}, queue);

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

    public static void send(Queue<String> queue, StopWordManager swm, WordFrequencyManager wfm) {
        queue.offer("swm");
        queue.offer("wfm");
    }

    public static void send(Queue<String> queue, DataStorageManager dsm, StopWordManager swm) {
        queue.offer("dsm");
        queue.offer("swm");
    }

    public static void send(Queue<String> queue, WordFrequencyController wfc, DataStorageManager dsm) {
        queue.offer("wfc");
        queue.offer("dsm");
    }
}

class DataStorageManager {
    public String[] words;
    public String text;

    public String[] dispatch(String[] message) {
        if (message[0].equals("init")) {
            this.init(message[1]);
            return new String[0];
        } else {
            if (message[0].equals("words")) {
                return this._words();
            }
        }
        return new String[0];
    }

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

    public void run() {
        return;
    }
    public String[] _words() {
        // scan
        words = text.split("\\s+");
        return words;
    }
}

class StopWordManager {
    public Set<String> stopWords = new HashSet<>();

    public boolean dispatch(String[] message) {
        if (message[0].equals("init")) {
            this.init();
            return true;
        } else {
            if (message[0].equals("is_stop_word")) {
                return this._is_stop_word(message[1]);
            }
        }
        return true;
    }

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

    public void run() {
        return;
    }

    public boolean _is_stop_word(String word) {
        return stopWords.contains(word);
    }
}

class WordFrequencyManager {
    public Map<String, Integer> dict = new HashMap<String, Integer>();

    public TreeMap<String, Integer> dispatch(String[] message) {
        if (message[0].equals("increment_count")) {
            this._increment_count(message[1]);
        } else {
            if (message[0].equals("sorted")) {
                return this._sorted();
            }
        }
        return new TreeMap<String, Integer>();
    }

    // frequencies
    public void _increment_count(String word) {
        if (dict.get(word) == null){
            dict.put(word, 1);
        } else {
            dict.put(word, dict.get(word) + 1);
        }
    }

    public void run() {
        return;
    }

    public TreeMap<String, Integer> _sorted() {
        SortMapByValueComparator bvc = new SortMapByValueComparator(dict);
        TreeMap<String, Integer> sorted_book = new TreeMap<String, Integer>(bvc);

        sorted_book.putAll(dict);
        return sorted_book;
    }
    
}

class WordFrequencyController {
    DataStorageManager _storage_manager;
    StopWordManager _stop_word_manager;
    WordFrequencyManager _word_freq_manager;

    ThreadManager thread_storage_manager;
    ThreadManager thread_stop_word_manager; 
    ThreadManager thread_word_freq_manager;


    public TreeMap<String, Integer> dispatch(String[] message, Queue<String> queue) {
        if (queue.isEmpty()) {
            return null;
        }
        if (message[0].equals("init")) {
            this._init(message[1]);
            return null;
        } else {
            if (message[0].equals("run")) {
                thread_storage_manager.start();
                thread_stop_word_manager.start();
                thread_word_freq_manager.start();
                return this._run();
            }
        }
        return null;
    }

    public void _init(String path_to_file) {
        _storage_manager = new DataStorageManager();
        _stop_word_manager = new StopWordManager();
        _word_freq_manager = new WordFrequencyManager();

        _storage_manager.dispatch(new String[]{"init", path_to_file});
        _stop_word_manager.dispatch(new String[]{"init"});

        thread_storage_manager = new ThreadManager( "_storage_manager");
        thread_stop_word_manager = new ThreadManager( "_stop_word_manager");
        thread_word_freq_manager = new ThreadManager( "_word_freq_manager");
    }

    public TreeMap<String, Integer> _run() {
        for (String word : _storage_manager.dispatch(new String[]{"words"})) {
            if (!_stop_word_manager.dispatch(new String[]{"is_stop_word", word})) {
                _word_freq_manager.dispatch(new String[]{"increment_count", word});
            }
        }

        TreeMap<String, Integer> sorted_frequency = _word_freq_manager.dispatch(new String[]{"sorted"});
        

        return sorted_frequency;
    }
}

class ThreadManager extends Thread {
    private Thread t;
    
    private String threadName;
    public DataStorageManager _storage_manager;
    public StopWordManager _stop_word_manager;
    public WordFrequencyManager _word_freq_manager;

    public ThreadManager(String name) {
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
            _storage_manager = new DataStorageManager();
            _storage_manager.run();
        } else if (threadName.equals("_stop_word_manager")) {
            _stop_word_manager = new StopWordManager();
            _stop_word_manager.run();
        } else {
            _word_freq_manager = new WordFrequencyManager();
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