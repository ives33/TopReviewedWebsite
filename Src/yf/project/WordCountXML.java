package yf.project;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextOutputFormat;

public class WordCountXML {

	static String DELIMITER = ",";

	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			XMLParserReview xPR;
			try {
				xPR = new XMLParserReview(value.toString());
				
				String hash = xPR.getHashR();
				List<String> reviewLists = xPR.getReviews();

				for (String eachReview : reviewLists) {
					//String mapOutput = WordUtil.cleanWords(eachCat) + DELIMITER + xP.getHash() + DELIMITER + xP.getUrl() + DELIMITER + xP.getUsercount() + DELIMITER + tagNameLists.toString() + DELIMITER + tagCountLists.toString();
					String line = WordUtil.cleanWords(eachReview); 	
					StringTokenizer tokenizer = new StringTokenizer(line);
					while (tokenizer.hasMoreTokens()) {
						word.set(hash + "," + tokenizer.nextToken());
						output.collect(word, one);
					}	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("");
			}
		}
	}

	public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, Text> {
		public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, Text> output,
				Reporter reporter) throws IOException {
			int sum = 0;
			while (values.hasNext()) {
				sum += values.next().get();
			}
			String redOutput = key + DELIMITER + String.valueOf(sum);
			output.collect(new Text(""), new Text(redOutput));
		}
	}

	public static void main(String[] args) throws Exception {
		JobConf conf = new JobConf(WordCountXML.class);
		conf.set(XmlInputFormat.START_TAG_KEY, "<document>");
		conf.set(XmlInputFormat.END_TAG_KEY, "</document>");
		conf.setJobName("WordCountWithoutCombiner");
		//nb of outfile = nb of reducers
		//set nb of reducer to 3 
		//conf.setNumReduceTasks(3);
		
		//is the final file for output on HDFS
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);
		//the logic in the mapper is transformation, select field, filtering
		conf.setMapperClass(Map.class);
		//set the combiner to reduce disk IO and network IO
		//conf.setCombinerClass(Reduce.class);
		//increase the number of reduced tasks
		conf.setReducerClass(Reduce.class);
		//set the partitioner after creating MyPartitioner.class
		//conf.setPartitionerClass(partitioner.class);
		
		//splits code into chunk then into records then into k,v
		conf.setInputFormat(XmlInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		//is HDFS data location
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		//note: reporter is an API to track job progress
		JobClient.runJob(conf);
	}
}