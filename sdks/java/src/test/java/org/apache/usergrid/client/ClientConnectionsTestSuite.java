package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Direction;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by ApigeeCorporation on 9/10/15.
 */
public class ClientConnectionsTestSuite {

    @Before
    public void before() {
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, SDKTestConfiguration.authFallBack);
        Usergrid.authorizeAppClient(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
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

        fields = new HashMap<>(3);
        fields.put("name", "amici");
        fields.put("place", "San Jose");

        //should set properties for a given object, overwriting properties that exist and creating those that don\'t
        UsergridEntity entitytwo = new UsergridEntity(collectionName);
        entitytwo.putProperties(fields);
        client.POST(entitytwo);

        //should connect entities by passing UsergridEntity objects as parameters
        client.connect(entityone, "likes", entitytwo);

        UsergridEntity response = client.getConnections(Direction.OUT, entityone, "likes").first();

        assertTrue("both entities name should be same", entitytwo.getName().equals(response.getName()));
        assertTrue("both entities uuid should be same", entitytwo.getUuidString().equals(response.getUuidString()));

        //should connect entities by passing a source UsergridEntity object and a target uuid.
        client.connect(entityone, "visited", entitytwo.getUuid().toString());

        response = client.getConnections(Direction.OUT, entityone, "visited").first();

        assertTrue("both entities name should be same", entitytwo.getName().equals(response.getName()));
        assertTrue("both entities uuid should be same", entitytwo.getUuidString().equals(response.getUuidString()));


        //should connect entities by passing source type, source uuid, and target uuid as parameters
        client.connect(entitytwo.getType(), entitytwo.getUuidString(), "visiter", entityone.getUuid().toString());

        response = client.getConnections(Direction.OUT, entitytwo, "visiter").first();

        assertTrue("both entities name should be same", entityone.getName().equals(response.getName()));
        assertTrue("both entities uuid should be same", entityone.getUuidString().equals(response.getUuidString()));


        //should connect entities by passing source type, source name, target type, and target name as parameters
        client.connect(entitytwo.getType(), entitytwo.getName(), "welcomed", entityone.getType(), entityone.getName());

        response = client.getConnections(Direction.OUT, entitytwo, "welcomed").first();

        assertTrue("both entities name should be same", entityone.getName().equals(response.getName()));
        assertTrue("both entities uuid should be same", entityone.getUuidString().equals(response.getUuidString()));

        //should connect entities by passing source type, source name, target type, and target name as parameters
        client.connect(entitytwo.getType(), entitytwo.getName(), "invalidLink", "invalidName");
        response = client.getConnections(Direction.OUT, entitytwo, "invalidLink").first();
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

        fields = new HashMap<>(3);
        fields.put("name", "amici");
        fields.put("place", "San Jose");

        //should set properties for a given object, overwriting properties that exist and creating those that don\'t
        UsergridEntity entitytwo = new UsergridEntity(collectionName);
        entitytwo.putProperties(fields);
        client.POST(entitytwo);

        //should connect entities by passing UsergridEntity objects as parameters
        client.connect(entityone, "likes", entitytwo);
        client.connect(entityone, "visited", entitytwo.getUuid().toString());

        UsergridEntity response = client.getConnections(Direction.OUT, entityone, "likes").first();

        assertTrue("both entities name should be same", entitytwo.getName().equals(response.getName()));
        assertTrue("both entities uuid should be same", entitytwo.getUuidString().equals(response.getUuidString()));


        response = client.getConnections(Direction.IN, entitytwo, "visited").first();

        assertTrue("both entities name should be same", entityone.getName().equals(response.getName()));
        assertTrue("both entities uuid should be same", entityone.getUuidString().equals(response.getUuidString()));

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

        fields = new HashMap<>(3);
        fields.put("name", "amici");
        fields.put("place", "San Jose");

        //should set properties for a given object, overwriting properties that exist and creating those that don\'t
        UsergridEntity entitytwo = new UsergridEntity(collectionName);
        entitytwo.putProperties(fields);
        client.POST(entitytwo);

        //should connect entities by passing UsergridEntity objects as parameters
        client.connect(entityone, "likes", entitytwo);
        client.connect(entityone, "visited", entitytwo.getUuid().toString());
        client.connect(entityone, "twice", entitytwo);
        client.connect(entityone, "thrice", entitytwo);

        //should disConnect entities by passing UsergridEntity objects as parameters
        client.disConnect(entityone, "likes", entitytwo);
        UsergridEntity response = client.getConnections(Direction.IN, entitytwo, "likes").first();
        assertTrue("response should be null", response == null);

        //should disConnect entities by passing source type, source uuid, and target uuid as parameters
        client.disConnect(entityone.getType(), entityone.getUuidString(), "visited", entitytwo.getUuidString());
        response = client.getConnections(Direction.OUT, entityone, "visited").first();
        assertTrue("response should be null", response == null);

        //should disConnect entities by passing source type, source name, target type, and target name as parameters
        client.disConnect(entityone.getType(), entityone.getName(), "twice", entitytwo.getType(), entitytwo.getName());
        response = client.getConnections(Direction.OUT, entityone, "twice").first();
        assertTrue("response should be null", response == null);

        //should fail to disConnect entities when specifying target name without type
        client.disConnect(entitytwo.getType(), entitytwo.getName(), "thrice", entityone.getName());
        response = client.getConnections(Direction.OUT, entitytwo, "thrice").first();
        assertTrue("both entities name should be same", response == null);

    }

}
