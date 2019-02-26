import plugins.Frequencies1;
import plugins.Words1;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Nineteen {
	public static String words_plugin;
	public static String frequencies_plugin;
	public static Words1 tfwords;
	public static Frequencies1 tffreqs;

	public static void main(String[] args) {
		//System.out.privateintln(args[0]);

		load_load_plugins();
		imp_oad_compiled(words_plugin);
		imp_oad_compiled(frequencies_plugin);
		TreeMap<String, Integer> sorted_book = tffreqs.top25(tfwords.extract_words(args[0]));

		int counter = 0;
		for (Map.Entry<String, Integer> entry : sorted_book.entrySet()){
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
	
	public static void imp_oad_compiled(String className) {
		if (className.equals("words_plugin")) {
			tfwords = new Words1();
		} else {
			tffreqs = new Frequencies1();
		}
	}
	
	public static void load_load_plugins() {
		Properties properties = new Properties();
		String configFile = "config.properties";
		try {
		    properties.load(new FileInputStream(configFile));
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		    return;
		} catch (IOException e) {
		    e.printStackTrace();
		    return;
		}

		words_plugin = properties.getProperty("words");
		frequencies_plugin = properties.getProperty("frequencies");
	}
}