package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Direction;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by ApigeeCorporation on 9/10/15.
 */
public class EntityTestSuite {

  @Before
  public void before() {
    Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
    Usergrid.authorizeAppClient(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
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
    UsergridEntity eLookUp = UsergridEntity.GET(collectionName,"testEntity1");
    assertTrue("The returned entity is null!", eLookUp != null); //    entity has been created
    assertTrue("The returned entity is redsquare!", eLookUp.getName().equals("testEntity1")); //    entity has been created

  }

  @Test
  public void testCollectionNameLength() {
    String collectionName = "testCollectionNameLength" + System.currentTimeMillis();
    collectionName += collectionName;
    collectionName += collectionName;
    collectionName += collectionName;
    collectionName += collectionName;
    collectionName += collectionName;
    collectionName += collectionName;
    collectionName += collectionName;

    Map<String, Map<String, String>> entityMap = new HashMap<>(7);

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");
    fields.put("shape", "square");

    entityMap.put("testEntity2", fields);

    UsergridEntity e = SDKTestUtils.createEntity(collectionName, "testEntity2", fields);

    UsergridEntity eLookup = UsergridEntity.GET(collectionName, "testEntity2");

    assertTrue("The returned entity is null!", eLookup != null);
    assertTrue("The returned entity does not have the same UUID", e.getUuidString().equals(eLookup.getUuidString()));
  }


  @Test
  public void testDuplicateEntityNameFailure() {
    String collectionName = "testDuplicateEntityNameFailure" + System.currentTimeMillis();

    Map<String, Map<String, String>> entityMap = new HashMap<>(7);

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");
    fields.put("shape", "square");

    entityMap.put("testEntity3", fields);

    UsergridEntity e = SDKTestUtils.createEntity(collectionName, "testEntity3", fields);

    e.POST(); //should work

    UsergridResponse r = e.POST(); //should fail

    assertTrue("Second entity create should not succeed!", r.getError() != null);
  }

  @Test
  public void testEntityLookupByName() {
    String collectionName = "testEntityLookupByName" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");
    fields.put("shape", "square");

    String entityName = "testEntity4";

    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);

    UsergridEntity eLookup = UsergridEntity.GET(collectionName, entityName);

    assertTrue("The returned entity is null!", eLookup != null);
    assertTrue("The returned entity does not have the same UUID", e.getUuidString().equals(eLookup.getUuidString()));
  }

  @Test
  public void testEntityLookupByUUID() {
    String collectionName = "testEntityLookupByUUID" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");
    fields.put("shape", "square");

    String entityName = "testEntity5";

    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);

    UsergridEntity eLookup = UsergridEntity.GET(collectionName, e.getUuidString());

