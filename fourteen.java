import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class Fourteen {

    public static void main(String[] args) {
        WordFrequencyFramework wfapp = new WordFrequencyFramework();
        StopWordFilter stop_word_filter = new StopWordFilter(wfapp);
        DataStorage data_storage = new DataStorage(wfapp, stop_word_filter);
        WordFrequencyCounter word_freq_counter = new WordFrequencyCounter(wfapp, data_storage);

        wfapp.dataStorage = data_storage;
        wfapp.stopWordFilter = stop_word_filter;
        wfapp.wordFrequencyCounter = word_freq_counter;

        wfapp.run(args[0]);
        
        DataStorage_Z dataStorage_Z = new DataStorage_Z(data_storage);
        System.out.println("");
        System.out.println("Number of words contain z = " + dataStorage_Z.produce_words_Z());
    }
}

class WordFrequencyFramework{
    ArrayList<String> _load_event_handlers = new ArrayList<>();
    ArrayList<String> _dowork_event_handlers = new ArrayList<>();
    ArrayList<String> _end_event_handlers = new ArrayList<>();

    DataStorage dataStorage;
    StopWordFilter stopWordFilter;
    WordFrequencyCounter wordFrequencyCounter;

    public void register_for_load_event(String handler) {
       _load_event_handlers.add(handler);
    }

    public void register_for_dowork_event(String handler) {
       _dowork_event_handlers.add(handler);
    }

    public void register_for_end_event(String handler) {
       _end_event_handlers.add(handler);
    }

    public void run(String path_to_file) {
        for (String event : _load_event_handlers) {
            if (event.equals("DataStorage_load")){
                dataStorage.__load(path_to_file);
            }
            else if(event.equals("StopWordFilter_load")){
                stopWordFilter.__load();
            }
        }

        for (String event : _dowork_event_handlers) {
            if (event.equals("produce_words")){
                dataStorage.__produce_words(this);
            }
        }
        
        for (String event : _end_event_handlers) {
            if (event.equals("print_freqs")){
                wordFrequencyCounter.__print_freqs();
            }
        }

    }
}

class DataStorage {
    String _data;
    StopWordFilter _stop_word_filter; 
    ArrayList<String> _word_event_handlers = new ArrayList<>();
    
    String [] words;
    public DataStorage(WordFrequencyFramework wfapp, StopWordFilter _stop_word_filter) {
        this._stop_word_filter = _stop_word_filter;
        wfapp.register_for_load_event("DataStorage_load");
        wfapp.register_for_dowork_event("produce_words");
    }
    
    public void __load(String path_to_file) {
        try {
          _data = new String(Files.readAllBytes(Paths.get(path_to_file)));
        } catch (IOException e) {
          e.printStackTrace();
        }
        _data = _data.replaceAll("[^a-zA-Z0-9\\s+]", " ").toLowerCase();
    }

    public void __produce_words(WordFrequencyFramework wfapp) {
        words = _data.split("\\s+");
        for (String word : words){
            if (!_stop_word_filter.is_stop_word(word)){
                for (String event : _word_event_handlers){
                    if (event.equals("increment_count")){
                        wfapp.wordFrequencyCounter.__increment_count(word);
                    }
                }
            }
        }

    }
    public void register_for_word_event(String handler) {
        _word_event_handlers.add(handler);
    }
}

class StopWordFilter {
    Set<String> _stop_words = new HashSet<>();

    public StopWordFilter(WordFrequencyFramework wfapp) {
        wfapp.register_for_load_event("StopWordFilter_load");
    }

    public void __load() {
        String text = "";
        try {
          text = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
        } catch (IOException e) {
          e.printStackTrace();
        }

        String[] splitword = text.split(",");
        for (String word : splitword){
            _stop_words.add(word);
        }
    }

    public boolean is_stop_word(String word) {
        return _stop_words.contains(word);
    }
}

class WordFrequencyCounter {
    public Map<String, Integer> _word_freqs = new HashMap<String, Integer>();

    public WordFrequencyCounter(WordFrequencyFramework wfapp, DataStorage data_storage) {
        data_storage.register_for_word_event("increment_count");
        wfapp.register_for_end_event("print_freqs");
    }

    public void __increment_count(String word) {
        if (_word_freqs.get(word) == null){
            _word_freqs.put(word, 1);
        } else {
            _word_freqs.put(word, _word_freqs.get(word) + 1);
        }
    }

    public void __print_freqs() {
        SortMapByValueComparator bvc = new SortMapByValueComparator(_word_freqs);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);

        sorted_map.putAll(_word_freqs);
        
        int i = 25;
        for (Map.Entry<String, Integer> entry : sorted_map.entrySet()) {
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

class DataStorage_Z{
    StopWordFilter _stop_word_filter; 
    ArrayList<String> _word_event_handlers = new ArrayList<>();
    
    String [] words;
    public DataStorage_Z(DataStorage dataStorage) {
        this._stop_word_filter = dataStorage._stop_word_filter;
        this.words = dataStorage.words;
    }
    
    public int produce_words_Z(){
        int i = 0;
        for (String word : words){
            if(!_stop_word_filter.is_stop_word(word) && word.contains("z")){
                i++;
            }
        }
        return i;
    }
}