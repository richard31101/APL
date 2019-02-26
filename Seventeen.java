import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.reflect.Method; 
import java.lang.reflect.Field; 
import java.lang.reflect.Constructor; 
import java.lang.reflect.InvocationTargetException;

public class Seventeen {
	

	public static void main(String[] args) {
		
		String extract_words_func = null;
		String frequencies_func = null;
		String sort_func = null;
		String filename = null;

		if (args.length > 0) {
			extract_words_func = "extract_words_func";
			frequencies_func = "frequencies_imp";
			sort_func = "sort_func";
			filename = args[0];
		} else {
			extract_words_func = "";
			frequencies_func = "";
			sort_func = "";
			filename = "";
		}

		TermFrequency termFrequency = new TermFrequency(filename);
		Class cls = termFrequency.getClass();
		try {
			cls.getDeclaredMethod(extract_words_func).invoke(termFrequency); 
			cls.getDeclaredMethod(frequencies_func).invoke(termFrequency);
			cls.getDeclaredMethod(sort_func).invoke(termFrequency);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		

		int counter = 0;
		for (Map.Entry<String, Integer> entry: termFrequency.sorted_book.entrySet()){
			String key = entry.getKey();
		    Integer val = entry.getValue();
		    String result = String.format("%s  -  %d", key, val);
		    System.out.println(result);
		    counter += 1;
		    if (counter >= 25){
		    	break;
		    }
		}
	}

	
}

class TermFrequency {
	public Set<String> stopWords = new HashSet<>();
	public String path;
	public String[] book;
	public Map<String, Integer> dict;
	public TreeMap<String, Integer> sorted_book;

	public TermFrequency(String path) {
		this.path = path;
	}

	public void extract_words_func() {
		// read stop word
		String stopWords_str = "";
	    try {
	      stopWords_str = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
		String[] split = stopWords_str.split(",");
		for (String s:split){
			stopWords.add(s);
		}

		// read file
		String text = "";
	    try {
	      text = new String(Files.readAllBytes(Paths.get(path)));
	    } catch (IOException e) {
	      e.printStackTrace();
	    }

	    // normalize
	    text = text.toLowerCase();
		text = text.replaceAll("[^a-zA-Z0-9\\s+]", " ");

		// remove stop words
		book = text.split("\\s+");
		for (int i=0; i<book.length; i++){
			if (stopWords.contains(book[i]) || (book[i].length() <= 1)){
				book[i] = " ";
			}
		}

	}


	public void frequencies_imp(){
		dict = new HashMap<String, Integer>();
		for (String b:book){
			if (b.equals(" ")){
				continue;
			}else if (dict.get(b) == null){
				dict.put(b, 1);
			}else{
				dict.put(b, dict.get(b) + 1);
			}
		}

	}

	public void sort_func(){
		SortMapByValueComparator bvc = new SortMapByValueComparator(dict);
		sorted_book = new TreeMap<String, Integer>(bvc);

		sorted_book.putAll(dict);
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