    assertTrue("The returned entity is null!", eLookup != null);
    assertTrue("The returned entity does not have the same UUID", e.getUuidString().equals(eLookup.getUuidString()));
  }

  @Test
  public void testEntityLookupByQuery() {
    String collectionName = "testEntityLookupByQuery" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");
    fields.put("shape", "square");

    String entityName = "testEntity6";

    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);

    UsergridQuery q;
    UsergridEntity eLookup;

    SDKTestUtils.indexSleep();

    q = new UsergridQuery.Builder()
        .collection(collectionName)
        .eq("color", "red").build();

    eLookup = q.GET().first();
    assertTrue("The entity was not returned on lookup", eLookup != null);
    assertTrue("The returned entity does not have the same UUID", e.getUuidString().equals(eLookup.getUuidString()));

    q = new UsergridQuery.Builder()
        .collection(collectionName)
        .eq("name", entityName).build();

    eLookup = q.GET().first();

    assertTrue("The entity was not returned on lookup", eLookup != null);
    assertTrue("The returned entity does not have the same UUID", e.getUuidString().equals(eLookup.getUuidString()));

    q = new UsergridQuery.Builder()
        .collection(collectionName)
        .eq("shape", "square").build();

    eLookup = q.GET().first();

    assertTrue("The entity was not returned on lookup", eLookup != null);
    assertTrue("The returned entity does not have the same UUID", e.getUuidString().equals(eLookup.getUuidString()));

    q = new UsergridQuery.Builder()
        .collection(collectionName)
        .eq("shape", "circle").build();

    eLookup = q.GET().first();

    assertTrue("The entity was not expected to be returned on lookup", eLookup == null);
  }

  @Test
  public void testEntityUpdate() {
    String collectionName = "testEntityLookupByUUID" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");
    fields.put("shape", "square");
    fields.put("orientation", "up");

    String entityName = "testEntity7";

    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);

    SDKTestUtils.sleep(1000);

    UsergridQuery q;
    q = new UsergridQuery.Builder()
        .collection(collectionName)
        .eq("orientation", "up").build();

    UsergridEntity eLookup;

    eLookup = q.GET().first();

    assertTrue("The returned entity does not have the same UUID when querying by field", e.getUuidString().equals(eLookup.getUuidString()));

    e.putproperty("orientation", "down");

    e.PUT();


    eLookup = UsergridEntity.GET(collectionName, e.getUuidString());

    assertTrue("The returned entity does not have the same UUID", e.getUuidString().equals(eLookup.getUuidString()));
    assertTrue("The field was not updated!", eLookup.getStringProperty("orientation").equals("down"));

    SDKTestUtils.sleep(1000);

    q = new UsergridQuery.Builder()
        .collection(collectionName)
        .eq("orientation", "up").build();

    eLookup = q.GET().first();

    assertTrue("The entity was returned for old value!", eLookup == null);
  }

  @Test
  public void testEntityDelete() {
    String collectionName = "testEntityDelete" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");
    fields.put("shape", "square");
    fields.put("orientation", "up");

    String entityName = "testEntity8";

    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);

    SDKTestUtils.indexSleep();

    UsergridQuery q;
    q = new UsergridQuery.Builder()
        .collection(collectionName)
        .eq("orientation", "up").build();

    UsergridEntity eLookup;
    eLookup = q.GET().first();

    assertTrue("The returned entity was null!", eLookup != null);
    assertTrue("The returned entity does not have the same UUID when querying by field", e.getUuidString().equals(eLookup.getUuidString()));

    e.DELETE();

    eLookup = UsergridEntity.GET(collectionName, e.getUuidString());

    assertTrue("The entity was not expected to be returned by UUID", eLookup == null);

    eLookup = UsergridEntity.GET(collectionName, e.getName());

    assertTrue("The entity was not expected to be returned by getName", eLookup == null);

    q = new UsergridQuery.Builder()
        .collection(collectionName)
        .eq("color", "red")
        .build();

    eLookup = q.GET().first();

    assertTrue("The entity was not expected to be returned", eLookup == null);

    q = new UsergridQuery.Builder()
        .collection(collectionName)
        .eq("shape", "square")
        .build();

    eLookup = q.GET().first();

    assertTrue("The entity was not expected to be returned", eLookup == null);

    q = new UsergridQuery.Builder()
        .collection(collectionName)
        .eq("orientation", "up")
        .build();

    eLookup = q.GET().first();

    assertTrue("The entity was not expected to be returned", eLookup == null);
  }

  @Test
  public void testEntityPutProperty() {
    String collectionName = "testEntityPutProperty" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");
    fields.put("shape", "square");

    String entityName = "testEntity9";

    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);
    e.putproperty("orientation", "up");
    e.putproperty("sides", Integer.valueOf(4));
    e.save();

    UsergridEntity eLookUp = UsergridEntity.GET(collectionName,"testEntity9");

    //Check if the property was added correctly
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity putproperty() was successfull ", eLookUp.getStringProperty("orientation").equals("up"));
    assertTrue("The entity putproperty() was successfull ", eLookUp.getProperties().get("sides").asInt() == 4 );

    //Overwrite the property if it exists.
    e.putproperty("orientation","horizontal");
    e.save();
    eLookUp = UsergridEntity.GET(collectionName,"testEntity9");
    assertTrue("The entity putproperty() was successfull ", eLookUp.getStringProperty("orientation").equals("horizontal"));

    //should not be able to set the name key (name is immutable)
    e.setName("entityNew");
    e.save();
    eLookUp = UsergridEntity.GET(collectionName,"testEntity9");
    assertTrue("The entity putproperty() was successfull ", eLookUp.getName().equals("testEntity9"));
    eLookUp = UsergridEntity.GET(collectionName,"entityNew");
    assertTrue("The entity putproperty() was successfull ", eLookUp == null);

  }

  @Test
  public void testEntityPutProperties() throws JSONException {
    String collectionName = "testEntityProperties" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");

    String entityName = "testEntity9";

    //should set properties for a given object, overwriting properties that exist and creating those that don\'t
    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);
    Map<String, Object> properties = new HashMap<>();
    properties.put("shape", "square");
    properties.put("orientation", "up");
    properties.put("color","black");
    e.putProperties(properties);
    e.save();

    UsergridEntity eLookUp = UsergridEntity.GET(collectionName, "testEntity9");
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity putproperty() was successfull ", eLookUp.getStringProperty("orientation").equals("up"));
    assertTrue("overwrite existing property",eLookUp.getStringProperty("color").equals("black"));

  }

  @Test
  public void testEntityRemovePropertiesAndSave() throws JSONException {
    String collectionName = "testEntityProperties" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");

    String entityName = "testEntity9";

    //should set properties for a given object, overwriting properties that exist and creating those that don\'t
    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);
    Map<String, Object> properties = new HashMap<>();
    properties.put("shape", "square");
    properties.put("orientation", "up");
    properties.put("color","black");
    e.putProperties(properties);
    e.save();

    UsergridEntity eLookUp = UsergridEntity.GET(collectionName, "testEntity9");
    assertTrue("The entity returned is not null.", eLookUp != null);

    String[] removeProperties = {"shape", "color"};
    e.removeProperties(removeProperties);
    e.save();

    eLookUp = UsergridEntity.GET(collectionName, "testEntity9");
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("overwrite existing property",eLookUp.getStringProperty("color") == null);
    assertTrue("overwrite existing property",eLookUp.getStringProperty("shape") == null);

  }


  @Test
  public void testEntityRemoveProperty() throws JSONException {
    String collectionName = "testEntityProperties" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");

    String entityName = "testEntity11";

    //should set properties for a given object, overwriting properties that exist and creating those that don\'t
    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);
    Map<String, Object> properties = new HashMap<>();
    properties.put("shape", "square");
    properties.put("orientation", "up");
    properties.put("color","black");
    e.putProperties(properties);
    e.save();

    UsergridEntity eLookUp = UsergridEntity.GET(collectionName, "testEntity11");
    assertTrue("The entity returned is not null.", eLookUp != null);

    e.removeEntityProperty("color");
    e.removeEntityProperty("shape");
    e.save();

    eLookUp = UsergridEntity.GET(collectionName, "testEntity11");
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("overwrite existing property",eLookUp.getStringProperty("color") == null);
    assertTrue("overwrite existing property",eLookUp.getStringProperty("shape") == null);

  }



