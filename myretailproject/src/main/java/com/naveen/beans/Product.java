package com.naveen.beans;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Product {
	
	private String productId;
	
	private String description;
	
	private String category;
	
	private double price;
	
	//Status variable is for checking the product availability 
	private boolean status = false;
	
	
	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	//Instance variable to store the details of Stores,vendors and fulfillment centers
	private HashMap<String, HashMap<String,Integer>> availability;

	public HashMap<String, HashMap<String, Integer>> getAvailability() {
		return availability;
	}

	public void setAvailability(
			HashMap<String, HashMap<String, Integer>> availability) {
		this.availability = availability;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	

}
