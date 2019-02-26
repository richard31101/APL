package HW3;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;





public class HW3_2 {
	
	
	public static void main(String[] args) throws IOException {

		HW3_2.readFile(new File("pride-and-prejudice.txt"), (a,b) -> HW3_2.filter_chars_and_normalize(a, b));
	
	}
	
	interface I {
	    public void myMethod(List<?> component, I myMethodsInterface);
	}
	
	
	public static void sort(List<?> map, I myMethodsInterface) {
		System.out.println("sort");
		@SuppressWarnings("unchecked")
		List<String> list =  (List<String>) map;
		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub
				return Integer.parseInt(o2.split(" ")[1]) - Integer.parseInt(o1.split(" ")[1]);
			}
			
		});
		myMethodsInterface.myMethod(list, (a,b) -> HW3_2.nothing(a, b));
	}

	public static void readFile(File file, I myMethodsInterface) throws IOException {
		System.out.println("readFile");
		List<String> list = new ArrayList<String>();
		FileReader fr = new FileReader(file);	
		BufferedReader br = new BufferedReader(fr); 
		while (br.ready()) {
			list.add(br.readLine());
		}
		fr.close();
		
		myMethodsInterface.myMethod(list, (a,b) -> HW3_2.scan(a, b));
	}
	
	public static void filter_chars_and_normalize(List<?> data, I myMethodsInterface) {
		System.out.println("filter_chars_and_normalize");
		List<String> tmp = new ArrayList<>();
		for(int i = 0; i < data.size(); i++) {
			String temp = ((String) data.get(i)).toLowerCase();	
			String temparray = temp.replaceAll("[^a-zA-Z0-9]", " ");	
			tmp.add(temparray);					
		}
		myMethodsInterface.myMethod(tmp, (a,b) -> HW3_2.remove_stop_words(a,b));
	}
	
	public static void scan(List<?> data, I myMethodsInterface) {
		System.out.println("scan");
		List<String> tmp = new ArrayList<>();
		for(int i = 0; i < data.size(); i++) {
			String[] temparray = ((String) data.get(i)).trim().split(" ");
			for(int j = 0; j < temparray.length; j++) {
				if(temparray[j].trim().length() == 0) continue;
				tmp.add(temparray[j].trim());					
			}					
		}
		myMethodsInterface.myMethod(tmp, (a,b) -> HW3_2.frequencies(a,b));
	}
	

	
	public static void remove_stop_words(List<?> data, I myMethodsInterface)  {
		System.out.println("remove_stop_words");
		HashSet<String> set = new HashSet<>();
		List<String> tmp = new ArrayList<>();
		
		FileReader frs;
		try {
			frs = new FileReader(new File("stop_words.txt"));
			BufferedReader brs = new BufferedReader(frs); 
			while (brs.ready()) {
				String temp = brs.readLine().toLowerCase();	
				String[] temparray = temp.trim().split(",");	
				for(int i = 0;i < temparray.length;i++) {
					set.add(temparray[i]);					
				}
			}
			frs.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for(int i = 0; i < data.size(); i++) {
			if(!set.contains(data.get(i))) {   
				tmp.add((String) data.get(i));
			}
		}
		myMethodsInterface.myMethod(tmp, (a,b) -> HW3_2.sort(a,b));
	}
	
	public static void frequencies(List<?> a2, I myMethodsInterface) {
		System.out.println("frequencies");
		HashMap<String, Integer> map = new HashMap<>();
		
		for(int i = 0; i < a2.size(); i++) {
			String tmp = a2.get(i).toString();
			if(tmp.length() == 1) continue;
			if(!map.containsKey(tmp)) {
				map.put(tmp, 0);
			}
			map.put(tmp, map.get(tmp) + 1);
		}
		
		LinkedList<String> list = new LinkedList<>();
		for(Entry<String, Integer> o : map.entrySet()) {
			list.add(o.getKey() + " " + o.getValue());
		}
		myMethodsInterface.myMethod(list, (a,b) -> HW3_2.print(a,b));
	}
	


	public static void print(List<?> list , I myMethodsInterface) {
		System.out.println("print");
		@SuppressWarnings("unchecked")
		List<String> words = (List<String>) list;
		int i = 1;
		for(String o : words) {
			String[] arr = o.split(" ");
			if(i <= 25) System.out.println(i +": "+arr[0]+ " - " + arr[1]);
			i++;
		}
		System.out.println("before");
//		myMethodsInterface.myMethod(list, (a,b) -> HW3_2.nothing(a,b));
		System.out.println("here");
	}
	
	public static void nothing(List<?> list , I myMethodsInterface) {
	    System.out.println("nothing");
	}
}