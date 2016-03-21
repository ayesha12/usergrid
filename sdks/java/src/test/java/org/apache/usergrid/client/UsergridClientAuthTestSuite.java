package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.UsergridEnums;
import org.apache.usergrid.java.client.UsergridRequest;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

/**
 * Created by ApigeeCorporation on 9/14/15.
 */
public class UsergridClientAuthTestSuite {


    @Test
    public void clientAppInit() {
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, SDKTestConfiguration.authFallBack);
        Usergrid.authorizeAppClient(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);

        //should fall back to using no authentication when currentUser is not authenticated and authFallback is set to NONE
        UsergridClient client = Usergrid.getInstance();
//        client.config.authMode = UsergridEnums.UsergridAuthMode.NONE;

        String[] segments = {client.config.orgId,client.config.appId,"users"};
        UsergridRequest request = new UsergridRequest(UsergridEnums.UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                client.config.baseUrl,null,null,null,null,segments);
        UsergridResponse response = client.requestManager.performRequest(request);
    }

    @Test
    public void clientUserInit() {
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
        Usergrid.authorizeAppUser(SDKTestConfiguration.APP_UserName, SDKTestConfiguration.APP_Password);

    }


}
