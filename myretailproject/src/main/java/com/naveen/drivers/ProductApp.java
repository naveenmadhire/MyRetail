package com.naveen.drivers;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.naveen.beans.Product;

/*
 * author "Naveen Madhire"
 * 
 * Class is used for creating a client app for calling the REST API
 */
public class ProductApp {
	
	
	private Client client;
	
	public ProductApp(){
		client = ClientBuilder.newClient();
	}
	
	public Product get(String productId, String unitRequested){
		WebTarget target = client.target("http://localhost:1025/myretailproject/webapi/");
		
		
		Response response = target.path("query?" + productId + unitRequested).request(MediaType.APPLICATION_JSON).get(Response.class);
		
		if(response.getStatus()!=200){
			throw new RuntimeException(response.getStatus() + ": Bad return code. Check the input values or the application code");
		}
		
		return response.readEntity(Product.class);
	}

}
