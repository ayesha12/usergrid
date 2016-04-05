package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.UsergridEnums;
import org.apache.usergrid.java.client.UsergridRequest;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.model.UsergridUser;
import org.apache.usergrid.java.client.model.UsergridUserAuth;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by ApigeeCorporation on 9/14/15.
 */
public class UsergridClientAuthTestSuite {


    @Test
    public void clientAppInit() {
        Usergrid.initSharedInstance(SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.authFallBack);
        Usergrid.authenticateApp(new UsergridAppAuth(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET));

        //should fall back to using no authentication when currentUser is not authenticated and authFallback is set to NONE
        UsergridClient client = Usergrid.getInstance();
//        client.config.authMode = UsergridEnums.UsergridAuthMode.NONE;

        String[] segments = {client.getOrgId(), client.getAppId(), "users"};
        UsergridRequest request = new UsergridRequest(UsergridEnums.UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                client.getBaseUrl(), null, null, null, null, segments);
        UsergridResponse response = client.sendRequest(request);
    }

    @Test
    public void clientUserInit() {
        Usergrid.initSharedInstance(SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, SDKTestConfiguration.USERGRID_URL);
        UsergridResponse response = Usergrid.authenticateUser(new UsergridUserAuth(SDKTestConfiguration.APP_UserName, SDKTestConfiguration.APP_Password));
        UsergridResponse getResponse = Usergrid.GET("user","eb8145ea-e171-11e5-a5e5-2bc0953f9fe6");
        if( getResponse.getEntities() != null ) {
            UsergridEntity entity = getResponse.first();
            if( entity instanceof UsergridUser) {
                UsergridUser user = (UsergridUser) entity;
                System.out.print(user.toString());
            }
            List<UsergridUser> users = getResponse.users();
            if( users != null ) {
                System.out.print(users.get(0).toString());
            }
        }

    }


}
