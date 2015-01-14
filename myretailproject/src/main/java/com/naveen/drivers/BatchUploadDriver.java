package com.naveen.drivers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import com.naveen.mappers.BatchUploadMapper;
import com.naveen.util.Constants;

/*
 * This Class is the driver program for inserting into HBase Product table
 */
public class BatchUploadDriver extends Configured implements Tool{
	
	static Logger logger = Logger.getLogger(BatchUploadDriver.class);
	
	public static void main(String[] args) {
		
		
		try {
			ToolRunner.run(new Configuration(), new BatchUploadDriver(), args);
		}
		
		catch( Exception e ){
			e.getStackTrace();
		}

	}
	
	@SuppressWarnings("deprecation")
	public int run(String[] args) throws Exception{
		
		
		//Load the property file which can be used for reading the constants
		
		Properties prop = new Properties();
		
		String propFileName = Constants.HBASE_PROPERTIES;
		
		InputStream inputStream = null;
		
		try{
		    logger.info("Inside the try block");
		    //inputStream = new FileInputStream(propFileName);
			
			inputStream = getClass().getResourceAsStream(propFileName);
			
			logger.info("Naveen1:" + inputStream);
			prop.load(inputStream);
			
			logger.info("Naveen2");
			
		    args[0] = prop.getProperty("input.file.location");
		    
		    args[1] = prop.getProperty("output.file.location");
		    


		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		boolean jobStatus = false;
		logger.info("Inside the run method");
		
		Configuration conf = this.getConf();
		
		try{
			
			Job job = new Job(conf,"MapReduce Job to insert into HBase table");
			
			Configuration hConf = HBaseConfiguration.create();
			
			hConf.set("hbase.zookeeper.quorum", prop.getProperty("hbase.zookeeper.quorum"));
			
			hConf.set("hbase.zookeeper.property.clientPort", prop.getProperty("hbase.zookeeper.property.clientPort"));
			
			HTable hTable = new HTable(hConf, prop.getProperty("hbase.table"));
			
			Path inputPath = new Path(args[0].trim());
			
			Path outputPath = new Path(args[1].trim());
			
			//Set the configuration settings for the job
			job.setJarByClass(BatchUploadDriver.class);
			
			job.setMapperClass(BatchUploadMapper.class);
			
			job.setMapOutputKeyClass(ImmutableBytesWritable.class);
			
			job.setMapOutputValueClass(KeyValue.class);
			
			HFileOutputFormat2.configureIncrementalLoad(job, hTable);
			
			FileSystem fs = FileSystem.get(conf);
			
			FileInputFormat.setInputPaths(job, inputPath);
			
			if(fs.exists(outputPath)){
				fs.delete(outputPath,true);
			}
			
			
			FileOutputFormat.setOutputPath(job, outputPath);
			
			logger.info("Checking the job status");
			jobStatus = job.waitForCompletion(true);
			
			
			if(jobStatus){
				
				logger.info("job is successful.Submitting the insert to HBase table");
				
				//SchemaMetrics.configureGlobally(hConf);
				
				LoadIncrementalHFiles loader = new LoadIncrementalHFiles(hConf);
				
				loader.doBulkLoad(outputPath,hTable);
			}
			
			return 0;
			
		}
		
		catch(IOException e){
			return 1;
		}
		
		catch(InterruptedException e){
			return 1;
		}
		
		catch(ClassNotFoundException e){
			return 1;
		}
	}
	
	

}
