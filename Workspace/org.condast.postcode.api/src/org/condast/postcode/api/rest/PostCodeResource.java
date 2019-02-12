package org.condast.postcode.api.rest;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.condast.commons.Utils;
import org.condast.commons.na.community.CommunityResource;
import org.condast.commons.na.location.CommunityQuery;
import org.condast.commons.strings.StringUtils;

// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the base URL + /community
@Path("/")
public class PostCodeResource{

	public static final String S_ERR_UNKNOWN_REQUEST = "An invalid request was rertrieved: ";
	public static final String S_ERR_INVALID_VESSEL = "An request was received from an unknown vessel:";
	
	/**
	 * Checks to see if the given postcode and house number is located in the string of
	 * places. Reurns Response.OK if it went well, and Response.noContent if not.
	 * @param postcode
	 * @param number
	 * @param places
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/places")
	public Response matchPlaces( @QueryParam("postcode") String postcode, @QueryParam("number") String number, @QueryParam("places") String places )
	{
		Response retval = Response.noContent().build();
		if( StringUtils.isEmpty(postcode))
			return retval;
		
		Collection<CommunityResource> results;
		try {
			results = CommunityQuery.getPostcodes(places);
			if( Utils.assertNull(results))
				return retval;
			String str = postcode.replace("\\s+", "").toUpperCase();
			for( CommunityResource cr: results ) {
				if( Arrays.asList(cr.getRange()).contains(str)) {
					return Response.ok().build();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			retval = Response.serverError().build();
		}
		return retval;
	}
}