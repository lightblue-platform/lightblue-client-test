package com.redhat.lightblue.test.utils;

import java.net.InetSocketAddress;

import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.junit.AfterClass;

import com.redhat.lightblue.client.LightblueClientConfiguration;
import com.redhat.lightblue.rest.RestConfiguration;
import com.redhat.lightblue.rest.crud.CrudResource;
import com.sun.net.httpserver.HttpServer;

public class AbstractCRUDControllerWithRest extends AbstractCRUDController {

    protected static HttpServer httpServer = null;
    
    protected static int httpPort = 8000;

    protected static void initLightblueFactoryWithRest(int httpServerPort, String... metadataResourcePaths) throws Exception {
    	httpPort = httpServerPort;
    	
        initLightblueFactory(metadataResourcePaths);

        RestConfiguration.setFactory(lightblueFactory);

        httpServer = HttpServer.create(new InetSocketAddress(httpPort), 0);
        HttpContextBuilder contextBuilder=new HttpContextBuilder();        
        contextBuilder.getDeployment().getActualResourceClasses().add(CrudResource.class);
        contextBuilder.setPath("/rest/data");
        contextBuilder.bind(httpServer);
        
        // TODO: setup another context for metadata endpoint
        
        httpServer.start();
    }

    protected static void initLightblueFactoryWithRest(String... metadataResourcePaths) throws Exception {
        initLightblueFactoryWithRest(httpPort, metadataResourcePaths);
    }

    @AfterClass
    public static void stopHttpServer(){
	if (httpServer != null)
		httpServer.stop(0);
    }
    
    protected static LightblueClientConfiguration getLightblueClientConfiguration() {
	    LightblueClientConfiguration lbConf = new LightblueClientConfiguration();
		lbConf.setUseCertAuth(false);
		lbConf.setDataServiceURI("http://localhost:"+httpPort+"/rest/data");
		lbConf.setMetadataServiceURI("http://localhost:"+httpPort+"/rest/metadata");
		return lbConf;
    }
}
