/*
Creator: Kevin Tang
Date: Mar 28, 2019
Description: Used in Preprocessing.java for counting frequency of unique words
*/
public class index implements Comparable{
	public String word;
	public int count;
	
	public index(String word, int count) {
		this.word = word;
		this.count = count;
	}
	
	public String getWord() {
		return this.word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void increment(){
		this.count++;
	}
	
	// most frequent word in the front of the list
	@Override
	public int compareTo(Object o) {
		if (this.count < ((index)o).getCount() )
			return 1;
		else if (this.count > ((index)o).getCount())
			return -1;
		return 0;
	}
}
