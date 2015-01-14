package com.naveen.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.ColumnRangeFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.regionserver.HRegion.RowLock;
import org.apache.hadoop.hbase.util.Bytes;

import com.naveen.beans.Product;
import com.naveen.util.Constants;

public class ProductInfo {
	
	public Properties getProperties(String propFileName){
		
		//Below logic is to read property file for HBase configuration
		Properties prop = new Properties();
		
		
		
		
		InputStream inputStream = null;
		
		try{
		    //inputStream = new FileInputStream(propFileName);
			
			inputStream = getClass().getResourceAsStream(propFileName);
			
			prop.load(inputStream);	    


		}
		
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return prop;
	}

	@SuppressWarnings({ "null", "deprecation" })
	public Product getProductAvailability(String productId, int unitRequested){
		
		HashMap<String,Integer> storeDetails = new HashMap<String,Integer>();
		HashMap<String,Integer> vendorDetails = new HashMap<String,Integer>();
		HashMap<String,Integer> fcDetails = new HashMap<String,Integer>();
		
		HashMap<String, HashMap<String,Integer>> mp = new HashMap<String, HashMap<String,Integer>>();
		
		Product product = null;
		
		product.setProductId(productId);
		
		//Below logic is to read property file for HBase configuration
		
		String propFileName = Constants.HBASE_PROPERTIES;
		getProperties(propFileName);
		Properties prop = getProperties(propFileName);
		
		HTable hTable = null;
		
		try{
			Configuration hConf = HBaseConfiguration.create();
			hConf.set("hbase.zookeeper.quorum", prop.getProperty("hbase.zookeeper.quorum"));

			hConf.set("hbase.zookeeper.property.clientPort", prop.getProperty("hbase.zookeeper.property.clientPort"));
			hTable = new HTable(hConf, prop.getProperty("hbase.table"));
			
			Get get = new Get(Bytes.toBytes(productId));
			
			get.addFamily(Bytes.toBytes(prop.getProperty("hbase.table.cf1")));
			Result result = hTable.get(get);
			
			
			//Fetching the details from the Result object
			product.setDescription(result.getValue(Bytes.toBytes(prop.getProperty("hbase.table.cf1")), Bytes.toBytes(prop.getProperty("hbase.table.cq1"))).toString());
			
			product.setPrice(Bytes.toDouble(result.getValue(Bytes.toBytes(prop.getProperty("hbase.table.cf1")), Bytes.toBytes(prop.getProperty("hbase.table.cq3")))));
			
			product.setCategory(result.getValue(Bytes.toBytes(prop.getProperty("hbase.table.cf1")), Bytes.toBytes(prop.getProperty("hbase.table.cq2"))).toString());
			
			
			
			//Creating a Scan object
			Scan scan = new Scan(Bytes.toBytes(unitRequested),Bytes.toBytes(unitRequested + 1));
			
			byte[] startColumn = Bytes.toBytes("0");
			
			byte[] endColumn = Bytes.toBytes("9");
			
			
			//Creating a filter object for the Hbase scan for fetching the columns
			Filter filter = new ColumnRangeFilter(startColumn, true, endColumn, true);
			
			scan.setFilter(filter);
			scan.addFamily(Bytes.toBytes(prop.getProperty("hbase.table.cf2")));
			scan.setBatch(20);
			
			ResultScanner rs = hTable.getScanner(scan);
			
			//Looping over the resultscanner
			
			for (Result r = rs.next(); r != null; r = rs.next()){
				
				for(KeyValue kv: r.raw()){
					if(kv.getQualifier().toString().contains("store")){
						storeDetails.put(kv.getQualifier().toString(), Bytes.toInt(kv.getValue()));
					}
					
					else if(kv.getQualifier().toString().contains("vendor")){
						vendorDetails.put(kv.getQualifier().toString(), Bytes.toInt(kv.getValue()));
					}
					
					else{
						fcDetails.put(kv.getQualifier().toString(), Bytes.toInt(kv.getValue()));
					}
					
					if(Bytes.toInt(kv.getValue())> 0)
						product.setStatus(true);
				}
				
			}
			
			//Storing the Store, Vendor and Fullfillment Center details
			
			
			mp.put("Store", storeDetails);
			mp.put("Vedor", vendorDetails);
			mp.put("FullfillmentCenter", fcDetails);
			
			
			product.setAvailability(mp);
		}
		
		catch(IOException e){
			e.printStackTrace();
			
		}
		
		return product;
	}
	
	/*
	 * Below method is to book 
	 */
	/*
	public String bookProduct(String productId, String quantity ) {
		
		//Below logic is to read property file for HBase configuration
		String propFileName = "/input.properties";
		getProperties(propFileName);
		Properties prop = getProperties(propFileName);
		
		
		HTable hTable = null;
		
		try{
			Configuration hConf = HBaseConfiguration.create();
			hConf.set("hbase.zookeeper.quorum", prop.getProperty("hbase.zookeeper.quorum"));

			hConf.set("hbase.zookeeper.property.clientPort", prop.getProperty("hbase.zookeeper.property.clientPort"));
			hTable = new HTable(hConf, prop.getProperty("hbase.table"));
			
			

		
	}
	*/
}
