import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Runs an experiment to approximate the number of false positives for bloomfilters.
 * 
 * Set static variable BIT_PER to set the number of bits per element in bloomfilter.
 * Set the DET_FILTER static variable to true to use a deterministic bloom filter, 
 * otherwise it will use a random one.
 * 
 * By default it will run 10 trials of the experiment and calculate the false positive rate
 * from each trial.  For each trial it will generate 5000 words and add it to the bloom filter.
 * Then it will generate another 5000 words that will NOT be added to the bloom filter.  This 
 * second set of words will be queried against the bloom filter.  The number of false positives
 * will be tracked.
 * 
 * The generated strings will have a max length of 20.  However, some strings will be generated
 * with some punctuation which is removed before adding to the filter so there is some variation
 * in string length.
 * 
 * @author Alex Shum
 */
public class FalsePositives {
	final static boolean DET_FILTER = false;
	final static int BITS_PER = 8;
	final static int NUM_TRIALS = 10;
	final static int NUM_WORDS = 5000; //number words added to filter
	final static int OTHER_WORDS = 5000; //number of unadded words to test
	final static int GEN_LENGTH = 20;
	
	/**
	 * Runs the experiment.  There are no command line parameters.
	 * @param args Command line arguments not used.
	 */
	public static void main(String[] args) {
		double rate_sum = 0;
		for(int i = 0; i < NUM_TRIALS; i++) {
			rate_sum += run_trial();
		}
		
		String filterType = DET_FILTER ? "deterministic bloom filter" : "random bloom filter";
		String output = "The average false positive rate is: " + 
						rate_sum / NUM_TRIALS + " for a " + filterType + 
						", with " + BITS_PER + " bits.";
		System.out.println(output);
	}
	
	/**
	 * Generates a random string consisting of upper/lower case letters
	 * numbers and some punctuation with max length.
	 * @param length Max length for strings.
	 * @return Randomly generated string with max length.
	 */
	private static String genRandString(int length) {
		String ans = "";
		
		int min_char = 48;
		int max_char = 122;
		Random rand = new Random();
		for(int i = 0; i < length; i++) {
			int r = rand.nextInt(max_char - min_char + 1) + min_char;
			ans = ans + (char) r;
		}
		
		return(ans);
	}
	
	/**
	 * Runs one iteration of a trial to calculate false positive rate.
	 * The parameters for these trials are by static variables.
	 * @return The false positive rate for this trial.
	 */
	private static double run_trial() {
		int n = 0;
		int fp = 0;
		
		List<String> added_words = new ArrayList<String>();
		AbstractBloomFilter filt;
		if(DET_FILTER) {
			filt = new BloomFilterDet(NUM_WORDS, BITS_PER);
		} else {
			filt = new BloomFilterRan(NUM_WORDS, BITS_PER);
		}
		
		
		String r;
		for(int i = 0; i < NUM_WORDS; i++) {
			r = genRandString(GEN_LENGTH).replaceAll("[^a-zA-Z0-9 ]", "");
			
			//insures unique random words; this should be rare.
			while(added_words.contains(r)) {
				r = genRandString(GEN_LENGTH).replaceAll("[^a-zA-Z0-9 ]", "");
			}
			
			added_words.add(r);
			filt.add(r);
		}
		
		for(int i = 0; i < OTHER_WORDS; i++) {
			r = genRandString(GEN_LENGTH).replaceAll("[^a-zA-Z0-9 ]", "");
			
			while(added_words.contains(r)) {
				r = genRandString(GEN_LENGTH).replaceAll("[^a-zA-Z0-9 ]", "");
			}
			
			n++;
			if(filt.appears(r)) fp++;
		}
		
		double err = (double) fp / n;
		
		return(err);
	}
}
