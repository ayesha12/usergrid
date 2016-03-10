package org.apache.usergrid.client;

import org.apache.commons.lang.NullArgumentException;
import org.apache.usergrid.java.client.Direction;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import javax.jws.soap.SOAPBinding;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by ApigeeCorporation on 9/10/15.
 */
public class ClientInItTestSuite {

//    @Before
//    public void before() {
//        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
//        Usergrid.authorizeAppClient(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
//
//    }

    @Test
    public void clientInit() throws JSONException {
        //should fail to initialize without an orgId and appId
        UsergridClient client1 = new UsergridClient(null,null);

        try{
            UsergridResponse response = client1.authenticateApp(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
            assertTrue("no error thrown", response.responseError.getError() == null);

        }
        catch (NullPointerException e){
        }

        //should initialize using properties defined in config.json
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL,SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
        UsergridClient client2 =Usergrid.getInstance();
        client2.config.authFallBack = SDKTestConfiguration.authFallBack;
        try{
            UsergridResponse response = client2.authenticateApp(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
            assertTrue("no error thrown", response.responseError == null);
        }
        catch (IllegalArgumentException e){
        }

    }

}
