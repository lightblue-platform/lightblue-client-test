package com.redhat.lightblue.test.utils;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;

import com.mongodb.BasicDBObject;
import com.redhat.lightblue.client.LightblueClientConfiguration;
import com.redhat.lightblue.client.http.LightblueHttpClient;
import com.redhat.lightblue.mongo.test.MongoServerExternalResource;
import com.redhat.lightblue.mongo.test.MongoServerExternalResource.InMemoryMongoServer;
import com.redhat.lightblue.rest.RestConfiguration;
import com.redhat.lightblue.rest.crud.CrudResource;
import com.redhat.lightblue.rest.metadata.MetadataResource;
import com.redhat.lightblue.test.AbstractCRUDTestController;
import com.sun.net.httpserver.HttpServer;

/**
 * Utility class for adding rest layer on top of mongo CRUD Controller. Extend this class when writing tests.
 *
 * @author mpatercz
 *
 */
@InMemoryMongoServer
public abstract class AbstractCRUDControllerWithRest extends AbstractCRUDTestController {

    @ClassRule
    public static MongoServerExternalResource mongoServer = new MongoServerExternalResource();

    private final static int DEFAULT_PORT = 8000;

    protected static HttpServer httpServer;

    private final int httpPort;

    @BeforeClass
    public static void setup() {
        System.setProperty("mongo.host", "localhost");
        System.setProperty("mongo.port", String.valueOf(MongoServerExternalResource.DEFAULT_PORT));
        System.setProperty("mongo.database", "lightblue");
    }

    @AfterClass
    public static void stopHttpServer() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    public AbstractCRUDControllerWithRest() throws Exception {
        this(true, DEFAULT_PORT);
    }

    /**
     * Setup lightblue backend with provided schemas and rest endpoints.
     *
     * @param httpServerPort
     *            port used for http (rest endpoints)
     * @throws Exception
     */
    public AbstractCRUDControllerWithRest(boolean loadStatically, int httpServerPort) throws Exception {
        super(loadStatically);
        httpPort = httpServerPort;

        if (!loadStatically || httpServer == null) {
            RestConfiguration.setFactory(lightblueFactory);

            httpServer = HttpServer.create(new InetSocketAddress(httpPort), 0);

            HttpContextBuilder dataContext = new HttpContextBuilder();
            dataContext.getDeployment().getActualResourceClasses().add(CrudResource.class);
            dataContext.setPath("/rest/data");
            dataContext.bind(httpServer);

            HttpContextBuilder metadataContext = new HttpContextBuilder();
            metadataContext.getDeployment().getActualResourceClasses().add(MetadataResource.class);
            metadataContext.setPath("/rest/metadata");
            metadataContext.bind(httpServer);

            httpServer.start();
        }
    }

    /**
     * Remove all documents from specified collections. Useful for cleaning up between tests.
     *
     * @param collectionName
     * @throws UnknownHostException
     */
    public static void cleanupMongoCollections(String... collectionNames) throws UnknownHostException {
        for (String collectionName : collectionNames) {
            mongoServer.getConnection().getDB(System.getProperty("mongo.database")).getCollection(collectionName).remove(new BasicDBObject());
        }
    }

    /**
     *
     * @return lightblue http client configuration needed to connect
     */
    protected LightblueClientConfiguration getLightblueClientConfiguration() {
        LightblueClientConfiguration lbConf = new LightblueClientConfiguration();
        lbConf.setUseCertAuth(false);
        lbConf.setDataServiceURI("http://localhost:" + httpPort + "/rest/data");
        lbConf.setMetadataServiceURI("http://localhost:" + httpPort + "/rest/metadata");
        return lbConf;
    }

    protected LightblueHttpClient getLightblueClient() {
        return new LightblueHttpClient(getLightblueClientConfiguration());
    }

}
