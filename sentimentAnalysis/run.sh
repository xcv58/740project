rm -r CalculateSentiment
rm a.jar
javac -classpath $HADOOP_INSTALL/share/hadoop/common/hadoop-common-2.2.0.jar:$HADOOP_INSTALL/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.2.0.jar:$HADOOP_INSTALL/share/hadoop/common/lib/commons-cli-1.2.jar -d . CalculateSentiment.java WordCount.java
jar -cvf a.jar  CalculateSentiment
hadoop fs -rm -r output
hadoop jar ./a.jar CalculateSentiment.WordCount test.txt output laptops
hadoop fs -cat output/part-r-00000
