package CalculateSentiment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

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

	private HashMap<String, HashMap<String, Float>> categoryKeywords;
	private HashMap<String, String> sentimentMap;
	private Pattern findTweet;
	private Pattern REPLACEMENTION_PATTERN;
	private double leastTokens;
	
	public CalculateSentiment() {
		this.sentimentMap = new HashMap<String, String>();
		this.findTweet = Pattern.compile("(?<=,\")[^\"]+(?=\"$)");
		this.REPLACEMENTION_PATTERN = Pattern.compile("\\@\\w+ ");
		this.leastTokens = 50;
	}
	public boolean addSentiment(String word, String score) {
		if (this.sentimentMap.containsKey(word)) {
			return false;
		} else {
			this.sentimentMap.put(word, score);
			return true;
		}
	}
	
	public void addCategoryInfo(HashMap<String, HashMap<String, Float>> hashmap) {
		this.categoryKeywords = hashmap;
	}
	
	// TODO: preprocessing
	private String preprocessing(String line) {
		String result = line.toLowerCase();
		Matcher m = findTweet.matcher(result);
		if (m.find()) {
			result = m.group();
		}
		Matcher replaceMatcher = REPLACEMENTION_PATTERN.matcher(result);
		if (replaceMatcher.find()) {
			result = replaceMatcher.replaceAll("");
		}
		result = StringEscapeUtils.unescapeCsv(result);
		result = StringEscapeUtils.unescapeXml(result);
		return result;
	}
	
	private boolean isCategory (String categoryName, String tweet) {
		if (this.categoryKeywords == null) {
			System.err.println("NOT ADD CATEGORY KEYWORDS INFORMATION!!!");
			return false;
		}
		HashMap<String, Float> categoryMap = this.categoryKeywords.get(categoryName);
		StringTokenizer tokens = new StringTokenizer(tweet.toLowerCase());
		float matchScore = 0;
		int matchSum = 0;
		if (categoryMap == null) {
			return false;
		}
		HashSet<String> dupSet = new HashSet<String>();
		while (tokens.hasMoreTokens()) {
			String tmpToken = tokens.nextToken();
			if (dupSet.contains(tmpToken)) {
				continue;
			}
			dupSet.add(tmpToken);
			if (categoryMap.containsKey(tmpToken)) {
				System.out.println(tmpToken);
				matchSum++;
				float tmpScore = categoryMap.get(tmpToken);
				System.out.println(tmpScore);
				matchScore += tmpScore;
			}
		}
		if ((matchScore / matchSum) >= this.leastTokens && matchSum > 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public double judgeOneTweer (String categoryName, String tweet) {
		//TODO: judge tweet is a description of this category
		String tmpTweet = this.preprocessing(tweet);
		System.out.println(tmpTweet);
		if (categoryName.equals("") || categoryName.isEmpty() || categoryName == null) {
			double score = this.calculateSentiment(tmpTweet.split("\\s+"));
			return score;
		}
		if (this.isCategory(categoryName, tmpTweet)) {
			double score = this.calculateSentiment(tmpTweet.split("\\s+"));
			return score;
		} else {
			return -1;
		}
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
	
//	public static void main(String[] args) {
//		String scoreString = "-4.96792,-7.04730";
//		double happyScore = 0;
//		double sadScore = 0;
//		int anchor = scoreString.indexOf(',');
//		if (anchor != -1) {
//			happyScore += Double.parseDouble(scoreString.substring(0, anchor));
//			sadScore += Double.parseDouble(scoreString.substring(anchor + 1));
//		}
//		double finalScore = 1 / (Math.exp(sadScore - happyScore) + 1);
//		
//		System.out.println(happyScore);
//		System.out.println(sadScore);
//		System.out.println(finalScore);
//	}
}