//  @Test
//  public void testEntityInsertInArray() throws JSONException {
//    String collectionName = "testEntityProperties" + System.currentTimeMillis();
//
//    Map<String, String> fields = new HashMap<>(3);
//    fields.put("color", "red");
//
//    String entityName = "testEntity1";
//
//    //should set properties for a given object, overwriting properties that exist and creating those that don\'t
//    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);
//    Object[] lenArr = {1,2,3};
//    e.putproperty("lenArray",lenArr);
//    e.save();
//
//    e.insert("lenArray",1,6);
//    e.save();
//    UsergridEntity eLookUp = UsergridEntity.GET(collectionName, "testEntity1");
//    assertTrue("The entity returned is not null.", eLookUp != null);
//
//    assertTrue("The entity returned is not null.", eLookUp != null);
//    Object[] toCompare = {1,2,3,1};
//
//
//  }

  @Test
  public void testEntityConnectDisConnectGetConnections() throws JSONException {
    String collectionName = "testEntityProperties" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(1);
    fields.put("color", "red");

    String entityName = "testEntity1";

    //should set properties for a given object, overwriting properties that exist and creating those that don\'t
    UsergridEntity e1 = SDKTestUtils.createEntity(collectionName, entityName, fields);
    Map<String, Object> properties1 = new HashMap<>();
    properties1.put("shape", "square");
    e1.putProperties(properties1);
    e1.save();

    entityName = "testEntity2";
    UsergridEntity e2 = SDKTestUtils.createEntity(collectionName, entityName, fields);
    Map<String, Object> properties2 = new HashMap<>();
    properties2.put("color","green");
    properties2.put("shape", "circle");
    e2.putProperties(properties2);
    e2.save();

    //should connect entities by passing a target UsergridEntity object as a parameter
    e1.connect("likes",e2);
    e1.save();
    UsergridEntity eLookUp = UsergridEntity.GET(collectionName, "testEntity1");
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity returned is not null.", eLookUp.getConnections(Direction.OUT,"likes").get(0).getName().equals("testEntity2"));
    UsergridEntity eLookUp2 = UsergridEntity.GET(collectionName, "testEntity2");
    assertTrue("The entity returned is not null.", eLookUp2.getConnections(Direction.IN,"likes").get(0).getName().equals("testEntity1"));


    e1.disconnect("likes",e2);
    e1.save();
    eLookUp = UsergridEntity.GET(collectionName, "testEntity1");
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity returned is not null.", eLookUp.getConnections(Direction.OUT,"likes").size() == 0);

    //should connect entities by passing target uuid as a parameter
    e1.connect("visited",e2.getUuid().toString());
    e1.save();
    eLookUp = UsergridEntity.GET(collectionName, "testEntity1");
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity returned is not null.", eLookUp.getConnections(Direction.OUT,"visited").get(0).getName().equals("testEntity2"));

    e1.disconnect("visited",e2.getUuid().toString());
    e1.save();
    eLookUp = UsergridEntity.GET(collectionName, "testEntity1");
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity returned is not null.", eLookUp.getConnections(Direction.OUT,"visited").size() == 0);

    //should connect entities by passing target type and name as parameters
    e1.connect("revisit",e2.getType(),e2.getName());
    e1.save();
    eLookUp = UsergridEntity.GET(collectionName, "testEntity1");
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity returned is not null.", eLookUp.getConnections(Direction.OUT,"revisit").get(0).getName().equals("testEntity2"));

    e1.disconnect("revisit",e2.getType(),e2.getName());
    e1.save();
    eLookUp = UsergridEntity.GET(collectionName, "testEntity1");
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity returned is not null.", eLookUp.getConnections(Direction.OUT,"revisit").size() == 0);

  }


}
