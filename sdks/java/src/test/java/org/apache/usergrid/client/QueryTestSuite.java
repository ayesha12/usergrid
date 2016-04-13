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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
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

import static org.junit.Assert.*;

public class QueryTestSuite {

    public static final String COLLECTION = "shapes";

    public static UsergridClient client = null;

    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    @Before
    public void before() {
        Usergrid.initSharedInstance(SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.authFallBack);
        Usergrid.authenticateApp(new UsergridAppAuth(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET));
        client = Usergrid.getInstance();

    }

    @After
    public void after() {
        Usergrid.reset();
    }

    /**
     * Test a basic set of queries where there is inclusion and exclusion based on
     * two fields
     */
    @Test
    public void testBasicQuery() {

        UsergridQuery qDelete = new UsergridQuery(COLLECTION);
        Usergrid.DELETE(qDelete);

        Map<String, UsergridEntity> entityMapByUUID = SDKTestUtils.createColorShapes(COLLECTION);
        Map<String, UsergridEntity> entityMapByName = new HashMap<>(entityMapByUUID.size());

        for (Map.Entry<String, UsergridEntity> uuidEntity : entityMapByUUID.entrySet()) {
            entityMapByName.put(uuidEntity.getValue().getName(), uuidEntity.getValue());
        }

        SDKTestUtils.indexSleep();

        Map<String, String> fields = new HashMap<>(7);
        fields.put("red", "square");
        fields.put("blue", "circle");
        fields.put("yellow", "triangle");


        for (Map.Entry<String, String> entry : fields.entrySet()) {
            UsergridEntity targetEntity = entityMapByName.get(entry.getKey() + entry.getValue());

            UsergridQuery q = new UsergridQuery(COLLECTION).eq("color", entry.getKey());
            UsergridResponse r = Usergrid.GET(q);

            assertTrue("query for " + entry.getKey() + " shape should return 1, not: " + r.getEntities().size(), r.getEntities().size() == 1);
            assertTrue("query for " + entry.getKey() + " shape should the right UUID", r.first().getUuid().equals(targetEntity.getUuid()));
        }
        Usergrid.DELETE(qDelete);
    }

    /**
     * Test that geolocation is working as expected with different ranges and radius
     * also test that results are sorted ascending by distance from the specified point
     */
    @Test
    public void testGeoQuery() {

        String collectionName = "sdktestlocation";

        UsergridQuery deleteQuery = new UsergridQuery(collectionName);
        Usergrid.DELETE(deleteQuery);

        ArrayList<UsergridEntity> entities = new ArrayList<>();
        UsergridEntity apigeeOffice = new UsergridEntity(collectionName,"Apigee Office");
        apigeeOffice.setLocation(37.334115, -121.894340);
        entities.add(apigeeOffice);

        UsergridEntity amicis = new UsergridEntity(collectionName,"Amicis");
        amicis.setLocation(37.335616, -121.894168);
        entities.add(amicis);

        UsergridEntity sanPedroMarket = new UsergridEntity(collectionName,"SanPedroMarket");
        sanPedroMarket.setLocation(37.336499, -121.894356);
        entities.add(sanPedroMarket);

        UsergridEntity saintJamesPark = new UsergridEntity(collectionName,"saintJamesPark");
        saintJamesPark.setLocation(37.339079, -121.891422);
        entities.add(saintJamesPark);

        UsergridEntity sanJoseNews = new UsergridEntity(collectionName,"sanJoseNews");
        sanJoseNews.setLocation(37.337812, -121.890784);
        entities.add(sanJoseNews);

        UsergridEntity deAnza = new UsergridEntity(collectionName,"deAnza");
        deAnza.setLocation(37.334370, -121.895081);
        entities.add(deAnza);

        Usergrid.POST(entities);

        SDKTestUtils.indexSleep();

        float centerLat = 37.334110f;
        float centerLon = -121.894340f;

        UsergridQuery q1 = new UsergridQuery(collectionName).locationWithin(611.00000, centerLat, centerLon);

        UsergridResponse qr = Usergrid.GET(q1);
        assertNotNull(qr.getEntities());

        UsergridEntity lastEntity = null;
        for (UsergridEntity entity : qr.getEntities()) {

            JsonNode locationNode = entity.getEntityProperty("location");
            DoubleNode lat = (DoubleNode) locationNode.get("latitude");
            DoubleNode lon = (DoubleNode) locationNode.get("longitude");

            float d1 = distFrom(centerLat, centerLon, lat.floatValue(), lon.floatValue());
            System.out.println("Entity " + entity.getName() + " is " + d1 + " away");

            assertTrue("Entity " + entity.getName() + " was included but is not within specified distance (" + d1 + ")", d1 <= 611.0);

            if (lastEntity != null) {
                JsonNode lastLocationNode = lastEntity.getEntityProperty("location");
                DoubleNode lastLat = (DoubleNode) lastLocationNode.get("latitude");
                DoubleNode lastLon = (DoubleNode) lastLocationNode.get("longitude");

                float d2 = distFrom(centerLat, centerLon, lastLat.floatValue(), lastLon.floatValue());

//        assertTrue("GEO results are not sorted by distance descending: expected " + d1 + " <= " + d2, d1 <= d2);
            }

            lastEntity = entity;
        }
        UsergridQuery q2 = new UsergridQuery(collectionName).locationWithin(150, centerLat, centerLon);

        UsergridResponse qr2 = Usergrid.GET(q2);

        for (UsergridEntity entity : qr2.getEntities()) {

            JsonNode locationNode = entity.getEntityProperty("location");
            DoubleNode lat = (DoubleNode) locationNode.get("latitude");
            DoubleNode lon = (DoubleNode) locationNode.get("longitude");

            float d1 = distFrom(centerLat, centerLon, lat.floatValue(), lon.floatValue());
            System.out.println("Entity " + entity.getName() + " is " + d1 + " away");

            assertTrue("Entity " + entity.getName() + " was included but is not within specified distance (" + d1 + ")", d1 <= 150);

            if (lastEntity != null) {
                JsonNode lastLocationNode = lastEntity.getEntityProperty("location");
                DoubleNode lastLat = (DoubleNode) lastLocationNode.get("latitude");
                DoubleNode lastLon = (DoubleNode) lastLocationNode.get("longitude");

                float d2 = distFrom(centerLat, centerLon, lastLat.floatValue(), lastLon.floatValue());

//        assertTrue("GEO results are not sorted by distance descending: expected " + d1 + " <= " + d2, d1 <= d2);
            }

            lastEntity = entity;
        }
    }
}
