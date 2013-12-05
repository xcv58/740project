package CalculateSentiment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/*
 * Store the categories and product keywords.
 * Use HashMap<String, HashSet<String>
 * Store the sentiment words information
 * By HashpMap<String, String>;
 * the second String look like a "-4.96792,-7.04730"
 * first one means happy, second one means sad
 */
public class CalculateSentiment implements Serializable {
	private static final long serialVersionUID = 7526472295622754748L;

	private HashMap<String, HashSet<String>> categoryKeywords;
	private HashMap<String, String> sentimentMap;
	
	public CalculateSentiment() {
		this.categoryKeywords = new HashMap<String, HashSet<String>>();
		this.sentimentMap = new HashMap<String, String>();
	}
	public boolean addSentiment(String word, String score) {
		if (this.sentimentMap.containsKey(word)) {
			return false;
		} else {
			this.sentimentMap.put(word, score);
			return true;
		}
	}
	
	// TODO: preprocessing
	private String preprocessing(String line) {
		return line;
	}
	
	public String judgeOneTweer (String categoryName, String tweet) {
		//TODO: judge tweet is a description of this category
		String tmpTweet = tweet.toLowerCase();
		double score = this.calculateSentiment(tmpTweet.split("\\s+"));
		return Double.toString(score);
	}
	
	private double calculateSentiment(String[] tokens) {
		double happyScore = 0;
		double sadScore = 0;
		for (String tmp:tokens) {
			String scoreString = this.sentimentMap.get(tmp);
			if (scoreString != null) {
				int anchor = scoreString.indexOf(',');
				if (anchor != -1) {
					happyScore += Double.parseDouble(scoreString.substring(0, anchor));
					sadScore += Double.parseDouble(scoreString.substring(anchor + 1));
				}
			}
		}
		double finalScore = 1 / (Math.exp(sadScore - happyScore) + 1);
		return finalScore;
	}
	
	public static void main(String[] args) {
		String scoreString = "-4.96792,-7.04730";
		double happyScore = 0;
		double sadScore = 0;
		int anchor = scoreString.indexOf(',');
		if (anchor != -1) {
			happyScore += Double.parseDouble(scoreString.substring(0, anchor));
			sadScore += Double.parseDouble(scoreString.substring(anchor + 1));
		}
		double finalScore = 1 / (Math.exp(sadScore - happyScore) + 1);
		
		System.out.println(happyScore);
		System.out.println(sadScore);
		System.out.println(finalScore);
	}
}
