if [ $# -eq 0 ]
then
	echo "No arguments supplied, use default category: Laptops"
	category="Laptops"
else
	category=$1
fi
rm -r CalculateSentiment >/dev/null
rm a.jar >/dev/null
javac -classpath $HADOOP_INSTALL/share/hadoop/common/hadoop-common-2.2.0.jar:$HADOOP_INSTALL/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.2.0.jar:$HADOOP_INSTALL/share/hadoop/common/lib/commons-cli-1.2.jar:./org.apache.commons.lang_2.6.0.v201205030909.jar -d . CalculateSentiment.java WordCount.java >/dev/null
jar -cvf a.jar  CalculateSentiment >/dev/null
hadoop fs -rm -r output >/dev/null
hadoop fs -put -f CalculateSentiment.obj CalculateSentiment.obj >/dev/null
# hadoop jar ./a.jar CalculateSentiment.WordCount tweets.txt output tablet
hadoop jar ./a.jar CalculateSentiment.WordCount tweets.txt output $category
# hadoop jar ./a.jar CalculateSentiment.WordCount test.txt output laptops
hadoop fs -cat output/part-r-00000 > ~/temp.out
