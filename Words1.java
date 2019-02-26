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

public class Words1 {
	public Words1(){}
	public String[] extract_words(String path) {
		// read stop word
		Set<String> stopWords = new HashSet<>();
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
		String[] book = text.split("\\s+");
		for (int i=0; i<book.length; i++){
			if (stopWords.contains(book[i]) || (book[i].length() <= 1)){
				book[i] = " ";
			}
		}

		return book;
	}
}