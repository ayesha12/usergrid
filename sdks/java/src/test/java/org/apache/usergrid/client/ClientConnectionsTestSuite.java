package org.apache.usergrid.client;

import org.apache.usergrid.java.client.UsergridEnums.UsergridDirection;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by ApigeeCorporation on 9/10/15.
 */
public class ClientConnectionsTestSuite {

    @Before
    public void before() {
        Usergrid.initSharedInstance(SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.authFallBack);
        UsergridAppAuth appAuth = new UsergridAppAuth(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
        Usergrid.authenticateApp(appAuth);
    }

    @After
    public void after() {
        Usergrid.reset();
    }

    @Test
    public void clientConnect() throws JSONException {
        UsergridClient client = Usergrid.getInstance();

        String collectionName = "testClientConnection" + System.currentTimeMillis();

        Map<String, Object> fields = new HashMap<>(3);
        fields.put("name", "john");
        fields.put("place", "San Jose");

        //should set properties for a given object, overwriting properties that exist and creating those that don\'t
        UsergridEntity entityone = new UsergridEntity(collectionName);
        entityone.putProperties(fields);
        client.POST(entityone);
        entityone =  client.GET(collectionName, "john").first();

        fields = new HashMap<>(3);
        fields.put("name", "amici");
        fields.put("place", "San Jose");

        //should set properties for a given object, overwriting properties that exist and creating those that don\'t
        UsergridEntity entitytwo = new UsergridEntity(collectionName);
        entitytwo.putProperties(fields);
        client.POST(entitytwo);
        UsergridResponse response1 = client.GET(collectionName, "amici");
        entitytwo =  response1.first();

        //should connect entities by passing UsergridEntity objects as parameters
        client.connect(entityone, "likes", entitytwo);

        UsergridEntity response = client.getConnections(UsergridDirection.OUT, entityone, "likes").first();

        assertTrue("both entities name should be same", entitytwo.getName().equals(response.getName()));
        assertTrue("both entities uuid should be same", entitytwo.getUuid().equals(response.getUuid()));

        //should connect entities by passing a source UsergridEntity object and a target uuid.
        client.connect(entityone.getType(), entityone.getUuid(), "visited", entitytwo.getUuid().toString());

        response = client.getConnections(UsergridDirection.OUT, entityone, "visited").first();

        assertTrue("both entities name should be same", entitytwo.getName().equals(response.getName()));
        assertTrue("both entities uuid should be same", entitytwo.getUuid().equals(response.getUuid()));


        //should connect entities by passing source type, source uuid, and target uuid as parameters
        client.connect(entitytwo.getType(), entitytwo.getUuid(), "visiter", entityone.getUuid().toString());

        response = client.getConnections(UsergridDirection.OUT, entitytwo, "visiter").first();

        assertTrue("both entities name should be same", entityone.getName().equals(response.getName()));
        assertTrue("both entities uuid should be same", entityone.getUuid().equals(response.getUuid()));


        //should connect entities by passing source type, source name, target type, and target name as parameters
        client.connect(entitytwo.getType(), entitytwo.getName(), "welcomed", entityone.getType(), entityone.getName());

        response = client.getConnections(UsergridDirection.OUT, entitytwo, "welcomed").first();

        assertTrue("both entities name should be same", entityone.getName().equals(response.getName()));
        assertTrue("both entities uuid should be same", entityone.getUuid().equals(response.getUuid()));

        //should connect entities by passing source type, source name, target type, and target name as parameters
        client.connect(entitytwo.getType(), entitytwo.getName(), "invalidLink", "invalidName");
        response = client.getConnections(UsergridDirection.OUT, entitytwo, "invalidLink").first();
        assertTrue("both entities name should be same", response == null);

    }

    @Test
    public void clientGetConnect() throws JSONException {
        UsergridClient client = Usergrid.getInstance();

        String collectionName = "testClientGetConnection" + System.currentTimeMillis();

        Map<String, Object> fields = new HashMap<>(3);
        fields.put("name", "john");
        fields.put("place", "San Jose");

        //should set properties for a given object, overwriting properties that exist and creating those that don\'t
        UsergridEntity entityone = new UsergridEntity(collectionName);
        entityone.putProperties(fields);
        client.POST(entityone);
        entityone = client.GET(collectionName, "john").first();

        fields = new HashMap<>(3);
        fields.put("name", "amici");
        fields.put("place", "San Jose");

        //should set properties for a given object, overwriting properties that exist and creating those that don\'t
        UsergridEntity entitytwo = new UsergridEntity(collectionName);
        entitytwo.putProperties(fields);
        client.POST(entitytwo);

        UsergridResponse response1 = client.GET(collectionName, "amici");
        entitytwo =  response1.first();
        //should connect entities by passing UsergridEntity objects as parameters
        client.connect(entityone, "likes", entitytwo);
        client.connect(entityone, "visited", entitytwo);

        UsergridEntity response = client.getConnections(UsergridDirection.OUT, entityone, "likes").first();

        assertTrue("both entities name should be same", entitytwo.getName().equals(response.getName()));
        assertTrue("both entities uuid should be same", entitytwo.getUuid().equals(response.getUuid()));


        response = client.getConnections(UsergridDirection.IN, entitytwo, "visited").first();

        assertTrue("both entities name should be same", entityone.getName().equals(response.getName()));
        assertTrue("both entities uuid should be same", entityone.getUuid().equals(response.getUuid()));

    }

    @Test
    public void clientDisConnect() throws JSONException {
        UsergridClient client = Usergrid.getInstance();

        String collectionName = "testClientGetConnection" + System.currentTimeMillis();

        Map<String, Object> fields = new HashMap<>(3);
        fields.put("name", "john");
        fields.put("place", "San Jose");

        //should set properties for a given object, overwriting properties that exist and creating those that don\'t
        UsergridEntity entityone = new UsergridEntity(collectionName);
        entityone.putProperties(fields);
        client.POST(entityone);
        entityone = client.GET(collectionName, "john").first();

        fields = new HashMap<>(3);
        fields.put("name", "amici");
        fields.put("place", "San Jose");

        //should set properties for a given object, overwriting properties that exist and creating those that don\'t
        UsergridEntity entitytwo = new UsergridEntity(collectionName);
        entitytwo.putProperties(fields);
        client.POST(entitytwo);
        entitytwo =  client.GET(collectionName, "amici").first();

        //should connect entities by passing UsergridEntity objects as parameters
        client.connect(entityone, "likes", entitytwo);
        client.connect(entityone, "visited", entitytwo);
        client.connect(entityone, "twice", entitytwo);
        client.connect(entityone, "thrice", entitytwo);

        //should disConnect entities by passing UsergridEntity objects as parameters
        client.disconnect(entityone, "likes", entitytwo);
        UsergridEntity response = client.getConnections(UsergridDirection.IN, entitytwo, "likes").first();
        assertTrue("response should be null", response == null);

        //should disConnect entities by passing source type, source uuid, and target uuid as parameters
        client.disconnect(entityone.getType(), entityone.getUuid(), "visited", entitytwo.getUuid());
        response = client.getConnections(UsergridDirection.OUT, entityone, "visited").first();
        assertTrue("response should be null", response == null);

        //should disConnect entities by passing source type, source name, target type, and target name as parameters
        client.disconnect(entityone.getType(), entityone.getName(), "twice", entitytwo.getType(), entitytwo.getName());
        response = client.getConnections(UsergridDirection.OUT, entityone, "twice").first();
        assertTrue("response should be null", response == null);

        //should fail to disConnect entities when specifying target name without type
        client.disconnect(entitytwo.getType(), entitytwo.getName(), "thrice", entityone.getName());
        response = client.getConnections(UsergridDirection.OUT, entitytwo, "thrice").first();
        assertTrue("both entities name should be same", response == null);

    }

}
