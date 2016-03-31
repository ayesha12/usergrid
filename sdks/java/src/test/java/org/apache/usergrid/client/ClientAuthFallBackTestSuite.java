package org.apache.usergrid.client;

import org.apache.usergrid.java.client.*;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by ApigeeCorporation on 9/10/15.
 */
public class ClientAuthFallBackTestSuite {
    public static UsergridClient client = null;

    @Before
    public void before() {
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, SDKTestConfiguration.authFallBack);
        UsergridAppAuth appAuth = new UsergridAppAuth(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
        Usergrid.authenticateApp(appAuth);
        client = Usergrid.getInstance();
        String[] segments = {client.config.orgId, client.config.appId,"roles","guest","permissions"};
        Map<String, Object> params = new HashMap<>();
        params.put("permission","get,post,put,delete:/**");
        UsergridRequest request = new UsergridRequest(UsergridEnums.UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                client.config.baseUrl, params, null, segments);
        UsergridResponse resp = new UsergridRequestManager(client).performRequest(request);
    }

    @After
    public void after() {
        UsergridClient client1 = client.usingAuth(client.config.appAuth);
        client1.config.authMode = UsergridEnums.UsergridAuthMode.NONE;
        String[] segments = {client1.config.orgId, client1.config.appId,"roles","guest","permissions"};
        Map<String, Object> params = new HashMap<>();
        params.put("permission","get,post,put,delete:/**");
        UsergridRequest request = new UsergridRequest(UsergridEnums.UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                client1.config.baseUrl, params, null, segments);
        //TODO : throws error.
        UsergridResponse resp = new UsergridRequestManager(client1).performRequest(request);
//        System.out.println(resp);

        Usergrid.reset();
    }

    @Test
    public void authFallBackNONETest() throws JSONException {
        //should fall back to using no authentication when currentUser is not authenticated and authFallback is set to NONE
        client.config.authMode = UsergridEnums.UsergridAuthMode.NONE;
        UsergridQuery q = new UsergridQuery("users").desc("created");
        UsergridResponse resp = client.GET(q);
        assertTrue("The returned resonse should have error", resp != null);
        assertTrue("The returned resonse should have error", resp.responseError != null);

    }

    @Test
    public void authFallBackAPPTest() throws JSONException {
        //should fall back to using no authentication when currentUser is not authenticated and authFallback is set to NONE
        client.config.authMode = UsergridEnums.UsergridAuthMode.APP;
        UsergridQuery q = new UsergridQuery("users").desc("created");
        UsergridResponse resp = client.GET(q);
        assertTrue("The returned resonse should not have error", resp != null);
        assertTrue("The returned resonse should not have error", resp.responseError == null);

    }


}
