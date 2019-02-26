import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HW1
{ 
    public static void main(String args[]) {
    	
	String stop_words = readFileAsString("/home/richard/Desktop/PL/HW1/stop_words.txt");
	String pride = readFileAsString("/home/richard/Desktop/PL/HW1/pride-and-prejudice.txt");           
	String[] part1 = split(stop_words);
	String[] part2 = split(pride);

	Set<String> stop_set = new HashSet<>();
	Map<String, Integer> words = new HashMap<>();

	for(String word: part1){
		stop_set.add(word);
	}
	
	for(String word: part2){ 
		boolean exist = stop_set.contains(word);
		if (!exist){
			if(words.containsKey(word)) {
				int val = words.get(word);	
				words.put(word, val + 1);
			}
			else
				words.put(word, 1);
		}
	}

	try {
			File file = new File("test1.txt");
			FileWriter fileWriter = new FileWriter(file);
			for(String key: words.keySet()){
        		fileWriter.write(key + ": " + words.get(key) + "\n");
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

    }

    public static String readFileAsString(String filepath){
		String text = "";
		try {
		      text = new String(Files.readAllBytes(Paths.get(filepath)));
		} catch (IOException e) {
		      e.printStackTrace();
		    }
		return text; 
	}

	public static String[] split(String text){
		text = text.replaceAll("[^a-zA-Z0-9]", " ");
		text = text.toLowerCase();
		String[] part = text.split("\\s+", -1);
		return part;
	}
}
