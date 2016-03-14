package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Usergrid;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by ApigeeCorporation on 9/14/15.
 */
public class UsergridClientAuthTestSuite {


    @Test
    public void clientUserInit() {
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
        Usergrid.authorizeAppUser(SDKTestConfiguration.APP_UserName,SDKTestConfiguration.APP_Password);

    }



}
