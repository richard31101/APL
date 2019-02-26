package plugins;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.reflect.Method; 
import java.lang.reflect.Field; 
import java.lang.reflect.Constructor; 
import java.lang.reflect.InvocationTargetException;

public class Frequencies1 {
	
	public TreeMap<String, Integer> top25(String[] book) {
		HashMap<String, Integer> dict = new HashMap<String, Integer>();
		for (String b:book){
			if (b.equals(" ")){
				continue;
			}else if (dict.get(b) == null){
				dict.put(b, 1);
			}else{
				dict.put(b, dict.get(b) + 1);
			}
		}

		TreeMap<String, Integer> sorted_book;
		SortMapByValueComparator bvc = new SortMapByValueComparator(dict);
		sorted_book = new TreeMap<String, Integer>(bvc);

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