package yf.project;
//package com.deb.mapreduce;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextOutputFormat;


public class ImportReview {

	static String DELIMITER = ",";

	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

		// 1, "one tow three" -> one, 1 and two ,1 and three,1
		@Override
		public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) 
				throws IOException {

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();

			XMLParser xP;
			try {
				xP = new XMLParser(value.toString());

				//List<String> categories = xP.getCategory();
				//List<String> reviewLists = xP.getReviews();
				List<String> tagNameLists = xP.getTagName();
				List<String> tagCountLists = xP.getTagCount();
/*
				for (String eachReview : reviewLists) {
					if (BadWords.isBad(eachReview)) {
						negative++;
					} else {
						postive++;
					}
				}

				for (String eachCat : categories) {
					//String mapOutput = WordUtil.cleanWords(eachCat) + DELIMITER + xP.getHash() + DELIMITER + xP.getUrl() + DELIMITER + xP.getUsercount() + DELIMITER + tagNameLists.toString() + DELIMITER + tagCountLists.toString();
					String mapOutput = WordUtil.cleanWords(eachCat) + DELIMITER + xP.getHash() + DELIMITER + xP.getUrl() + DELIMITER + xP.getUsercount() + DELIMITER +xP.getCategory();
							//xP.getTagName() + DELIMITER + xP.getTagCount();
					output.collect(new Text(""), new Text(mapOutput));
				}
*/				
				for (int i=0; i < tagNameLists.size(); i++) {
					String mapOutput = WordUtil.cleanWords(tagNameLists.get(i)) + DELIMITER + tagCountLists.get(i) + DELIMITER + xP.getHash() + DELIMITER + xP.getUrl() + DELIMITER + xP.getUsercount() + DELIMITER +xP.getCategory() + DELIMITER + dateFormat.format(date);
					output.collect(new Text(""), new Text(mapOutput));
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("");
			}


		}

	}

	public static void main(String[] args) throws Exception {

		JobConf conf = new JobConf(ImportReview.class);
		//conf.set("fs.default.name", "hdfs://localhost:54310");
		conf.set(XmlInputFormat.START_TAG_KEY, "<document>");
		conf.set(XmlInputFormat.END_TAG_KEY, "</document>");
		conf.setJobName("importreview");
		conf.setNumReduceTasks(0);
		conf.setMapperClass(Map.class);

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);

		// how the data will be read
		conf.setInputFormat(XmlInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		JobClient.runJob(conf);

	}
}
