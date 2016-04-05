package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridUserAuth;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by ApigeeCorporation on 9/14/15.
 */
public class UsergridInitTest {


    @Test
    public void testInitAppUsergrid() {

        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
        Usergrid.authenticateApp(new UsergridAppAuth(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET));
        assertTrue("usergrid should be an instance of usergrid client", Usergrid.getInstance().getClass() == UsergridClient.class);
    }

    @Test
    public void testInitUserUsergrid() {

        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
        Usergrid.authenticateUser(new UsergridUserAuth(SDKTestConfiguration.APP_UserName, SDKTestConfiguration.APP_Password));
        assertTrue("usergrid should be an instance of usergrid client", Usergrid.getInstance().getClass() == UsergridClient.class);
    }
}
