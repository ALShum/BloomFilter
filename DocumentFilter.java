import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adds the contents of a text document to a bloom filter.
 * @author Alex Shum
 */
public class DocumentFilter {
	boolean docAdded;
	AbstractBloomFilter filter;
	String fileName;
	String pathName;
	static final List<String> stopWords = 
			new ArrayList<String>(Arrays.asList(
					"are", "with", "the", "can", "has", 
					"had", "let", "like", "that", "for", "and"));
	
	/**
	 * Creates a new document filter for a specific text file.
	 * @param bitsPerWord The number of bits to use for each element (unique word) in the document.
	 * @param fileName The name of the text file.
	 * @param pathName The path of the text file.
	 * @throws IOException if the text file cannot be found or opened.
	 */
	public DocumentFilter(int bitsPerWord, String fileName, String pathName) throws IOException {
		docAdded = false;
		
		//find num unique words
		FileReader fr = new FileReader(pathName + File.separator + fileName);
		int numUnique = numUnique(fr);
		
		filter = new BloomFilterDet(numUnique, bitsPerWord);
		this.fileName = fileName;
		this.pathName = pathName;		
	}
	
	/**
	 * Adds all the contents (unique words) to the document.  This does some minimal preprocessing
	 * of the text by removing punctuation, converting to lower case and removing stop words.
	 * 
	 * Stop words removed are any words shorter than length 3 and the following:
	 * the, are, with, can, has, had, let, like, that, for, and
	 * 
	 * @throws IOException if the text file cannot be found or opened.
	 */
	public void addDocument() throws IOException {
		//read in the file
		FileReader fr = new FileReader(pathName + File.separator + fileName);
		BufferedReader b = new BufferedReader(fr);
		
		String line;
		String[] words;
		while((line = b.readLine()) != null) {
			words = line.replaceAll("[^a-zA-Z0-9 ]", " ").toLowerCase().split("\\s+");
			for(int i = 0; i < words.length; i++) {
				if(words[i].length() > 2 && !isStopWord(words[i])) {
					//System.out.println(words[i]); //for debugging
					filter.add(words[i]);
				}
			}
		}
		b.close();
		
		docAdded = true;
	}
	
	/**
	 * Queries the bloom filter for a word.
	 * @param s String to check.
	 * @return true if the document might contain the word, false if the document definitely does not contain the word.
	 * @throws IllegalStateException if you try to check a word without adding all words in the document to the filter.
	 */
	public boolean appears(String s) throws IllegalStateException {
		if(!docAdded) throw new IllegalStateException("addDocument() first");
		return(filter.appears(s));
	}
	
	/**
	 * Queries the bloom filter for an array of words.
	 * @param s Array of strings to check.
	 * @return true if the document might contains any of the words, 
	 * 		false if the document definitely does not contain any of the words.
	 * @throws IllegalStateException if you try to check words without adding document words to the filter.
	 */
	public boolean appears(String[] s) throws IllegalStateException {
		for(int i = 0; i < s.length; i++) {
			if(appears(s[i])) return(true);
		}
		
		return(false);
	}
	
	/**
	 * Returns the file name of the document stored in the filter.
	 * @return file name of document
	 */
	public String getDocument() {
		return(fileName);
	}
	
	/**
	 * Returns the size of the bloom filter.
	 * @return size of the bloom filter.
	 */
	public int filterSize() {
		return(filter.filterSize());
	}
	
	/**
	 * Returns the number of words in the document added to the bloom filter.
	 * @return number of words in the bloom filter
	 * @throws IllegalStateException if you try to check on the size before adding any words from the document.
	 */
	public int dataSize() throws IllegalStateException {
		if(!docAdded) throw new IllegalStateException("addDocument() first");
		return(filter.dataSize());
	}
	
	/**
	 * Returns the number of hash functions to be used for this bloom filter
	 * @return the number of hashes.
	 */
	public int numHashes() {
		return(filter.numHashes());
	}
	
	/**
	 * Counts the number of unique words in a text file.  Does minimal processing
	 * to remove words less than 3 characters and some stop words.  See
	 * documents for addDocument() method to see a list of stop words removed.
	 * @param fileName The file name of the text document.
	 * @return the number of unique words.
	 * @throws IOException
	 */
	private int numUnique(FileReader fileName) throws IOException {
		BufferedReader b = new BufferedReader(fileName);
		Set<String> s = new HashSet<String>();
		
		String line;
		String[] words;
		while((line = b.readLine()) != null) {
			words = line.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().split("\\s+");
			for(int i = 0; i < words.length; i++) {
				if(words[i].length() > 2 && !isStopWord(words[i])) {
					s.add(words[i]);
				}
			}
		}
		b.close();
		
		return(s.size());
	}
	
	/**
	 * Checks if a string is a stop word or not.  The stop words to check are the following:  
	 * the, are, with, can, has, had, let, like, that, for, and
	 * 
	 * @param s String to check
	 * @return true if the word is in the list of stop words.  
	 */
	private boolean isStopWord(String s) {
		if(stopWords.contains(s)) return(true);
		return(false);
	}
}
