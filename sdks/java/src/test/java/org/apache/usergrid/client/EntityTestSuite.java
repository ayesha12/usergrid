package org.apache.usergrid.client;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.usergrid.java.client.Direction;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by ApigeeCorporation on 9/10/15.
 */
public class EntityTestSuite {
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
    UsergridEntity eLookUp = client.GET(collectionName,"testEntity1").first();
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

    UsergridEntity eLookup = client.GET(collectionName, "testEntity2").first();

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

    client.POST(e); //should work

    UsergridResponse r = client.POST(e); //should fail

    assertTrue("Second entity create should not succeed!", r.responseError != null);
  }

  @Test
  public void testEntityLookupByName() {
    String collectionName = "testEntityLookupByName" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");
    fields.put("shape", "square");

    String entityName = "testEntity4";

    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);

    UsergridEntity eLookup = client.GET(collectionName, entityName).first();

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

    UsergridEntity eLookup = client.GET(collectionName, e.getUuidString()).first();

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

    q = new UsergridQuery()
        .collectionName(collectionName)
        .eq("color", "red");

    eLookup = Usergrid.GET(q).first();
    assertTrue("The entity was not returned on lookup", eLookup != null);
    assertTrue("The returned entity does not have the same UUID", e.getUuidString().equals(eLookup.getUuidString()));

    q = new UsergridQuery(collectionName)
        .eq("name", entityName);

    eLookup = Usergrid.GET(q).first();

    assertTrue("The entity was not returned on lookup", eLookup != null);
    assertTrue("The returned entity does not have the same UUID", e.getUuidString().equals(eLookup.getUuidString()));

    q = new UsergridQuery()
        .collectionName(collectionName)
        .eq("shape", "square");

    eLookup = Usergrid.GET(q).first();

    assertTrue("The entity was not returned on lookup", eLookup != null);
    assertTrue("The returned entity does not have the same UUID", e.getUuidString().equals(eLookup.getUuidString()));

    q = new UsergridQuery(collectionName)
        .eq("shape", "circle");

    eLookup = Usergrid.GET(q).first();

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
    q = new UsergridQuery()
        .collectionName(collectionName)
        .eq("orientation", "up");

    UsergridEntity eLookup;

    eLookup = Usergrid.GET(q).first();

    assertTrue("The returned entity does not have the same UUID when querying by field", e.getUuidString().equals(eLookup.getUuidString()));

    e.putproperty("orientation", "down");

    client.PUT(e);


    eLookup = client.GET(collectionName, e.getUuidString()).first();

    assertTrue("The returned entity does not have the same UUID", e.getUuidString().equals(eLookup.getUuidString()));
    assertTrue("The field was not updated!", eLookup.getStringProperty("orientation").equals("down"));

    SDKTestUtils.sleep(1000);

    q = new UsergridQuery()
        .collectionName(collectionName)
        .eq("orientation", "up");

    eLookup = Usergrid.GET(q).first();

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
    q = new UsergridQuery()
        .collectionName(collectionName)
        .eq("orientation", "up");

    UsergridEntity eLookup;
    eLookup = Usergrid.GET(q).first();

    assertTrue("The returned entity was null!", eLookup != null);
    assertTrue("The returned entity does not have the same UUID when querying by field", e.getUuidString().equals(eLookup.getUuidString()));

    client.DELETE(e);

    eLookup = client.GET(collectionName, e.getUuidString()).first();

    assertTrue("The entity was not expected to be returned by UUID", eLookup == null);

    eLookup = client.GET(collectionName, e.getName()).first();

    assertTrue("The entity was not expected to be returned by getName", eLookup == null);

    q = new UsergridQuery()
        .collectionName(collectionName)
        .eq("color", "red");

    eLookup = Usergrid.GET(q).first();

    assertTrue("The entity was not expected to be returned", eLookup == null);

    q = new UsergridQuery()
        .collectionName(collectionName)
        .eq("shape", "square");

    eLookup = Usergrid.GET(q).first();

    assertTrue("The entity was not expected to be returned", eLookup == null);

    q = new UsergridQuery()
        .collectionName(collectionName)
        .eq("orientation", "up");

    eLookup = Usergrid.GET(q).first();

    assertTrue("The entity was not expected to be returned", eLookup == null);
  }

  @Test
  public void testEntityPutPropertyAndSave() {
    String collectionName = "testEntityPutProperty" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");
    fields.put("shape", "square");

    String entityName = "testEntity9";

    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);
    e.putproperty("orientation", "up");
    e.putproperty("sides", Integer.valueOf(4));
    e.save();

    UsergridEntity eLookUp = client.GET(collectionName,"testEntity9").first();

    //Check if the property was added correctly
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity putproperty() was successfull ", eLookUp.getStringProperty("orientation").equals("up"));
    assertTrue("The entity putproperty() was successfull ", eLookUp.getProperties().get("sides").asInt() == 4 );

    //Overwrite the property if it exists.
    e.putproperty("orientation","horizontal");
    e.save();
    eLookUp = client.GET(collectionName,"testEntity9").first();
    assertTrue("The entity putproperty() was successfull ", eLookUp.getStringProperty("orientation").equals("horizontal"));
    eLookUp = client.GET(collectionName,"entityNew").first();

    //should not be able to set the name key (name is immutable)
    e.setName("entityNew");
    e.save();
    eLookUp = client.GET(collectionName,"testEntity9").first();
    assertTrue("The entity putproperty() was successfull ", eLookUp.getName().equals("testEntity9"));
    eLookUp = client.GET(collectionName,"entityNew").first();
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

    UsergridEntity eLookUp = client.GET(collectionName, "testEntity9").first();
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

    UsergridEntity eLookUp = client.GET(collectionName, "testEntity9").first();
    assertTrue("The entity returned is not null.", eLookUp != null);

    String[] removeProperties = {"shape", "color"};
    e.removeProperties(removeProperties);
    e.save();

    eLookUp = client.GET(collectionName, "testEntity9").first();
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

    UsergridEntity eLookUp = client.GET(collectionName, "testEntity11").first();
    assertTrue("The entity returned is not null.", eLookUp != null);

    e.removeEntityProperty("color");
    e.removeEntityProperty("shape");
    e.save();

    eLookUp = client.GET(collectionName, "testEntity11").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("overwrite existing property",eLookUp.getStringProperty("color") == null);
    assertTrue("overwrite existing property",eLookUp.getStringProperty("shape") == null);

  }

  @Test
  public void testEntityAppendInArray() throws JSONException {
    String collectionName = "testEntityProperties" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");

    String entityName = "testEntity1";

    //should set properties for a given object, overwriting properties that exist and creating those that don\'t
    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);
    ArrayList<Object> lenArr = new ArrayList<>();
