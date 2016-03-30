package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by ApigeeCorporation on 9/10/15.
 */
public class UsergridResponseErrorTest {
    public static UsergridClient client = null;

    @Before
    public void before() {
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, SDKTestConfiguration.authFallBack);
        UsergridAppAuth appAuth = new UsergridAppAuth(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
        Usergrid.authenticateApp(appAuth);
        client = Usergrid.getInstance();

    }

    @After
    public void after() {
        Usergrid.reset();
    }

    @Test
    public void testEntityCreationSuccess() {
        String collectionName = "ect" + System.currentTimeMillis();

        Map<String, Map<String, String>> entityMap = new HashMap<>(7);

        Map<String, String> fields = new HashMap<>(3);
        fields.put("color", "red");
        fields.put("shape", "square");

        entityMap.put("testEntity1", fields);
        UsergridEntity e = SDKTestUtils.createEntity(collectionName, "testEntity1", fields);
        UsergridResponse eLookUp = client.GET(collectionName, "testEntity1");
        assertTrue("The returned entity is null!", eLookUp.responseError == null); //    entity has been created

        UsergridResponse response = client.GET(collectionName, "testEntity15");
        assertTrue("The returned entity is null!", response.responseError != null); //    entity has been created

    }
}
