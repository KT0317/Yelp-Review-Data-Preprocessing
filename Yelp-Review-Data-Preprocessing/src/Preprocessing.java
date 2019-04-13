/*
Creator: Kevin Tang
Date: Mar 28, 2019
Description: Preprocessing 2 CVS files by filtering out stop-words and infrequent words.
			Furthermore, create arff files that is read by Weka (Data Mining Software)
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Preprocessing {

	public static void main(String[] args) throws NumberFormatException, IOException{
		// Use to see how long it takes process and create file
		// NOTE: this is just for me to see how long it takes to run this
		long startTime =System.currentTimeMillis();
		
		//Open files
		//Change directory 
		File training_set = new File("train.csv");
		File testing_set = new File("test.csv");
		File stop_words = new File("stop_words.lst");
		
		
		//Getting the list of stop words
		ArrayList<String> list_stopwords = new ArrayList<String>();	
		try{
			Scanner s = new Scanner(stop_words);
			while (s.hasNext())
				list_stopwords.add(s.next());
			
		} catch (FileNotFoundException e){
			System.out.println("FileNotFound");
			e.printStackTrace();
		}
		
		System.out.println("Starting Stop word removal");
		
////////////////////////////////////// Training Set ///////////////////////////////////////////////////////
		
	    ArrayList<ArrayList<String>> training = new ArrayList<ArrayList<String>>();
	    ArrayList<String> classification = new ArrayList<String>();	
	    ArrayList<Integer> id_training = new ArrayList<Integer>();
	    ArrayList<index> unique = new ArrayList<index>();
	    
	    // stop word removal of training set
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(training_set));
			String line;
			StringTokenizer st;
			boolean first_line = true;
			
			while((line = br.readLine()) != null) {
				st = new StringTokenizer(line, ",");
				String temp;
				
				// skip header
				if(first_line) {
					while(st.hasMoreTokens())
						st.nextToken();
					first_line = false;
				}
				
				while(st.hasMoreTokens()) {
					// train.csv is written as "<text review> classification ID" in that order
					ArrayList<String> review_revised = new ArrayList<String>();
					
					temp = st.nextToken().replaceAll("'", "");	// remove single quote
					temp = temp.replaceAll("\\p{Punct}", " ");	// remove anything not a letter and replace with a space
					
					// split using space(s) as a delimiter
					for (String word : temp.split("\\s+")) {
				    	Stemmer stem = new Stemmer();
						word = word.toLowerCase();
						char[] new_word = word.toCharArray();
						
						stem.add(new_word, new_word.length);
						stem.stem();
						String revised_word = stem.toString();
						
						// If stemmed word/original word is in the list of stop words, reject
						// Also reject 2 letter (or less) words
						boolean newWordFlag = true;
						if ( !list_stopwords.contains(revised_word) && !list_stopwords.contains(word) && revised_word.length()>2) {
							for (index i : unique) {
								if (i.getWord().equals(revised_word)){
									i.increment();
									newWordFlag = false;
								}
							}
							if (newWordFlag)
								unique.add(new index(revised_word,1));
							
							review_revised.add(revised_word);
						}
					}
					Collections.sort(review_revised);
					training.add(review_revised);
					classification.add(st.nextToken());
					id_training.add(Integer.parseInt(st.nextToken()));
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFound");
			e.printStackTrace();
		}
		

/////////////////////////////////////////// Testing set ///////////////////////////////////////////////
		
		ArrayList<ArrayList<String>> test = new ArrayList<ArrayList<String>>();
	    ArrayList<Integer> id_test = new ArrayList<Integer>();
		
		// stop word removal of test set
		try {
			br = new BufferedReader(new FileReader(testing_set));
			String line;
			StringTokenizer st;
			boolean first_line = true;
			
			while((line = br.readLine()) != null) {
				st = new StringTokenizer(line, ",");
				String temp;
				
				// skip header
				if(first_line) {
					while(st.hasMoreTokens())
						st.nextToken();
					first_line = false;
				}
				
				while(st.hasMoreTokens()) {
					// test.csv is written as "ID <text review>" in that order
					ArrayList<String> review_revised = new ArrayList<String>();	
					
    				id_test.add(Integer.parseInt(st.nextToken()));
    
					temp = st.nextToken().replaceAll("'", "");	// remove single quotes
					temp = temp.replaceAll("\\p{Punct}", " ");	// remove anything not a letter and replace with a space
					
					for (String word : temp.split(" ")) {
						Stemmer stem = new Stemmer();
						word = word.toLowerCase();
						char[] new_word = word.toCharArray();
						
						stem.add(new_word, new_word.length);
						stem.stem();
						String revised_word = stem.toString();
						
						// If stemmed word/original word in stop word, reject
						// Also reject 2 letter (or less) words
						boolean newWordFlag = true;
						if ( !list_stopwords.contains(revised_word) && !list_stopwords.contains(word) && revised_word.length()>2 ) {
							for (index i : unique) {
								if (i.getWord().equals(revised_word)){
									i.increment();
									newWordFlag = false;
								}
							}
							// add new word
							if (newWordFlag)
								unique.add(new index(revised_word,1));
								
							review_revised.add(revised_word);
						}
					}
					Collections.sort(review_revised);
					test.add(review_revised);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFound");
			e.printStackTrace();
		}
		
		Collections.sort(unique);

		System.out.println("Stop word removal complete");
		System.out.println("Elapsed time: "+(System.currentTimeMillis()-startTime)*0.001+" seconds");
		
////////////////////////////////////////////// infrequent word removal //////////////////////////////////////////////////////
		
		// Given that the training set has 40,000 entries and test set has 10,000 entries (total 50,000 entries),
		// I'm going to say infrequent words occur 1% or less (tried 2 or less time, it gave me at least 15,000 attributes)
		// weka cannot handle that many attributes given 40,000 entries
		
		System.out.println("Number of unique words Before: " +unique.size());
		
		// removing infrequent 
		Iterator<index> l = unique.iterator();
		int size = (int) Math.floor(unique.size()/100);
		while(l.hasNext()){
			index i = l.next();
			if(i.getCount() <= size){
				l.remove();
			}
		}
		
		System.out.println("Number of unique words After: " +unique.size());
		
		// Convert to into arff format
		File trainOutput = new File("train_output.arff");
		File testOutput = new File("test_output.arff");
		
		createARFF(unique, training, classification, id_training, trainOutput);
		createARFF(unique, test, null, id_test, testOutput);
	}


	public static void createARFF(ArrayList<index> wordList,
						ArrayList<ArrayList<String>> Review,
									ArrayList<String> classify,
									ArrayList<Integer> ID,
									File output)
	{
		long startTime=System.currentTimeMillis();
		System.out.println("Creating ARFF file");
		
		try{
		    PrintWriter writer = new PrintWriter(output);
		    writer.println("@RELATION review");
		    writer.println();
		    
		    for (index i: wordList){
		    	writer.println("@ATTRIBUTE " + i.getWord() + " NUMERIC");
		    }
		    writer.println("@ATTRIBUTE ID NUMERIC");
		    writer.println("@ATTRIBUTE classing {positive, negative, neutral}");
		    writer.println();
		    
		    writer.println("@DATA");
		    
		    // For each review
		    for (int num = 0; num < Review.size() ; num++){
		    	
		    	// For each word
				for (index i : wordList)
					writer.print(Collections.frequency( Review.get(num), i.getWord() ) + ",");
				
				writer.print( ID.get(num) + ",");
				
				// default to positive
		    	if (classify == null)
					writer.println("?");
		    	else
		    		writer.println(classify.get(num));
			}
		    writer.close();
		    
		} catch (IOException e) {
			System.out.println("Output file IOError");
			e.printStackTrace();
		}
		
		System.out.println("ARFF file created");
		System.out.println("Elapsed time: "+(System.currentTimeMillis()-startTime)*0.001+" seconds");
	}
}
