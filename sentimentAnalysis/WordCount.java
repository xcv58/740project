/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package CalculateSentiment;

import java.util.StringTokenizer;
import java.util.Random;
import java.net.URI;
// import java.io.IOException;
// import java.io.BufferedReader;
// import java.io.FileReader;
import java.io.*;

import CalculateSentiment.CalculateSentiment;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.filecache.DistributedCache;

public class WordCount {

    public static class TokenizerMapper 
	extends Mapper<Object, Text, Text, Text>{
    
	private Text word = new Text();
	private CalculateSentiment cal = WordCount.read("object");
	
	public void map(Object key, Text value, Context context
			) throws IOException, InterruptedException {
	    String line = value.toString().toLowerCase();
	    // CalculateSentiment cal = context.getConfiguration().get("category");

			String categoryName = context.getConfiguration().get("category");
			categoryName = categoryName.toLowerCase();
			word.set(line);
			double result = cal.judgeOneTweer(categoryName, line);
			// double result = 1;
			// double result = 1;
			// double doubleResult = Double.parseDouble(result);
			if (result > 0) {
				//DoubleWritable finalResult = new DoubleWritable(doubleResult);
				context.write(new Text(Double.toString(result)), word);
			}
	    // StringTokenizer itr = new StringTokenizer(line);
	    // while (itr.hasMoreTokens()) {
	    // 	String tmpToken = itr.nextToken();

	    // 	word.set(tmpToken);
	    // 	String result = cal.judgeOneTweer("", tmpToken);
	    // 	double doubleResult = Double.parseDouble(result);
	    // 	DoubleWritable finalResult = new DoubleWritable(doubleResult);

	    // 	context.write(word, finalResult);
	    // }
	}
    }
  
    public static class DoubleSumReducer 
	// extends Reducer<Text,DoubleWritable,Text,DoubleWritable> {
	extends Reducer<Text,Text,Text,Text> {
	// private DoubleWritable result = new DoubleWritable();

	public void reduce(Text key, Iterable<Text> values, 
			   Context context
			   ) throws IOException, InterruptedException {
	    double sum = 0;
			// for (DoubleWritable val : values) {
			// 	sum += val.get();
			// }
			for (Text tmpResult : values) {
				Text result = new Text(tmpResult);
				context.write(result, key);
			}
	    // result.set(sum);
	    // context.write(key, result);
			// context.write(result, key);
	}
    }
    
    public static CalculateSentiment read(String path) {
	try {
	    FileInputStream fileIn;
	    fileIn = new FileInputStream(path);
	    ObjectInputStream in = new ObjectInputStream(fileIn);
	    CalculateSentiment readObject = (CalculateSentiment) in.readObject();
	    in.close();
	    fileIn.close();
	    return readObject;
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public static void main(String[] args) throws Exception {
	Path tempDir = new Path("wordcount-temp-" + Integer.toString(new Random().nextInt(Integer.MAX_VALUE)));


	Configuration conf = new Configuration();
	String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
	if (otherArgs.length != 3) {
	    System.err.println("Usage: wordcount <in> <out> <category>");
	    System.exit(2);
	}
	conf.set("category", otherArgs[2]);

	// try {
	//     String filePath = otherArgs[0];
	//     BufferedReader br = new BufferedReader(new FileReader(filePath));
	//     String line = br.readLine();
	//     conf.set("category", line);
	// } catch (Exception e) {
	//     e.printStackTrace();
	// }
	// conf.set("category", WordCount.read(otherArgs[2]));
	

	DistributedCache.createSymlink(conf);
        String path = "CalculateSentiment.obj";
        Path filePath = new Path(path);
        String uriWithLink = filePath.toUri().toString() + "#" + "object";
        DistributedCache.addCacheFile(new URI(uriWithLink), conf);

	// DistributedCache.addCacheFile(new URI("/CalculateSentiment.obj"), conf);
	Job job = new Job(conf, "Test");

	job.setJarByClass(WordCount.class);
	job.setMapperClass(TokenizerMapper.class);
	job.setCombinerClass(DoubleSumReducer.class);
	job.setReducerClass(DoubleSumReducer.class);
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(Text.class);
	job.setNumReduceTasks(1);
	FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
