import java.util.Random;

/**
 * Bloom filter implementation that Extends AbstractBloomFilter.
 * This bloom filter uses a random hash function to hash strings.
 * @author Alex Shum
 */
public class BloomFilterRan extends AbstractBloomFilter {
	public int a1 = 0, b1 = 0, a2 = 0, b2 = 0; //for the random hash functions
	
	/**
	 * Creates a new bloom filter with Random hash function.  
	 * Due to how the random hash function is created, the user inputted bitsPerElement
	 * will be a lower bound.  The random hash function depends on the length of the
	 * bit array to be prime.  Because of this, if the bit lenth corresponding to the inputted
	 * bitsPerElement is not prime, the actual length of the bloom filter will be
	 * set to the next largest prime number.
	 * 
	 * @param setSize The estimated number of elements in the set.
	 * @param bitsPerElement The lower bound on the number of bits per element in the set.
	 */
	public BloomFilterRan(int setSize, int bitsPerElement) {
		super(setSize, bitsPerElement);
		tableLength = nextPrime(setSize * bitsPerElement);
		numHash = (int)(0.69 * (tableLength / setSize + 1)) + 1; //ln(2) * M/N
		
		Random r = new Random();
		while(a1 == a2) {
			a1 = r.nextInt(tableLength);
			a2 = r.nextInt(tableLength);
		}
		
		while(b1 == b2) {
			b1 = r.nextInt(tableLength);
			b2 = r.nextInt(tableLength);
		}
	}

	
	/**
	 * Generates k-hashes of a string using random hash functions for use with bloom filter bit array.
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
		int first = randHash(s, a1, b1);
		int second = randHash(s, a2, b2);
		
		for(int i = 0; i < numHash; i++) {
			ans[i] = (first + second * i) % tableLength;
			if(ans[i] < 0) ans[i] = -ans[i];
		}
		
		return(ans);
	}
	
	/**
	 * A random hash function for strings. Requires random coefficients.
	 * Given two random coefficients generated from {0, 1, ..., p - 1}
	 * where p = prime length of bloom filter bit array, the random 
	 * hash function will be: a + b * x.  
	 * 
	 * @param s String to be hashed.
	 * @param a Random coefficient of hash function.
	 * @param b Random coefficient of hash function.
	 * @return Integer hash value of string.
	 */
	public int randHash(String s, int a, int b) {
		int hashed = 0;
		
		for(int i = 0; i < s.length(); i++) {
			hashed ^= s.charAt(i);
			hashed = a + b * hashed;
			hashed = hashed % tableLength;
		}
		
		return(hashed);
	}
	
	/**
	 * Finds the next prime number larger than a starting integer.
	 * @param n Starting integer.
	 * @return The next prime number larger than starting integer.
	 */
	private int nextPrime(int n) {
		boolean isPrime = false;
		
		int m = n;
		while(!isPrime) {
			isPrime = isPrime(++m);
		}
		
		return(m);
	}
	
	/**
	 * Checks if integer is prime or not.
	 * This is based on the sieve of eratosthenes.
	 * This particular implementation is is based off information from:
	 * http://en.wikipedia.org/wiki/Primality_test
	 * 
	 * @param n Integer to check for primality.
	 * @return true if n is prime otherwise false.
	 */
	private boolean isPrime(int n) {
		if(n == 1) return(false);
		else if(n == 2 || n == 3) return(true);
		else if(n % 2 == 0 || n % 3 == 0) return(false);
		else {
			for(int i = 5; i*i < n + 1; i += 6) {
				if(n % i == 0 || n % (i + 2) == 0) {
					return(false);
				}
			}
			return(true);
		}
	}
	
}
