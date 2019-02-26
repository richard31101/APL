import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Fourteen {

    public static void main(String[] args) {
        // write your code here
        WordFrequencyFramework wfapp = new WordFrequencyFramework();
        StopWordFilter stop_word_filter = new StopWordFilter(wfapp);
        DataStorage data_storage = new DataStorage(wfapp, stop_word_filter);
        WordFrequencyCounter word_freq_counter = new WordFrequencyCounter(wfapp, data_storage);
        wfapp.ds = data_storage;
        wfapp.swf = stop_word_filter;
        wfapp.wfc = word_freq_counter;
        wfapp.run(args[0]);

        DataStorageZ data_storage_z = new DataStorageZ(data_storage);
        System.out.println(data_storage_z.produce_words_z());
    }
}

class WordFrequencyFramework {
    DataStorage ds;
    StopWordFilter swf;
    WordFrequencyCounter wfc;
    ArrayList<String> load_event = new ArrayList<>();
    ArrayList<String> dowork_event = new ArrayList<>();
    ArrayList<String> end_event = new ArrayList<>();


    public void register_for_load_event(String event) {
        load_event.add(event);
    }

    public void register_for_dowork_event(String event) {
        dowork_event.add(event);
    }

    public void register_for_end_event(String event) {
        end_event.add(event);
    }

    public void run(String path) {

        for (String event : load_event) {
            if (event.equals("DataStorage_load")) {
                ds.load(path);
            } else if (event.equals("StopWordFilter_load")) {
                swf.load();
            }
        }
    
        for (String event : dowork_event) {
            if (event.equals("DataStorage_produce_words")) {
                ds.produce_words(this);
            }
        }


        for (String event : end_event) {
            if (event.equals("WordFrequencyCounter_print_freqs")) {    
                wfc.print_freqs();
            }
        }
    }
}

class DataStorageZ {
    public String book;
    StopWordFilter swf;
    public ArrayList<String> events = new ArrayList<>();

    public DataStorageZ(DataStorage ds) {
        this.book = ds.book;
        this.swf = ds.swf;
    }

    public int produce_words_z() {
        String[] words = book.split("\\s+");
        int i = 0;
        for (String s : words) {
            if (!swf.is_stop_word(s) && s.contains("z")) {
                i++;
            }
        } 
        return i;
    }    
}

class DataStorage {
    public String book;
    StopWordFilter swf;

    public ArrayList<String> events = new ArrayList<>();

    public DataStorage(WordFrequencyFramework wfapp, StopWordFilter swf) {
        this.swf = swf;
        wfapp.register_for_load_event("DataStorage_load");
        wfapp.register_for_dowork_event("DataStorage_produce_words");
    }

    public void load(String path) {
        try {
          book = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
          e.printStackTrace();
        }

        // filter_chars_and_normalize
        book = book.toLowerCase();
        book = book.replaceAll("[^a-zA-Z0-9\\s+]", " ");
    }

    public void produce_words(WordFrequencyFramework wfapp) {
        String[] words = book.split("\\s+");
        int i = 0;
        for (String s : words) {
            if (!swf.is_stop_word(s)) {
                for (String event : events) {
                    if (event.equals("increment_count")) {
                        wfapp.wfc.increment_count(s);
                    }
                }
            }
        } 
    }    

    public void register_for_word_event(String event) {
        events.add(event);
    }
}

class StopWordFilter {
    public Set<String> stopWords = new HashSet<>();

    public StopWordFilter(WordFrequencyFramework wfapp) {
        wfapp.register_for_load_event("StopWordFilter_load");
    }

    public void load() {
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

    public boolean is_stop_word(String word) {
        return stopWords.contains(word);
    }
}

class WordFrequencyCounter {
    public Map<String, Integer> frequency = new HashMap<String, Integer>();

    public WordFrequencyCounter(WordFrequencyFramework wfapp, DataStorage data_storage) {
        data_storage.register_for_word_event("increment_count");
        wfapp.register_for_end_event("WordFrequencyCounter_print_freqs");
    }

    public void increment_count(String word) {
        if (frequency.containsKey(word)) {
            frequency.put(word, frequency.get(word) + 1);
        } else {
            frequency.put(word, 1);
        }
    }

    public void print_freqs() {
        SortMapByValueComparator bvc = new SortMapByValueComparator(frequency);
        TreeMap<String, Integer> sorted_frequency = new TreeMap<String, Integer>(bvc);
        sorted_frequency.putAll(frequency);

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