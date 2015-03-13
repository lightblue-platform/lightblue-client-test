package com.redhat.lightblue.test.example;

import static com.redhat.lightblue.client.projection.FieldProjection.includeField;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.client.http.LightblueHttpClient;
import com.redhat.lightblue.client.request.data.DataInsertRequest;
import com.redhat.lightblue.test.utils.AbstractCRUDControllerWithRest;

/**
 * Testing your code against lightblue example.
 *
 * @author mpatercz
 *
 */
public class CountryDAOTest extends AbstractCRUDControllerWithRest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        initLightblueFactoryWithRest("country.json");
    }


	@Test
	public void testCountry() throws IOException {
		LightblueClient client = new LightblueHttpClient(getLightblueClientConfiguration());
		
		Country c = new Country();
		c.setName("Poland");
		c.setIso2Code("PL");
		c.setIso3Code("POL");								
		
		DataInsertRequest request = new DataInsertRequest(Country.objectType, Country.objectVersion);
        
        request.create(c);
        request.returns(includeField("*"));        
        Country[] countries = client.data(request, Country[].class);
        Assert.assertEquals("PL", countries[0].getIso2Code());
	}

}
