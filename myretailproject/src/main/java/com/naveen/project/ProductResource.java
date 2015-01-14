package com.naveen.project;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.naveen.beans.Product;
import com.naveen.dao.ProductInfo;

/*
 * REST API for Iventory management system
 */
@Path("product")
public class ProductResource {
	
	private ProductInfo productInfo = new ProductInfo();
	
	//Below logic is to return a constructed Product Details in JSON format. This is just to test the product contents in the JSON format
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Product getProductInfo(){
		
		Product p1 = new Product();
		
		p1.setProductId("111");
		p1.setDescription("Watch");
		p1.setPrice(10.15);
		p1.setCategory("Electronics");
		HashMap<String,Integer> storeDetails = new HashMap<String,Integer>();
		HashMap<String,Integer> vendorDetails = new HashMap<String,Integer>();
		HashMap<String,Integer> fcDetails = new HashMap<String,Integer>();
		HashMap<String, HashMap<String,Integer>> mp = new HashMap<String, HashMap<String,Integer>>();
		
		
		storeDetails.put("1000",10);
		storeDetails.put("1001",20);
		vendorDetails.put("2000",1);
		vendorDetails.put("2001",2);
		fcDetails.put("3000", 100);
		fcDetails.put("3001", 101);
		
		mp.put("Store", storeDetails);
		mp.put("Vendor",vendorDetails);
		mp.put("FullfillmentCenter",fcDetails);
		
		p1.setAvailability(mp);
		
		return p1;
		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/query")
	public Response getProductInfo(@QueryParam("productId") String productId,
			                       @QueryParam("unitRequested") int unitRequested){
		
		if(productId==null || unitRequested==0){
			return Response.status(Status.BAD_REQUEST).build();
		}
		Product product = productInfo.getProductAvailability(productId,unitRequested);
		
		return Response.ok().entity(product).build();
		
	}
	
	/*
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/query")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response bookProduct(MultivaluedMap<String,String> fromURL) {
		
		String str = productInfo.bookProduct(fromURL.getFirst("Product"),fromURL.getFirst("Quantity"));
		
		return Response.ok().entity(str).build();
		
	}
	*/
	
	
	

}
