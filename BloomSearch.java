import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Adds the entire contents of a folder to document filters and queries each document filter for search terms.
 * @author Alex Shum
 */
public class BloomSearch {
	private final static int BITS_PER_ELEM = 16;
	
	/**
	 * For each text file in the directory creates a new DocumentFilter and returns the file name
	 * of any document which contains the search term.  
	 * @param args Directory with text files followed by search terms.
	 * 		  Example: <Directory> <term1> <term2> <term 3> ...
	 */
	public static void main(String[] args) {
		if(args.length < 2) throw new IllegalArgumentException("please enter directory followed by search terms");
		File folder = new File(args[0]);
		File[] contents = folder.listFiles();
		
		List<DocumentFilter> filters = new ArrayList<DocumentFilter>();
		DocumentFilter filt;
		for(int i = 0; i < contents.length; i++) {
			if(contents[i].isFile()) {
				try {
					filt = new DocumentFilter(BITS_PER_ELEM, contents[i].getName(), args[0]);
					filt.addDocument();
					filters.add(filt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
		}
		
		String[] terms = Arrays.copyOfRange(args, 1, args.length);
		List<String> found = new ArrayList<String>();
		for(DocumentFilter f : filters) {
			if(f.appears(terms)) found.add(f.getDocument());
		}
		
		System.out.println(found.toString());
		
		int sumSize = 0;
		for(DocumentFilter f : filters) {
			sumSize += f.filterSize();
		}
		System.out.println("Total number of bits used: " + sumSize);
	}
}
