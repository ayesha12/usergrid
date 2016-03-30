package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.UsergridEnums;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridUserAuth;
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

        UsergridClient client2 = Usergrid.getInstance();
        client2.config.authMode = SDKTestConfiguration.authFallBack;
        try {
            UsergridResponse response = client2.authenticateApp(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
            assertTrue("no error thrown", response.responseError == null);
            assertTrue("response status is OK", response.ok == true);
            assertTrue("should have a valid token",response.getAccessToken().length() > 10);
            assertTrue("client.appAuth.token should be set to the token returned from Usergrid",
                    client2.config.appAuth.accessToken.equals(response.getAccessToken()));
            assertTrue("client.appAuth.isValid should be true",client2.config.appAuth.isValidToken() == true);
            assertTrue("client.appAuth.expiry should be set to a future date",
                    client2.config.appAuth.token_expiry > System.currentTimeMillis());
        } catch (IllegalArgumentException e) {
            assertTrue(" error thrown", e != null);
        }

        //should authenticate by passing clientId and clientSecret in an object
        client2 = Usergrid.getInstance();
        client2.config.authMode = SDKTestConfiguration.authFallBack;
        try {
            UsergridAppAuth auth = new UsergridAppAuth(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
            UsergridResponse response = client2.authenticateApp(auth);
            assertTrue("no error thrown", response.responseError == null);
            assertTrue("response status is OK", response.ok == true);
            assertTrue("should have a valid token",response.getAccessToken().length() > 10);
            assertTrue("client.appAuth.token should be set to the token returned from Usergrid",
                    client2.config.appAuth.accessToken.equals(response.getAccessToken()));
            assertTrue("client.appAuth.isValid should be true",client2.config.appAuth.isValidToken() == true);
            assertTrue("client.appAuth.expiry should be set to a future date",
                    client2.config.appAuth.token_expiry > System.currentTimeMillis());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            assertTrue(" error thrown", e == null);
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

        UsergridClient client2 = Usergrid.getInstance();
        client2.config.authMode = UsergridEnums.UsergridAuthMode.USER;
        try {
            UsergridResponse response = client2.authenticateUser(SDKTestConfiguration.APP_UserName, SDKTestConfiguration.APP_Password);
            assertTrue("no error thrown", response.responseError == null);
            assertTrue("response status is OK", response.ok == true);
            assertTrue("should have a valid token",response.getAccessToken().length() > 10);
            assertTrue("client.appAuth.token should be set to the token returned from Usergrid",
                    client2.config.userAuth.accessToken.equals(response.getAccessToken()));
            assertTrue("client.appAuth.isValid should be true",client2.config.userAuth.isValidToken() == true);
            assertTrue("client.appAuth.expiry should be set to a future date",
                    client2.config.userAuth.token_expiry > System.currentTimeMillis());
        } catch (IllegalArgumentException e) {
            assertTrue(" error thrown", e != null);
        }

        client2 = Usergrid.getInstance();
        client2.config.authMode = UsergridEnums.UsergridAuthMode.USER;
        try {
            UsergridUserAuth auth = new UsergridUserAuth(SDKTestConfiguration.APP_UserName, SDKTestConfiguration.APP_Password);
            UsergridResponse response = client2.authenticateUser(auth);
            assertTrue("no error thrown", response.responseError == null);
            assertTrue("response status is OK", response.ok == true);
            assertTrue("should have a valid token",response.getAccessToken().length() > 10);
            assertTrue("client.appAuth.token should be set to the token returned from Usergrid",
                    client2.config.userAuth.accessToken.equals(response.getAccessToken()));
            assertTrue("client.appAuth.isValid should be true",client2.config.userAuth.isValidToken() == true);
            assertTrue("client.appAuth.expiry should be set to a future date",
                    client2.config.userAuth.token_expiry > System.currentTimeMillis());
        } catch (IllegalArgumentException e) {
            assertTrue(" error thrown", e != null);
        }

    }

}
