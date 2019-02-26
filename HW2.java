import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Four
{ 
    public static void main(String args[]) {
    	if (args.length > 1 || args.length == 0){
    		System.out.println("Error, usage: java ClassName inputfile");
			System.exit(1);
    	}
		
		Map<String, Integer> words = new HashMap<>();
		String data = readFile(args[0]);
		data = filter(data);
		String[] scan_file = scan(data);
		scan_file = remove(scan_file);
		words = frequencies(scan_file);
		words = sortByValue(words);
		try {
				File file = new File("week2-Four.txt");
				FileWriter fileWriter = new FileWriter(file);
				int count = 0;
				for(String key: words.keySet()){
	        		fileWriter.write(key + "  -  " + words.get(key) + "\n");
	        		count += 1;
	        		if (count >= 25){
	        			break;
	        		}
				}
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    }

    public static String readFile(String filepath){
		String text = "";
		try {
		      text = new String(Files.readAllBytes(Paths.get(filepath)));
		} catch (IOException e) {
		      e.printStackTrace();
		    }
		return text; 
	}
	
	public static String filter(String text){
		text = text.replaceAll("[^a-zA-Z0-9]", " ");
		text = text.toLowerCase();
		return text;
	}
	
	public static String[] scan(String text){
	    String[] data = text.split("\\s+", -1);
	    return data;
	}
	
	public static String[] remove(String[] book) {
	    //stop words
        Set<String> stop_set = new HashSet<>();
        String stop_words = readFile("/projects/stop_words.txt");
		stop_words = filter(stop_words);
		String data_stop[] = scan(stop_words);
        for(String word: data_stop){
			stop_set.add(word);
		}
		// word list
		for(int i = 0; i < book.length; i++){
		    boolean exist = stop_set.contains(book[i]) || book[i].length() <= 1;
	        if (exist){
	            book[i] = null;
	        }	    
		}
		return book;		
    }
        
	public static Map<String, Integer> frequencies(String[] book){
	    Map<String, Integer> words = new HashMap<>();
        for(String word: book){
            if(word != null){
                if(words.containsKey(word)){
    				int val = words.get(word);	
    				words.put(word, val + 1);
    			}
    			else
    				words.put(word, 1);
		    }

        }
        return words;
    }
    
    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return -(o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
	
}
