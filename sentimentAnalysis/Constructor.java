package CalculateSentiment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;

public class Constructor {

	private final static Pattern findTweet = Pattern
			.compile("(?<=,\")[^\"]+(?=\"$)");
	private final static Pattern REPLACEMENTION_PATTERN = Pattern
			.compile("\\@\\w+ ");
	final static String[] stopWords = { "a", "able", "about", "across", "after", "all",
		"almost", "also", "am", "among", "an", "and", "any", "are",
		"as", "at", "be", "because", "been", "but", "by", "can",
		"cannot", "could", "dear", "did", "do", "does", "either",
		"else", "ever", "every", "for", "from", "get", "got", "had",
		"has", "have", "he", "her", "hers", "him", "his", "how",
		"however", "ƒi", "if", "in", "into", "is", "it", "its", "just",
		"least", "let", "like", "likely", "may", "me", "might", "most",
		"must", "my", "neither", "no", "nor", "not", "of", "off",
		"often", "on", "only", "or", "other", "our", "own", "rather",
		"said", "say", "says", "she", "should", "since", "so", "some",
		"than", "that", "the", "their", "them", "then", "there",
		"these", "they", "this", "tis", "to", "too", "twas", "us",
		"wants", "was", "we", "were", "what", "when", "where", "which",
		"while", "who", "whom", "why", "will", "with", "would", "yet",
		"you", "your" };	
	final static HashSet<String> stopWordSet = new HashSet<String>(
			Arrays.asList(stopWords));

	private HashMap<String, Integer> allTokenHashMap;
	private HashMap<String, HashMap<String, Integer>> categoryMap;
	
