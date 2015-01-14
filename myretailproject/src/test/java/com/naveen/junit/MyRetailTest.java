package com.naveen.junit;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.naveen.beans.Product;
import com.naveen.drivers.ProductApp;


public class MyRetailTest {
	
	@Test
	public void testGet() {
		ProductApp client = new ProductApp();
		
		Product product = client.get("123466","1");
		
		System.out.println(product);
		assertNotNull(product);
	}

}
