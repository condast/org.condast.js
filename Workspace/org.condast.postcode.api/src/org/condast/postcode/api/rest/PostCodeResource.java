package org.condast.postcode.api.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


import com.google.gson.Gson;

// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the base URL + /community
@Path("/community")
public class PostCodeResource{

	public static final String S_ERR_UNKNOWN_REQUEST = "An invalid request was rertrieved: ";
	public static final String S_ERR_INVALID_VESSEL = "An request was received from an unknown vessel:";
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/community")
	public Response login( @QueryParam("place") String place )
	{
		Response retval = Response.serverError().build();
		
		Gson gson = new Gson();
		long[] results = new long[2];
		String str = gson.toJson( results );
		retval = Response.ok( str ).build();
		return retval;
	}
}