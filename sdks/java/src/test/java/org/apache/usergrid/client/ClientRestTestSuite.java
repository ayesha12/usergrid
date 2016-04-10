/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.auth.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ClientRestTestSuite {

    String collectionName;

    @Before
    public void before()  {
        Usergrid.initSharedInstance(SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.authFallBack);
        UsergridAppAuth appAuth = new UsergridAppAuth(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
        Usergrid.authenticateApp(appAuth);
        CreateCollectionAndEntity();
    }

    @After
    public void after() {
        Usergrid.reset();
    }

    public void CreateCollectionAndEntity()  {
        UsergridClient client = Usergrid.getInstance();
        collectionName = "testClientConnection" + System.currentTimeMillis();
        Map<String, Object> fields = new HashMap<>(3);
        fields.put("place", "San Jose");
        UsergridEntity entityone = new UsergridEntity(collectionName,"john");
        entityone.putProperties(fields);
        client.POST(entityone);
        fields = new HashMap<>(3);
        fields.put("place", "San Jose");
        UsergridEntity entitytwo = new UsergridEntity(collectionName,"amici");
        entitytwo.putProperties(fields);
        client.POST(entitytwo);
        entityone = Usergrid.GET(collectionName,"john").first();
        entitytwo = Usergrid.GET(collectionName,"amici").first();
        client.connect(entityone, "likes", entitytwo);
        client.connect(entityone.getType(), entityone.getUuid(), "visited", entitytwo.getUuid());


    }

    @Test
    public void clientGET()  {
        UsergridClient client = Usergrid.getInstance();

        //Retrieve the entity.
        UsergridResponse response = client.GET(collectionName, "john");

        //response.ok should be true
        assertTrue("no error thrown", response.getResponseError() == null);

        //response.entities should be an array
        assertTrue("reponse entities is an Array", response.getEntities().getClass() == ArrayList.class);

        //response.first should exist and have a valid uuid
        assertTrue("first entity is not null", response.first() != null);
        assertTrue("first entity is not null and has uuid", response.first().getUuid() != null);

        //response.entity should exist and have a valid uuid
        assertTrue("first entity is not null", response.entity() != null);
        assertTrue("first entity is not null and has uuid", response.entity().getUuid() != null);

        //response.last should exist and have a valid uuid
        assertTrue("last entity is not null", response.last() != null);
        assertTrue("last entity is not null and has uuid", response.last().getUuid() != null);


        UsergridQuery query = new UsergridQuery(collectionName)
                .eq("place", "San Jose");


    }


}
