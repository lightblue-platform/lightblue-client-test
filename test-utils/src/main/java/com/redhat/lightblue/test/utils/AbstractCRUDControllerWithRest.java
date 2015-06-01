package com.redhat.lightblue.test.utils;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.junit.AfterClass;

import com.redhat.lightblue.client.LightblueClientConfiguration;
import com.redhat.lightblue.client.LightblueClientConfiguration.Compression;
import com.redhat.lightblue.client.http.LightblueHttpClient;
import com.redhat.lightblue.client.response.LightblueResponse;
import com.redhat.lightblue.client.test.request.DataInsertRequestStub;
import com.redhat.lightblue.mongo.test.AbstractMongoCRUDTestController;
import com.redhat.lightblue.rest.RestConfiguration;
import com.redhat.lightblue.rest.crud.CrudResource;
import com.redhat.lightblue.rest.metadata.MetadataResource;
import com.restcompress.provider.LZFDecodingInterceptor;
import com.restcompress.provider.LZFEncodingInterceptor;
import com.sun.net.httpserver.HttpServer;

/**
 * Utility class for adding rest layer on top of mongo CRUD Controller. Extend this class when writing tests.
 *
 * @author mpatercz
 *
 */
public abstract class AbstractCRUDControllerWithRest extends AbstractMongoCRUDTestController {

    private final static int DEFAULT_PORT = 8000;

    private static HttpServer httpServer;

    private final int httpPort;
    private final String dataUrl;
    private final String metadataUrl;

    @AfterClass
    public static void stopHttpServer() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
        httpServer = null;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public String getMetadataUrl() {
        return metadataUrl;
    }

    public AbstractCRUDControllerWithRest() throws Exception {
        this(DEFAULT_PORT);
    }

    /**
     * Setup lightblue backend with provided schemas and rest endpoints.
     *
     * @param httpServerPort
     *            port used for http (rest endpoints)
     * @throws Exception
     */
    public AbstractCRUDControllerWithRest(int httpServerPort) throws Exception {
        super();
        httpPort = httpServerPort;
        dataUrl = "http://localhost:" + httpPort + "/rest/data";
        metadataUrl = "http://localhost:" + httpPort + "/rest/metadata";
        System.setProperty("client.data.url", getDataUrl());
        System.setProperty("client.metadata.url", getMetadataUrl());

        if (httpServer == null) {
            RestConfiguration.setFactory(getLightblueFactory());

            httpServer = HttpServer.create(new InetSocketAddress(httpPort), 0);

            HttpContextBuilder dataContext = new HttpContextBuilder();
            dataContext.getDeployment().getActualResourceClasses().add(CrudResource.class);
            dataContext.getDeployment().getActualProviderClasses().add(LZFEncodingInterceptor.class);
            dataContext.getDeployment().getActualProviderClasses().add(LZFDecodingInterceptor.class);
            dataContext.setPath("/rest/data");
            dataContext.bind(httpServer);

            HttpContextBuilder metadataContext = new HttpContextBuilder();
            metadataContext.getDeployment().getActualResourceClasses().add(MetadataResource.class);
            metadataContext.getDeployment().getActualProviderClasses().add(LZFEncodingInterceptor.class);
            metadataContext.getDeployment().getActualProviderClasses().add(LZFDecodingInterceptor.class);
            metadataContext.setPath("/rest/metadata");
            metadataContext.bind(httpServer);

            httpServer.start();
        }
    }

    /**
     *
     * @return lightblue http client configuration needed to connect
     */
    protected LightblueClientConfiguration getLightblueClientConfiguration() {
        LightblueClientConfiguration lbConf = new LightblueClientConfiguration();
        lbConf.setUseCertAuth(false);
        lbConf.setDataServiceURI(getDataUrl());
        lbConf.setMetadataServiceURI(getMetadataUrl());
        return lbConf;
    }

    public LightblueHttpClient getLightblueClient() {
        return new LightblueHttpClient(getLightblueClientConfiguration());
    }

    public LightblueResponse loadData(String entityName, String entityVersion, String resourcePath) throws IOException {
        DataInsertRequestStub request = new DataInsertRequestStub(
                entityName, entityVersion, loadResource(resourcePath, false));
        LightblueResponse response = getLightblueClient().data(request);
        assertFalse(response.getText(), response.hasError());

        return response;
    }
}
