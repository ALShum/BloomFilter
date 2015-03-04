import java.util.BitSet;

/**
 * Abstract Bloom Filter implements most of the methods for a bloom filter except 
 * for a specific hashing method.
 * @author Alex Shum
 */
public abstract class AbstractBloomFilter {
	BitSet ary;
	int bitsPerElement; //= M/N where M = size of table and N = number of elements
	int setSize; //expected number of elements = N
	int numElem; //number of elements stored in bloom filter
	int tableLength; //number of bits for the table
	int numHash; //number of hash functions
	
	/**
	 * Creates a bloom filter with specified set size and bits per element.
	 * Note that the size of the bloom filter will be setSize * bitsPerElement.
	 * @param setSize The estimated size of the set of things stored in bloomfilter.
	 * @param bitsPerElement The number of bits for each element; how many bits to use for each set element.
	 */
	public AbstractBloomFilter(int setSize, int bitsPerElement) {
		this.setSize = setSize;
		this.bitsPerElement = bitsPerElement;
		
		tableLength = bitsPerElement * setSize;
		numHash = (int)(0.69 * bitsPerElement) + 1; //ln(2) * M/N
		
		ary = new BitSet();
		numElem = 0;
	}
	
	/**
	 * Adds a string to the bloom filter.  Case insensitive.
	 * @param s String to add.
	 */
	public void add(String s) {
		s = s.toLowerCase();
		int[] pos = hashFunc(s);
		
		for(int i = 0; i < pos.length; i++) {
			if(!ary.get(pos[i])) ary.flip(pos[i]);
		}
		
		numElem++;
	}
	
	/**
	 * Checks if string has been added to filter.  Case insensitive.
	 * @param s String to check.
	 * @return true if filter possibly contains string, false if it definitely does not.
	 */
	public boolean appears(String s) {
		s = s.toLowerCase();
		int[] pos = hashFunc(s);
		
		for(int i = 0; i < pos.length; i++) {
			if(!ary.get(pos[i])) return(false); 
		}

		return true;
	}
	
	/**
	 * Returns bitset array length of filter.
	 * @return size of bloomfilter array.
	 */
	public int filterSize() {
		return(tableLength);
	}
	
	/**
	 * Returns number of elements added to bloom filter
	 * @return Number of elements added so far
	 */
	public int dataSize() {
		return(numElem);
	}
	
	/**
	 * Returns the number of hash functions = ln(2) * M / N
	 * M = size of table
	 * N = set size
	 * @return number of hash functions
	 */
	public int numHashes() {
		return(numHash);
	}
	
	/**
	 * Returns a k-length array corresponding to the k-hashes for a string
	 * @param s The string to hash.
	 * @return k-hashes of string in an int array
	 */
	public abstract int[] hashFunc(String s);
}
