package com.redhat.lightblue.test.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.junit.AfterClass;
import org.junit.ClassRule;

import com.redhat.lightblue.config.DataSourcesConfiguration;
import com.redhat.lightblue.config.JsonTranslator;
import com.redhat.lightblue.config.LightblueFactory;
import com.redhat.lightblue.metadata.EntityMetadata;
import com.redhat.lightblue.metadata.Metadata;
import com.redhat.lightblue.mongo.test.MongoServerExternalResource;
import com.redhat.lightblue.mongo.test.MongoServerExternalResource.InMemoryMongoServer;
import com.redhat.lightblue.util.JsonUtils;

// copied from https://github.com/lightblue-platform/lightblue-ldap/blob/master/lightblue-ldap-integration-test/src/test/java/com/redhat/lightblue/crud/ldap/AbstractCRUDController.java
@InMemoryMongoServer
public abstract class AbstractCRUDController {

    @ClassRule
    public static MongoServerExternalResource mongoServer = new MongoServerExternalResource();

    protected static LightblueFactory lightblueFactory;

    protected static void initLightblueFactory(String... metadataResourcePaths)
            throws Exception {

        System.setProperty("mongo.host", "localhost");
        System.setProperty("mongo.port", String.valueOf(MongoServerExternalResource.DEFAULT_PORT));
        System.setProperty("mongo.database", "lightblue");

        lightblueFactory = new LightblueFactory(
                new DataSourcesConfiguration(JsonUtils.json(loadResource("/datasources.json", true))));

        JsonTranslator tx = lightblueFactory.getJsonTranslator();

        Metadata metadata = lightblueFactory.getMetadata();
        for(String metadataResourcePath : metadataResourcePaths){
            metadata.createNewMetadata(tx.parse(EntityMetadata.class, JsonUtils.json(loadResource(metadataResourcePath, false))));
        }
    }

    @AfterClass
    public static void cleanup(){
        lightblueFactory = null;
    }

    /**
     * Load contents of resource on classpath as String.
     *
     * @param resourceName
     * @param local true if should look for resource in lightblue-test-utils.jar
     * @return the resource as a String
     * @throws IOException
     */
    public static final String loadResource(String resourceName, boolean local) throws IOException {
	System.out.println("Loading "+resourceName+" "+AbstractCRUDController.class.getResource(resourceName));

	StringBuilder buff = new StringBuilder();

        try (
			InputStream is = local ? AbstractCRUDController.class.getResourceAsStream(resourceName): AbstractCRUDController.class.getClassLoader().getResourceAsStream(resourceName);
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset());
                BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                buff.append(line).append("\n");
            }
        }

        return buff.toString();
    }


}