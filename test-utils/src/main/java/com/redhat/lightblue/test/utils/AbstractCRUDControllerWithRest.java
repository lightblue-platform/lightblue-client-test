package com.redhat.lightblue.test.utils;

import java.net.InetSocketAddress;

import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.junit.AfterClass;

import com.redhat.lightblue.rest.RestConfiguration;
import com.redhat.lightblue.rest.crud.CrudResource;
import com.sun.net.httpserver.HttpServer;

public class AbstractCRUDControllerWithRest extends AbstractCRUDController {

    protected static HttpServer httpServer = null;

    protected static void initLightblueFactoryWithRest(int httpServerPort, String... metadataResourcePaths) throws Exception {
        initLightblueFactory(metadataResourcePaths);

        RestConfiguration.setFactory(lightblueFactory);

        httpServer = HttpServer.create(new InetSocketAddress(httpServerPort), 0);
        HttpContextBuilder contextBuilder=new HttpContextBuilder();
        contextBuilder.getDeployment().getActualResourceClasses().add(CrudResource.class);
        contextBuilder.bind(httpServer);
        httpServer.start();
    }

    protected static void initLightblueFactoryWithRest(String... metadataResourcePaths) throws Exception {
        initLightblueFactoryWithRest(8000, metadataResourcePaths);
    }

    @AfterClass
    public static void stopHttpServer(){
	if (httpServer != null)
		httpServer.stop(0);
    }
}
