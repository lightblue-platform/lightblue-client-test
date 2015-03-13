package com.redhat.lightblue.test.example;

import org.junit.BeforeClass;
import org.junit.Test;

import com.redhat.lightblue.test.utils.AbstractCRUDControllerWithRest;

/**
 *
 * @author mpatercz
 *
 */
public class CountryDAOTest extends AbstractCRUDControllerWithRest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        initLightblueFactoryWithRest("./country.json");
    }


	@Test
	public void test() {

	}

}