	public Constructor() {
		this.allTokenHashMap = new HashMap<String, Integer>();
		this.categoryMap = new HashMap<String, HashMap<String, Integer>>();
	}
	
	
	private CalculateSentiment read(String path) {
		try {
			FileInputStream fileIn;
			fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			CalculateSentiment readObject = (CalculateSentiment) in
					.readObject();
			in.close();
			fileIn.close();
			return readObject;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	private void addLineToMap(String line, String CategoryName) {
		List<String> tokenList = this.getToken(line);
		HashMap<String, Integer> tmpCategoryMap = this.categoryMap.get(CategoryName);
		if (tmpCategoryMap == null) {
			tmpCategoryMap = new HashMap<String, Integer>();
			this.categoryMap.put(CategoryName, tmpCategoryMap);
		}
		for (String oneToken : tokenList) {
			Integer intForCategory = tmpCategoryMap.get(oneToken);
			if (intForCategory == null) {
				tmpCategoryMap.put(oneToken, new Integer(1));
				Integer intForAll = this.allTokenHashMap.get(oneToken);
				if (intForAll == null) {
					this.allTokenHashMap.put(oneToken, new Integer(1));
				} else {
					this.allTokenHashMap.put(oneToken, intForAll + 1);
				}
			} else {
				tmpCategoryMap.put(oneToken, intForCategory + 1);
			}
		}
//		this.allTokenHashMap.put(key, value)
	}
	
	private List<String> getToken(String line) {
		String result = line.toLowerCase();
		ArrayList<String> resultArrayList = new ArrayList<String>();
		PTBTokenizer ptbt = new PTBTokenizer(
				new StringReader(line),
				new CoreLabelTokenFactory(), "");
		for (CoreLabel label; ptbt.hasNext();) {
			label = (CoreLabel) ptbt.next();
			String tmpString = label.toString();
			if (tmpString.length() == 1) {
			} else if (tmpString.equals("''") || tmpString.equals("-") || tmpString.equals("™")) {
			} else {
				tmpString = tmpString.toLowerCase();
				if (!this.stopWordSet.contains(tmpString)) {
					resultArrayList.add(tmpString);
				}
			}
		}
		return resultArrayList;
	}
	
	private void countTFIDF(CalculateSentiment cal) {
		long allTokenNumber = 0;
		long allTokenCount = 0;
		int categorySize = this.categoryMap.size();
		HashMap<String, HashMap<String,Float>> finalMap = new HashMap<String, HashMap<String,Float>>(categorySize);
		allTokenNumber = this.allTokenHashMap.size();
		Iterator e = this.allTokenHashMap.values().iterator();
		while(e.hasNext())
		{
			Integer number = (Integer) e.next();
			allTokenCount = allTokenCount + number;
//			Object key = entry.getKey();
//			Object val = entry.getValue();
		}
		e = this.categoryMap.entrySet().iterator();
		while (e.hasNext()) {
			Map.Entry entry = (java.util.Map.Entry) e.next();
			String tmpCategoryName = (String) entry.getKey();
			HashMap<String, Integer> categoryMap = (HashMap<String, Integer>) entry.getValue();
			Iterator valueIterator = categoryMap.values().iterator();
			HashMap<String, Float> tmpHashMap = new HashMap<String, Float>(categoryMap.size());
			long tmpAllToken = 0;
			while (valueIterator.hasNext()) {
				int tmpInt = (Integer) valueIterator.next();
				tmpAllToken += (long) tmpInt;
			}
			Iterator iteratorForCategory = categoryMap.entrySet().iterator();
			System.out.println(tmpCategoryName);
			while (iteratorForCategory.hasNext()) {
				Map.Entry<String, Integer> entryStringInt = (java.util.Map.Entry<String, Integer>) iteratorForCategory.next();
				String tmpToken = entryStringInt.getKey();
				Integer countInCategory = entryStringInt.getValue();
				Integer countInAll = this.allTokenHashMap.get(tmpToken);
				float tmpTFIDF = countInCategory * allTokenCount / tmpAllToken / countInAll;
				tmpHashMap.put(tmpToken, tmpTFIDF);
				System.out.println(tmpToken + ": " + tmpTFIDF + "= / " + tmpAllToken + "*"+ countInCategory + " * " + allTokenCount + " / " + countInAll );
			}
			finalMap.put(tmpCategoryName, tmpHashMap);
		}
		cal.addCategoryInfo(finalMap);
//		Set<Map.Entry<String,Integer>> entrySet = this.allTokenHashMap.entrySet();
//		Iterator e = entrySet.iterator();
//		while (e.hasNext()) {
//			Entry entry = (Entry) e.next();
////			entry
//		}
		System.out.println(allTokenNumber);
		System.out.println(allTokenCount);
	}
	
	
	private void addCategoryInfo (CalculateSentiment cal,String productsFile) {
		try {
			long sum = 0;
			BufferedReader br = new BufferedReader(new FileReader(productsFile));
			String line = br.readLine();
			boolean isproduct = false;
			String tmpCategoryName = "";
			while ((line = br.readLine()) != null) {
				sum++;
				if (sum % 10000 == 0) {
					System.out.println(sum);
				}
				if (isproduct) {
					if (line.startsWith("</products>")) {
						isproduct = false;
					} else {
						this.addLineToMap(line, tmpCategoryName);
					}
				} else if (line.startsWith("<name>")) {
					line = line.substring(6, line.length() - 7);
					line = line.toLowerCase();
					if (line.startsWith("all ")) {
						line = line.substring(4);
					}
					if (line.contains("laptops")) {
						line = "laptops";
					}
					tmpCategoryName = line;
					isproduct = true;
					br.readLine();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.countTFIDF(cal);
	}

	private void addSentiment(CalculateSentiment cal, String sentimentFile) {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(sentimentFile));
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				int anchor = line.indexOf(',');
				if (anchor != -1) {
					cal.addSentiment(line.substring(0, anchor),
							line.substring(anchor + 1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void process(CalculateSentiment cal, String sentimentFile, String productsFile, String objectFile) {
		this.addSentiment(cal, sentimentFile);
		this.addCategoryInfo(cal, productsFile);
		try {
			FileOutputStream fileOut = new FileOutputStream(objectFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(cal);
			out.close();
			fileOut.close();
			System.out.println("Serialized data is saved in " + objectFile);
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String productsFile = "/Users/xcv58/740_project/products.xml";
		// productsFile = "/Users/xcv58/740_project/tweets.csv";
		String sentimentFile = "/Users/xcv58/740_project/sentiment.csv";
		String objectFile = "/Users/xcv58/740_project/sentimentAnalysis/CalculateSentiment.obj";
		CalculateSentiment calculateSentiment = new CalculateSentiment();
		Constructor constructor = new Constructor();
		
		constructor.process(calculateSentiment, sentimentFile, productsFile, objectFile);

		CalculateSentiment c = constructor.read(objectFile);

//		double result;
//		String tweet;
//		tweet = "I love holidays";
//		result = c.judgeOneTweer("", tweet);
//		System.out.println(result);
//		tweet = "very sad";
//		result = c.judgeOneTweer("", tweet);
//		System.out.println(result);

		try {
			while (true) {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Enter String");
				String newtweet = br.readLine();
				// String newtweet =
				// "missing uw and my chi o friends!! mv kinda sucks ";
				double doubleResult = c.judgeOneTweer("laptops", newtweet);
				System.out.println(doubleResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
