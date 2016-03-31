package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.UsergridEnums;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.model.UsergridUserAuth;
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
public class UsergridResponseTest {
    public UsergridClient client = null;
    public UsergridUserAuth appUser = null;

    @Before
    public void before() {
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, UsergridEnums.UsergridAuthMode.USER);
        appUser = new UsergridUserAuth(SDKTestConfiguration.APP_UserName, SDKTestConfiguration.APP_Password);


    }

    @After
    public void after() {
        Usergrid.reset();
    }

    @Test
    public void testLogoutUser() {
        Usergrid.authenticateUser(appUser);
        client = Usergrid.getInstance();
        String collectionName = "ect" + System.currentTimeMillis();
        Map<String, Map<String, String>> entityMap = new HashMap<>(7);

        Map<String, String> fields = new HashMap<>(3);
        fields.put("color", "red");
        fields.put("shape", "square");
        entityMap.put("testEntity1", fields);

        UsergridEntity e = SDKTestUtils.createEntity(collectionName, "testEntity1", fields);
        UsergridResponse response = client.GET(collectionName, "testEntity1");
        Object instanceObj = response.statuscode;
        assertTrue("The returned statusCode is and object of integer", instanceObj instanceof Integer);
        instanceObj = response.ok;
        assertTrue("The returned statusCode is and object of boolean", instanceObj instanceof Boolean);

        UsergridResponse resp = client.logoutUser("Blueprints_usergrid_0302",null);
        System.out.println(resp);

        response = client.GET(collectionName, "testEntity1");
        assertTrue("The response should throw an error",response.responseError != null);

    }

    @Test
    public void testLogoutCurrentUser() {
        Usergrid.authenticateUser(appUser);
        client = Usergrid.getInstance();

        String collectionName = "ect" + System.currentTimeMillis();

        Map<String, Map<String, String>> entityMap = new HashMap<>(7);

        Map<String, String> fields = new HashMap<>(3);
        fields.put("color", "red");
        fields.put("shape", "square");

        entityMap.put("testEntity12", fields);


        UsergridEntity e = SDKTestUtils.createEntity(collectionName, "testEntity12", fields);
        UsergridResponse response = client.GET(collectionName, "testEntity12");
        assertTrue("The response should not throw an error",response.responseError == null);

        client.logoutCurrentUser();
        response = client.GET(collectionName, "testEntity1");
        assertTrue("The response should throw an error",response.responseError != null);

    }

}
