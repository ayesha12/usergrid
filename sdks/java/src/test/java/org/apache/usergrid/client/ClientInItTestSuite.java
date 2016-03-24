package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by ApigeeCorporation on 9/10/15.
 */
public class ClientInItTestSuite {

    @Before
    public void before() {
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, SDKTestConfiguration.authFallBack);
    }

    @After
    public void after() {
        Usergrid.reset();
    }

    @Test
    public void clientAppInit() throws JSONException {
        //should fail to initialize without an orgId and appId
        UsergridClient client1 = new UsergridClient(null, null);

        try {
            UsergridResponse response = client1.authenticateApp(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
            assertTrue("no error thrown", response.responseError.getError() == null);
        } catch (NullPointerException e) {
        }

        //should initialize using properties defined in config.json

        UsergridClient client2 = Usergrid.getInstance();
        client2.config.authMode = SDKTestConfiguration.authFallBack;
        try {
            UsergridResponse response = client2.authenticateApp(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
            assertTrue("no error thrown", response.responseError == null);
        } catch (IllegalArgumentException e) {
            assertTrue("no error thrown", e != null);
        }
    }

    @Test
    public void clientUserInit() {
        UsergridClient client1 = new UsergridClient(null, null);

        try {
            UsergridResponse response = client1.authenticateUser(SDKTestConfiguration.APP_UserName, SDKTestConfiguration.APP_Password);
            assertTrue("no error thrown", response.responseError.getError() == null);
        } catch (NullPointerException e) {
            assertTrue("no error thrown", e != null);
        }
        //TODO : need to add tests

    }

}
