package com.naveen.mappers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import com.naveen.util.Constants;

public class BatchUploadMapper extends Mapper <LongWritable,Text,ImmutableBytesWritable,KeyValue> {
	
	
	Logger logger = Logger.getLogger(BatchUploadMapper.class);
	
	//Static variable for referring the property file
	
	public static Properties prop = new Properties();
	
	@Override
	protected void setup(Context context){
		
		
		String propFileName = Constants.HBASE_PROPERTIES;
		
		InputStream inputStream = null;
		
		try{
			
			inputStream = getClass().getResourceAsStream(propFileName);
			
			logger.info("Naveen1:" + inputStream);
			prop.load(inputStream);
			
			logger.info("Naveen2");
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	protected void map(LongWritable key, Text value, Context context){
		
		logger.info("In map function");
		
		ImmutableBytesWritable rowkeyInBytes = new ImmutableBytesWritable();
		
		
		//Considering the Input Record to be in the below format
		//ProductId,Description,Category,Price,Location_Category,Store_id,Store_Units
		
		String [] inputRecord = value.toString().split("\t");
		
		rowkeyInBytes.set(Bytes.toBytes(inputRecord[0]));
		
		//The variable is to store the keyvalue values
		List <KeyValue> listofKeyValue = new ArrayList<KeyValue>();
		
		//Creating KeyValue for Description
		listofKeyValue.add(new KeyValue(Bytes.toBytes(inputRecord[0]),Bytes.toBytes(prop.getProperty("hbase.table.cf1")),
										 Bytes.toBytes(prop.getProperty("hbase.table.cq1")),Bytes.toBytes(inputRecord[1])));
		
		
		//Creating KeyValue for Category
		listofKeyValue.add(new KeyValue(Bytes.toBytes(inputRecord[0]),Bytes.toBytes(prop.getProperty("hbase.table.cf1")),
				 Bytes.toBytes(prop.getProperty("hbase.table.cq2")),Bytes.toBytes(inputRecord[2])));
		
		
		//Creating KeyValue for Price
		listofKeyValue.add(new KeyValue(Bytes.toBytes(inputRecord[0]),Bytes.toBytes(prop.getProperty("hbase.table.cf1")),
				 Bytes.toBytes(prop.getProperty("hbase.table.cq3")),Bytes.toBytes(inputRecord[3])));

		
		/*The variable is to store the location category.
		 * 
		 *  S = Store
		 *  V = Vendor
		 *  F = Fullfillment Center
		 */
		String locationCategory = new String(inputRecord[4]);
		
		if(locationCategory.equals("S"))
			listofKeyValue.add(new KeyValue(Bytes.toBytes(inputRecord[0]),Bytes.toBytes(prop.getProperty("hbase.table.cf2")),
				 Bytes.toBytes(inputRecord[5].concat("_Store")),Bytes.toBytes(inputRecord[6])));
		
		else if(locationCategory.equals("V"))
			listofKeyValue.add(new KeyValue(Bytes.toBytes(inputRecord[0]),Bytes.toBytes(prop.getProperty("hbase.table.cf2")),
					 Bytes.toBytes(inputRecord[5].concat("_Vendor")),Bytes.toBytes(inputRecord[6])));
		
		else
			listofKeyValue.add(new KeyValue(Bytes.toBytes(inputRecord[0]),Bytes.toBytes(prop.getProperty("hbase.table.cf2")),
					 Bytes.toBytes(inputRecord[5].concat("_FC")),Bytes.toBytes(inputRecord[6])));
		
		
		try {
			
			//Writing all the keyvalue as context objects.
			
			for(KeyValue k:listofKeyValue)
				context.write(rowkeyInBytes,k);
		} catch (IOException e) {
			logger.error("IO exception occurred during writing context object");
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("Interrupted error occurred during writing context object");
			e.printStackTrace();
		}
		
	}

}
