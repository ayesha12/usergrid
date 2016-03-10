package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Direction;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by ApigeeCorporation on 9/10/15.
 */
public class ClientRestTestSuite {

    String collectionName;
    @Before
    public void before() throws JSONException {
        Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
        Usergrid.authorizeAppClient(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
        CreateCollectionAndEntity();
    }

    public void CreateCollectionAndEntity() throws JSONException {
        UsergridClient client = Usergrid.getInstance();
        collectionName= "testClientConnection" + System.currentTimeMillis();
        Map<String, Object> fields = new HashMap<>(3);
        fields.put("name", "john");
        fields.put("place", "San Jose");
        UsergridEntity entityone = new UsergridEntity(collectionName);
        entityone.putProperties(fields);
        entityone.POST();
        fields = new HashMap<>(3);
        fields.put("name", "amici");
        fields.put("place", "San Jose");
        UsergridEntity entitytwo = new UsergridEntity(collectionName);
        entitytwo.putProperties(fields);
        entitytwo.POST();
        client.connect(entityone, "likes", entitytwo);
        client.connect(entityone, "visited", entitytwo.getUuid().toString());


    }

    @Test
    public void clientGET() throws JSONException {
        UsergridClient client = Usergrid.getInstance();

        //Retrieve the entity.
        UsergridResponse response = client.GET("sample","john");

        //response.ok should be true
        assertTrue("no error thrown", response.getError() == null);

        //response.entities should be an array
        assertTrue("reponse entities is an Array", response.getEntities().getClass() == ArrayList.class);

        //response.first should exist and have a valid uuid
        assertTrue("first entity is not null", response.first() != null);
        assertTrue("first entity is not null and has uuid", response.first().getUuidString() != null);

        //response.entity should exist and have a valid uuid
        assertTrue("first entity is not null", response.entity() != null);
        assertTrue("first entity is not null and has uuid", response.entity().getUuidString() != null);

        //response.last should exist and have a valid uuid
        assertTrue("last entity is not null", response.last() != null);
        assertTrue("last entity is not null and has uuid", response.last().getUuidString() != null);

    }


}
