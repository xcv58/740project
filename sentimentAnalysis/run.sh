rm -r CalculateSentiment
rm a.jar
javac -classpath $HADOOP_INSTALL/share/hadoop/common/hadoop-common-2.2.0.jar:$HADOOP_INSTALL/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.2.0.jar:$HADOOP_INSTALL/share/hadoop/common/lib/commons-cli-1.2.jar:/Users/xcv58/Downloads/eclipse/plugins/org.apache.commons.lang_2.6.0.v201205030909.jar -d . CalculateSentiment.java WordCount.java
jar -cvf a.jar  CalculateSentiment
hadoop fs -rm -r output
hadoop fs -put -f CalculateSentiment.obj CalculateSentiment.obj
hadoop jar ./a.jar CalculateSentiment.WordCount tweets.txt output laptops
# hadoop jar ./a.jar CalculateSentiment.WordCount test.txt output laptops
hadoop fs -cat output/part-r-00000
