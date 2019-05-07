# Yelp-Review-Data-Preprocessing
Obtain absolute frequency of unique words, remove their stems, stop-words and infrequent words

SOFTWARE USED: WEKA (collection of machine learning algorithms for data mining tasks) AND ECLIPSE (Java IDE)
- https://www.cs.waikato.ac.nz/ml/weka/downloading.html 
- https://www.eclipse.org/downloads/

The goal of this project is to take 50,000 yelp reviews obtained from the Yelp Open Dataset (https://www.yelp.com/dataset) and build  a classifier based on the training data.
40,000 of the reviews were randomly selected from the data set as training data and another 10,000 were used as the test set.
Both the training and test set contains an ID number and the user review in text but only the training set has a class label (which has one of the values: positive, negative or neutral).

PREPROCESSING.JAVA:

- This file has the main method that filters all the stop-words and infrequent words after stemming. 
- Then, it creates the corresponding arff files of the training and test set.
- All the other files just supplement methods to this java class.

STEMMER.JAVA: https://tartarus.org/martin/PorterStemmer/

- This java class basically stems a word (provided by Preprocessing.java) and returned in its stemmed version.
- Ex: discussed/discusses -> discuss

INDEX.JAVA:

- This java class stores a word and its frequency in a document.
- Mainly used to prune infrequent words.

HOW TO COMPILE THROUGH COMMAND LINE

1. Open command prompt
2. Change directory to the src folder
3. Compile by typing "javac index.java Stemmer.java Preprocessing.java" (without the quotation marks)
4. Run the file by typing "java Preprocessing" (without the quotation marks)

Runtime: Approximately 530 seconds

The rest is done in Weka