//    {1,2,3};
    lenArr.add(1);
    lenArr.add(2);
    lenArr.add(3);
    lenArr.add(4);
    e.putproperty("lenArray", lenArr);
    e.save();

    ArrayList<Object> lenArr2 = new ArrayList<>();

    lenArr2.add(6);
    lenArr2.add(7);

    e.append("lenArray", lenArr2);
    e.save();
    UsergridEntity eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);


    assertTrue("The entity returned is not null.", eLookUp != null);
    ArrayNode toCompare = new ArrayNode(JsonNodeFactory.instance);
    toCompare.add(1).add(2).add(3).add(4).add(6).add(7);
    assertTrue("The entity returned is not null.", eLookUp.getProperties().get("lenArray").equals(toCompare));

  }

  @Test
  public void testEntityPrependInArray() throws JSONException {
    String collectionName = "testEntityProperties" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");

    String entityName = "testEntity1";

    //should set properties for a given object, overwriting properties that exist and creating those that don\'t
    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);
    ArrayList<Object> lenArr = new ArrayList<>();
//    {1,2,3};
    lenArr.add(1);
    lenArr.add(2);
    lenArr.add(3);
    lenArr.add(4);
    e.putproperty("lenArray", lenArr);
    e.save();

    ArrayList<Object> lenArr2 = new ArrayList<>();

    lenArr2.add(6);
    lenArr2.add(7);

    e.prepend("lenArray", lenArr2);
    e.save();
    UsergridEntity eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);


    assertTrue("The entity returned is not null.", eLookUp != null);
    ArrayNode toCompare = new ArrayNode(JsonNodeFactory.instance);
    toCompare.add(6).add(7).add(1).add(2).add(3).add(4);
    assertTrue("The entity returned is not null.", eLookUp.getProperties().get("lenArray").equals(toCompare));

  }

  @Test
  public void testEntityPopInArray() throws JSONException {
    String collectionName = "testEntityProperties" + System.currentTimeMillis();
    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");
    String entityName = "testEntity1";

    //should remove the last value of an existing array
    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);
    ArrayList<Object> lenArr = new ArrayList<>();
    lenArr.add(1);
    lenArr.add(2);
    lenArr.add(3);
    e.putproperty("lenArray", lenArr);
    e.save();
    e.pop("lenArray");
    e.save();
    UsergridEntity eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);


    assertTrue("The entity returned is not null.", eLookUp != null);
    ArrayNode toCompare = new ArrayNode(JsonNodeFactory.instance);
    toCompare.add(1).add(2);
    assertTrue("The entity returned is not null.", eLookUp.getProperties().get("lenArray").equals(toCompare));


    //value should remain unchanged if it is not an array
    e.putproperty("foo","test1");
    e.save();
    e.pop("foo");
    eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    TextNode toCompare1 = new TextNode("test1");
    assertTrue("The entity returned is not null.", eLookUp.getProperties().get("foo").equals(toCompare1));


    //should gracefully handle empty arrays
    ArrayList<Object> lenArr2 = new ArrayList<>();
    e.putproperty("foo",lenArr2);
    e.save();
    e.pop("foo");
    eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    toCompare = new ArrayNode(JsonNodeFactory.instance);
    assertTrue("The entity returned is not null.", eLookUp.getProperties().get("foo").equals(toCompare));

  }


  @Test
  public void testEntityShiftInArray() throws JSONException {
    String collectionName = "testEntityProperties" + System.currentTimeMillis();
    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");
    String entityName = "testEntity1";

    //should remove the last value of an existing array
    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);
    ArrayList<Object> lenArr = new ArrayList<>();
    lenArr.add(1);
    lenArr.add(2);
    lenArr.add(3);
    e.putproperty("lenArray", lenArr);
    e.save();
    e.shift("lenArray");
    e.save();
    UsergridEntity eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);


    assertTrue("The entity returned is not null.", eLookUp != null);
    ArrayNode toCompare = new ArrayNode(JsonNodeFactory.instance);
    toCompare.add(2).add(3);
    assertTrue("The entity returned is not null.", eLookUp.getProperties().get("lenArray").equals(toCompare));


    //value should remain unchanged if it is not an array
    e.putproperty("foo","test1");
    e.save();
    e.shift("foo");
    eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    TextNode toCompare1 = new TextNode("test1");
    assertTrue("The entity returned is not null.", eLookUp.getProperties().get("foo").equals(toCompare1));


    //should gracefully handle empty arrays
    ArrayList<Object> lenArr2 = new ArrayList<>();
    e.putproperty("foo",lenArr2);
    e.save();
    e.shift("foo");
    eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    toCompare = new ArrayNode(JsonNodeFactory.instance);
    assertTrue("The entity returned is not null.", eLookUp.getProperties().get("foo").equals(toCompare));

  }


    @Test
  public void testEntityInsertInArray() throws JSONException {
    String collectionName = "testEntityProperties" + System.currentTimeMillis();

    Map<String, String> fields = new HashMap<>(3);
    fields.put("color", "red");

    String entityName = "testEntity1";

    //should set properties for a given object, overwriting properties that exist and creating those that don\'t
    UsergridEntity e = SDKTestUtils.createEntity(collectionName, entityName, fields);
    ArrayList<Object> lenArr = new ArrayList<>();
//    {1,2,3};
    lenArr.add(1);
    lenArr.add(2);
    lenArr.add(3);
    lenArr.add(4);
    e.putproperty("lenArray",lenArr);
    e.save();

    ArrayList<Object> lenArr2 = new ArrayList<>();

    lenArr2.add(6);
    lenArr2.add(7);

    e.insert("lenArray",lenArr2,6);
    e.save();
    UsergridEntity eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);


    assertTrue("The entity returned is not null.", eLookUp != null);
    ArrayNode toCompare = new ArrayNode(JsonNodeFactory.instance);
    toCompare.add(1).add(2).add(3).add(4).add(6).add(7);
    assertTrue("The entity returned is not null.", eLookUp.getProperties().get("lenArray").equals(toCompare));


      //should merge an array of values into an existing array at the specified index
    lenArr = new ArrayList<>();
