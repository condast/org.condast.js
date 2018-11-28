package test.condast.commons.js.rcp;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;

import test.condast.commons.js.rcp.entry.DesignEntryPoint;


public class BasicApplication implements ApplicationConfiguration {

    public void configure(Application application) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "Test Bootstrap");
        application.addEntryPoint("/hello", BasicEntryPoint.class, properties);
        application.addEntryPoint("/design", DesignEntryPoint.class, properties);
    }

}
