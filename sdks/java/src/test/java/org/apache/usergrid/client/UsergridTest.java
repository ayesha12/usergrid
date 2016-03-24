package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Usergrid;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by ApigeeCorporation on 9/14/15.
 */
public class UsergridTest {

    @Before
    public void before() {
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
        Usergrid.authorizeAppClient(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
    }

    @Test
    public void initialize() {
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
    }
}