//    {1,2,3};
    lenArr.add(1);
    lenArr.add(2);
    lenArr.add(3);
    lenArr.add(4);

    e.putproperty("lenArray",lenArr);
    e.save();
    lenArr2 = new ArrayList<>();

//    {1,2,3};
    lenArr2.add(5);
    lenArr2.add(6);
    lenArr2.add(7);
    lenArr2.add(8);

    e.insert("lenArray",lenArr2 ,2);
    e.save();
    eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    toCompare = new ArrayNode(JsonNodeFactory.instance);
    toCompare.add(1).add(2).add(5).add(6).add(7).add(8).add(3).add(4);
    assertTrue("The entity returned is not null.", eLookUp.getProperties().get("lenArray").equals(toCompare));

    //should convert an existing value into an array when inserting a second value
    e.putproperty("foo","test");
    e.save();
    e.insert("foo","test1",1);
    e.save();
    eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    toCompare = new ArrayNode(JsonNodeFactory.instance);
    toCompare.add("test").add("test1");

    assertTrue("The entity returned is not null.", eLookUp.getProperties().get("foo").equals(toCompare));

      //should create a new array when a property does not exist
      e.insert("foo1","test2",1);
      e.save();
      eLookUp = client.GET(collectionName, "testEntity1").first();
      assertTrue("The entity returned is not null.", eLookUp != null);
      toCompare = new ArrayNode(JsonNodeFactory.instance);
      toCompare.add("test2");

      assertTrue("The entity returned is not null.", eLookUp.getProperties().get("foo1").equals(toCompare));

    //should gracefully handle indexes out of range

      e.putproperty("Arrindex","test1");
      e.save();
      e.insert("Arrindex","test2",1000);
      e.save();
      eLookUp = client.GET(collectionName, "testEntity1").first();
      assertTrue("The entity returned is not null.", eLookUp != null);
      toCompare = new ArrayNode(JsonNodeFactory.instance);
      toCompare.add("test1").add("test2");
      assertTrue("The entity returned is not null.", eLookUp.getProperties().get("Arrindex").equals(toCompare));

      e.insert("Arrindex","test3",-1000);
      e.save();
      eLookUp = client.GET(collectionName, "testEntity1").first();
      assertTrue("The entity returned is not null.", eLookUp != null);
      toCompare = new ArrayNode(JsonNodeFactory.instance);
      toCompare.add("test3").add("test1").add("test2");
      assertTrue("The entity returned is not null.", eLookUp.getProperties().get("Arrindex").equals(toCompare));



    }

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
    UsergridEntity eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity returned is not null.", eLookUp.getConnections(Direction.OUT,"likes").get(0).getName().equals("testEntity2"));
    UsergridEntity eLookUp2 = client.GET(collectionName, "testEntity2").first();
    assertTrue("The entity returned is not null.", eLookUp2.getConnections(Direction.IN,"likes").get(0).getName().equals("testEntity1"));


    e1.disconnect("likes",e2);
    e1.save();
    eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity returned is not null.", eLookUp.getConnections(Direction.OUT,"likes").size() == 0);

    //should connect entities by passing target uuid as a parameter
    e1.connect("visited",e2.getUuid().toString());
    e1.save();
    eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity returned is not null.", eLookUp.getConnections(Direction.OUT,"visited").get(0).getName().equals("testEntity2"));

    e1.disconnect("visited",e2.getUuid().toString());
    e1.save();
    eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity returned is not null.", eLookUp.getConnections(Direction.OUT,"visited").size() == 0);

    //should connect entities by passing target type and name as parameters
    e1.connect("revisit",e2.getType(),e2.getName());
    e1.save();
    eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity returned is not null.", eLookUp.getConnections(Direction.OUT,"revisit").get(0).getName().equals("testEntity2"));

    e1.disconnect("revisit",e2.getType(),e2.getName());
    e1.save();
    eLookUp = client.GET(collectionName, "testEntity1").first();
    assertTrue("The entity returned is not null.", eLookUp != null);
    assertTrue("The entity returned is not null.", eLookUp.getConnections(Direction.OUT,"revisit").size() == 0);
  }
}
