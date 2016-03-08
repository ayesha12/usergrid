package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Direction;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by ApigeeCorporation on 9/10/15.
 */
public class ClientInItTestSuite {

    @Before
    public void before() {
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
        Usergrid.authorizeAppClient(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);

    }

    @Test
    public void clientInit() throws JSONException {
        //should fail to initialize without an orgId and appId
        UsergridClient client = new UsergridClient(null,null);
        try{
            UsergridResponse response = client.authenticateApp(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
            assertTrue("no error thrown", response.getError() == null);

        }
        catch (IllegalArgumentException e){
        }

        //should initialize using properties defined in config.json
        client = new UsergridClient(SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
        client.setBaseUrl(SDKTestConfiguration.USERGRID_URL);
        try{
            UsergridResponse response = client.authenticateApp(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
            assertTrue("no error thrown", response.getError() == null);
        }
        catch (IllegalArgumentException e){
        }

    }

}
