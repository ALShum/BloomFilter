/**
 * Bloom filter implementation that Extends AbstractBloomFilter.
 * This bloom filter uses the deterministic 32-bit FNV hash function to hash strings.
 * @author Alex Shum
 */
public class BloomFilterDet extends AbstractBloomFilter {
	/**
	 * Creates a new bloom filter with FNV hash
	 * @param setSize The estimated number of elements in the set.
	 * @param bitsPerElement The number of bits to use per element in set.
	 */
	public BloomFilterDet(int setSize, int bitsPerElement) {
		super(setSize, bitsPerElement);
	}

	/**
	 * Generates k-hashes of a string using FNV for use with bloom filter bit array.
	 * The number of hashes K is determined in the constructor.
	 * The number of hashes is based on the bits per element:
	 * ln(2) * M / N where M = size of bloom filter and N = set size
	 * 
	 * @param s String to generate k-hashes for.
	 * @return k-hashes
	 */
	@Override
	public int[] hashFunc(String s) {
		int[] ans = new int[numHash];
		int a = fnv(s);
		int b = a * string2int(s);
		
		for(int i = 0; i < numHash; i++) {
			ans[i] = (a + b*i) % tableLength;
			if(ans[i] < 0) ans[i] = -ans[i];
		}
		
		return(ans);
	}
	
	
	/**
	 * This is the 32-bit version of the FNV hash.  
	 * More information can be found at: 
	 * http://www.isthe.com/chongo/tech/comp/fnv/
	 * 
	 * The INIT and PRIME values are expressed in hex and those values are from
	 * the C implementation of FNV and can be found at: 
	 * http://www.isthe.com/chongo/src/fnv/hash_32.c
	 * 
	 * @param s String to be hashed.
	 * @return Integer hash value of string.
	 */
	private int fnv(String s) {
		final int FNVINIT = 0x811c9dc5;
	    final int FNVPRIME = 0x01000193;
		
		int h = FNVINIT;
		for(int i = 0; i < s.length(); i++) {
			h ^= s.charAt(i);
			h *= FNVPRIME;
			h = (int) (h % Math.pow(2, 32));
		}
		
		return(h);
	}
	
	/**
	 * Simple way of converting a string to an integer.
	 * XORs the characters of a string together and multiplies the result by string length
	 * @param s String to convert to integer.
	 * @return Integer after XORing the characters of string and multiplying length.
	 */
	private int string2int(String s) {
		int h = 0;
		
		for(int i = 0; i < s.length(); i++) {
			h ^= s.charAt(i);
		}
		
		h = h * s.length();
		return(h);
	}
}
