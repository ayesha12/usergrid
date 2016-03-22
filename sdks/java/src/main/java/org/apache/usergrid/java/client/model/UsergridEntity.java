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
package org.apache.usergrid.java.client.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import org.apache.usergrid.java.client.Direction;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.exception.ClientException;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.apache.usergrid.java.client.utils.JsonUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;

import static org.apache.usergrid.java.client.utils.JsonUtils.*;
import static org.apache.usergrid.java.client.utils.MapUtils.newMapWithoutKeys;

public class UsergridEntity {

    private static final Logger log = LoggerFactory.getLogger(UsergridEntity.class);

    public static final String STR_UUID = "uuid";
    public static final String PROPERTY_UUID = STR_UUID;
    public static final String PROPERTY_TYPE = "type";
    public static final String STR_NAME = "name";
    public static final String CREATED_TIMESTAMP = "created";
    private static final String METADATA = "metadata";
    private static final String MODIFIED_TIMESTAMP = "modified";
    private static final String USER = "users";
    private static final String FILEMETADATA = "file-metadata";


    public static Map<String, Class<? extends UsergridEntity>> CLASS_FOR_ENTITY_TYPE = new HashMap<>();

    static {
        CLASS_FOR_ENTITY_TYPE.put(UsergridUser.ENTITY_TYPE, UsergridUser.class);
    }

    protected Map<String, JsonNode> properties = new HashMap<>();

    public UsergridEntity() {
    }

    public UsergridEntity(final String type) {
        changeType(type);
    }

    public UsergridEntity(final UsergridEntity fromCopy) {

    }

    public void setType(@Nonnull String type) {
        setStringProperty(properties, PROPERTY_TYPE, type);
    }

    public void setUuid(@Nonnull UUID uuid) {
        setUUIDProperty(properties, PROPERTY_UUID, uuid);
    }

    public void setName(@Nonnull String name) {
        setStringProperty(properties, STR_NAME, name);
    }

    public void setCreated(@Nonnull Long cTimeStamp) {
        setLongProperty(properties, CREATED_TIMESTAMP, cTimeStamp);
    }

    public void setModified(@Nonnull Long modified) {
        setLongProperty(properties, MODIFIED_TIMESTAMP, modified);
    }

    @JsonIgnore
    public String getNativeType() {
        return getType();
    }

    @JsonIgnore
    public List<String> getPropertyNames() {
        List<String> properties = new ArrayList<>();
        properties.add(PROPERTY_TYPE);
        properties.add(PROPERTY_UUID);
        return properties;
    }

    public String getType() {
        return JsonUtils.getStringProperty(properties, PROPERTY_TYPE);
    }

    public UUID getUuid() {
        return getUUIDProperty(properties, PROPERTY_UUID);
    }

    public String getUuidString() {
        return getStringProperty(PROPERTY_UUID);
    }


    public String getName() {
        return getStringProperty(STR_NAME);
    }


    public Long getCreated() {
        return getEntityProperty(CREATED_TIMESTAMP);
    }

    public Long getmodified() {
        return getEntityProperty(MODIFIED_TIMESTAMP);
    }

    @JsonAnyGetter
    public Map<String, JsonNode> getProperties() {
        return newMapWithoutKeys(properties, getPropertyNames());
    }

    public String getStringProperty(String name) {
        return JsonUtils.getStringProperty(this.properties, name);
    }

    public <T> T getEntityProperty(String name) {
        return JsonUtils.getProperty(this.properties, name);
    }

    @JsonAnySetter
    public void putproperty(@Nonnull final String name,
                            @Nonnull final JsonNode value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, value);
        }
    }

    /**
     * Set the property
     *
     * @param name
     * @param value
     */
    public UsergridEntity putproperty(@Nonnull String name, @Nonnull String value) {
        setStringProperty(properties, name, value);
        return this;
    }

    public UsergridEntity putproperty(@Nonnull String name, @Nonnull Boolean value) {
        setBooleanProperty(properties, name, value);
        return this;
    }

    /**
     * Set the property
     *
     * @param name
     * @param value
     */
    public UsergridEntity putproperty(@Nonnull String name, @Nonnull ArrayList value) {
        setArrayProperty(properties, name, value);
        return this;
    }


    /**
     * Set the property
     *
     * @param name
     * @param value
     */
    public void putproperty(@Nonnull String name, @Nonnull long value) {
        setLongProperty(properties, name, value);
    }

    /**
     * Set the property
     *
     * @param name
     * @param value
     */
    public void putproperty(@Nonnull String name, @Nonnull float value) {
        setFloatProperty(properties, name, value);
    }


    /**
     * Similar to putProperty(), but accepts a json string of properties. Immutable properties will be ignored.
     *
     * @param jsonString
     * @throws JSONException
     */
    public void putProperties(@Nonnull String jsonString) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonString);
        Iterator keys = jsonObj.keys();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object value = jsonObj.getJSONObject(key);
            if (value instanceof String)
                putproperty(key, value.toString());
            else if (value instanceof Integer)
                putproperty(key, (Integer) value);
            else if (value instanceof Long)
                putproperty(key, (Long) value);
            else if (value instanceof Float)
                putproperty(key, (Float) value);
            else if (value instanceof JsonNode)
                putproperty(key, (JsonNode) value);
        }
    }

    /**
     * Similar to putProperty(), but accepts a dictionary/hash-map of properties. Immutable properties will be ignored.
     *
     * @param : dictionary/hash-map
     * @throws JSONException
     */
    public void putProperties(@Nonnull Map<String, Object> jsonHash) throws JSONException {

        for (String key : jsonHash.keySet()) {
            Object value = jsonHash.get(key);
            if (value instanceof String)
                putproperty(key, value.toString());
            else if (value instanceof Integer)
                putproperty(key, (Integer) value);
            else if (value instanceof Long)
                putproperty(key, (Long) value);
            else if (value instanceof Float)
                putproperty(key, (Float) value);
            else if (value instanceof JsonNode)
                putproperty(key, (JsonNode) value);
        }
    }

    public void changeType(@Nonnull String type) {
        setStringProperty(properties, PROPERTY_TYPE, type);
    }

    @Override
    public String toString() {
        return toJsonString(this);
    }

    public <T extends UsergridEntity> T toType(Class<T> t) {
        return toType(this, t);
    }

    public static <T extends UsergridEntity> T toType(@Nonnull final UsergridEntity usergridEntity,
                                                      @Nonnull final Class<T> t) {
        if (usergridEntity == null) {
            return null;
        }

        T newEntity = null;

        if (usergridEntity.getClass().isAssignableFrom(t)) {
            try {
                newEntity = (t.newInstance());
                if ((newEntity.getNativeType() != null)
                        && newEntity.getNativeType().equals(usergridEntity.getType())) {
                    newEntity.properties = usergridEntity.properties;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return newEntity;
    }

    public static <T extends UsergridEntity> List<T> toType(@Nonnull final List<UsergridEntity> entities,
                                                            @Nonnull final Class<T> t) {

        List<T> l = new ArrayList<T>(entities != null ? entities.size() : 0);

        if (entities != null) {
            for (UsergridEntity usergridEntity : entities) {
                T newEntity = usergridEntity.toType(t);
                if (newEntity != null) {
                    l.add(newEntity);
                }
            }
        }

        return l;
    }

    /**
     * Retrieves an Entity by type and getName
     *
     * @param collectionName the getName of the collection
     * @param name           the getName or UUID of the object
     * @return
     */
    public static UsergridEntity GET(@Nonnull final String collectionName, @Nonnull final String name) {
        return GET(Usergrid.getInstance(), collectionName, name);
    }

    public static UsergridEntity GET(@Nonnull final UsergridClient client, @Nonnull final String collectionName, @Nonnull final String name) {
        return client.getEntity(collectionName, name).first();
    }

    /**
     * Performs a PUT of this entity using the Singleton client
     *
     * @throws ClientException
     */
    public UsergridResponse PUT() throws ClientException {

        return PUT(Usergrid.getInstance());
    }

    public UsergridResponse PUT(@Nonnull final UsergridClient client) throws ClientException {

        // check for one of: getName, uuid, error if not found
        if (this.getUuid() == null && this.getStringProperty("getName") == null)
            throw new IllegalArgumentException("No getName or uuid is present for the entity. Invalid argument");

        UsergridResponse response = client.PUT(this);

        this.refresh(response.first());

        return response;
    }

    /**
     * Performs a POST of the entity using the Singleton client
     *
     * @throws ClientException
     */
    public UsergridResponse POST() throws ClientException {
        return POST(Usergrid.getInstance());
    }

    public UsergridResponse POST(@Nonnull final UsergridClient client) throws ClientException {
        UsergridResponse response = client.POST(this);
        if (response != null) {
            if (response.responseError == null) {
                UsergridEntity first = response.first();
                if (first != null)
                    this.refresh(first);
            }
            return response;
        }
        throw new ClientException("Response was null on POST!");
    }

    /**
     * Performs a DELETE of this entity using the Singleton client
     *
     * @throws ClientException
     */
    public UsergridResponse DELETE() throws ClientException {
        return DELETE(Usergrid.getInstance());
    }

    public UsergridResponse DELETE(@Nonnull final UsergridClient client) throws ClientException {
        return client.DELETE(this);
    }

    /**
     * Will refresh this object with a response from the server.  For example, when you do a POST and get back the
     * entity after a POST and setting the UUID
     *
     * @param newEntity
     */
    private void refresh(@Nonnull final UsergridEntity newEntity) {
        if (newEntity == null)
            return;

        String uuid = newEntity.getStringProperty(STR_UUID);

        // make sure there is an entity and a uuid
        this.setUuid(UUID.fromString(uuid));

        //todo - what else?
    }

    public void save() {
        save(Usergrid.getInstance());
    }

    public void save(@Nonnull final UsergridClient client) {
        if (this.getUuidString() != null || this.getUuidString() != "")
            PUT(client);
        else
            POST(client);
    }

    public void remove() {
        remove(Usergrid.getInstance());
    }

    public void remove(@Nonnull final UsergridClient client) {
        DELETE(client);
    }

    public UsergridResponse connect(@Nonnull final String relation, @Nonnull final String type, @Nonnull final String name) {
        return connect(Usergrid.getInstance(), relation, type, name);
    }

    public UsergridResponse connect(@Nonnull final UsergridClient client, @Nonnull final String relation, @Nonnull final String type, @Nonnull final String name) {
        return client.connect(this.getType(), this.getName(), relation, type, name);
    }

    public UsergridResponse connect(@Nonnull final String connectionType, @Nonnull final String targetUUId) throws ClientException {
        return connect(Usergrid.getInstance(), connectionType, targetUUId);
    }

    public UsergridResponse connect(@Nonnull final UsergridClient client, @Nonnull final String connectionType, @Nonnull final String targetUUId) throws ClientException {
        return client.connect(this.getType(), this.getName(), connectionType, targetUUId.toString());
    }

    public UsergridResponse connect(@Nonnull final String connectionType, @Nonnull final UsergridEntity target) throws ClientException {
        return connect(Usergrid.getInstance(), connectionType, target);
    }

    public UsergridResponse connect(@Nonnull final UsergridClient client, @Nonnull final String connectionType, @Nonnull final UsergridEntity target) throws ClientException {

        if (target.getUuid() != null) {
            return client.connect(
                    this.getType(),
                    this.getUuid() != null ? this.getUuid().toString() : this.getName(),
                    connectionType,
                    target.getUuid().toString());

        } else if (target.getType() != null && target.getName() != null) {
            return client.connect(
                    this.getType(),
                    this.getUuid() != null ? this.getUuid().toString() : this.getName(),
                    connectionType,
                    target.getType(),
                    target.getName());

        } else {
            throw new IllegalArgumentException("One of UUID or Type+Name is required for the target entity of the connection");
        }
    }

    public UsergridResponse disconnect(@Nonnull final String connectionType,
                                       @Nonnull final String targetuuid) throws ClientException {
        return disconnect(Usergrid.getInstance(), connectionType, targetuuid);
    }

    public UsergridResponse disconnect(@Nonnull final UsergridClient client, @Nonnull final String connectionType,
                                       @Nonnull final String targetuuid) throws ClientException {
        return client.disconnect(this.getType(), this.getName(), connectionType, targetuuid);
    }

    public UsergridResponse disconnect(@Nonnull final String connectionType,
                                       @Nonnull final String type, @Nonnull final String name) throws ClientException {
        return disconnect(Usergrid.getInstance(), connectionType, type, name);
    }

    public UsergridResponse disconnect(@Nonnull final UsergridClient client, @Nonnull final String connectionType,
                                       @Nonnull final String type, @Nonnull final String name) throws ClientException {
        return client.disconnect(this.getType(), this.getName(), connectionType, type, name);
    }


    public UsergridResponse disconnect(@Nonnull final String connectionType,
                                       @Nonnull final UsergridEntity target) throws ClientException {
        return disconnect(Usergrid.getInstance(), connectionType, target);
    }

    public UsergridResponse disconnect(@Nonnull final UsergridClient client, @Nonnull final String connectionType,
                                       @Nonnull final UsergridEntity target) throws ClientException {

        if (target.getUuid() != null) {
            return client.disconnect(
                    this,
                    connectionType,
                    target);

        } else if (target.getType() != null && target.getName() != null) {
            return client.disconnect(
                    this,
                    connectionType,
                    target);

        } else {
            throw new IllegalArgumentException("One of UUID or Type+Name is required for the target entity of the connection");
        }
    }


    public List<UsergridEntity> getConnections(@Nonnull final Direction direction, @Nonnull final String relationship) {
        UsergridClient client = Usergrid.getInstance();
        client.ValidateEntity(this);
        UsergridResponse resp = client.getConnections(direction, this, relationship);
        return resp.getEntities();
    }


    public UsergridEntity reload() {
        return GET(this.getType(), this.getName());
    }

    /**
     * Will effectively delete a property when it is set to null.  The property will not be
     * removed from the entity on the server side until a PUT is made
     *
     * @param propertyName
     */
    public void removeEntityProperty(String propertyName) {
        putproperty(propertyName, "");
    }

    public void removeProperties(String[] propertyList) {
        for (int i = 0; i < propertyList.length; i++) {
            removeEntityProperty(propertyList[i].toString());
        }
    }


    public void prepend(String propertyName, Object arrToInsert) {
        ArrayList<Object> initialArr = getArrayNode(getEntityProperty(propertyName));
        this.insert(propertyName, arrToInsert, 0);

    }

    public void append(String propertyName, ArrayList arrToInsert) {
        ArrayList<Object> initialArr = getArrayNode(getEntityProperty(propertyName));
        this.insert(propertyName, arrToInsert, initialArr.size() + 10);
    }


    public void insert(String propertyName, Object arrToInsert, int indx) {
        if (indx < 0)
            indx = 0;
        ArrayList<Object> initialArr = getArrayNode(getEntityProperty(propertyName));
        ArrayList<Object> arrayToInsert = getArrayNode(arrToInsert);
        ArrayList<Object> aToAdd = insertIntoArray(initialArr, arrayToInsert, indx);
        putproperty(propertyName, aToAdd);

    }

    public void pop(String propertyName) {
        Object entityProperty = getEntityProperty(propertyName);
        if (entityProperty.getClass() == POJONode.class) {
            ArrayList<Object> newArrNode = (ArrayList) ((POJONode) entityProperty).getPojo();
            if (newArrNode.size() == 0)
                return;
            newArrNode.remove(newArrNode.size() - 1);
            putproperty(propertyName, newArrNode);
        }
        //else if property s not an array, it retrieves the property as it is.
//TODO : if the array is already empty.
    }

    public void shift(String propertyName) {
        Object entityProperty = getEntityProperty(propertyName);
        if (entityProperty.getClass() == POJONode.class) {
            ArrayList<Object> newArrNode = (ArrayList) ((POJONode) entityProperty).getPojo();
            if (newArrNode.size() == 0)
                return;
            newArrNode.remove(0);
            putproperty(propertyName, newArrNode);
        }
//TODO : if the array is already empty.
    }


    private ArrayList<Object> getArrayNode(Object arrayToInsert) {
        if (arrayToInsert == null || arrayToInsert == "")
            return null;

        ArrayList<Object> arrayList = new ArrayList<>();

        if (arrayToInsert.getClass() == POJONode.class) {
            arrayList = (ArrayList) ((POJONode) arrayToInsert).getPojo();
        } else if (arrayToInsert.getClass() == ArrayList.class)
            return (ArrayList<Object>) arrayToInsert;
        else
            arrayList.add(arrayToInsert);

        return arrayList;

    }

    private ArrayList<Object> insertIntoArray(ArrayList<Object> propertyArray, ArrayList<Object> arrayToInsert, int indx) {

        if (propertyArray == null || propertyArray.size() == 0)
            return arrayToInsert;

        if (arrayToInsert == null || arrayToInsert.size() == 0)
            return null;

        if (propertyArray.size() > 0 && indx == 0) {
            arrayToInsert.addAll(propertyArray);
            return arrayToInsert;
        } else if (propertyArray.size() > 0 && indx > 0) {
            if (indx > propertyArray.size()) {
                propertyArray.addAll(arrayToInsert);
                return propertyArray;
            }
            ArrayList<Object> mergedArray = new ArrayList<>();
            for (int i = 0; i < indx; i++) {
                mergedArray.add(propertyArray.get(i));
            }
            mergedArray.addAll(arrayToInsert);
            for (int i = 0; i < propertyArray.size() - indx; i++) {
                mergedArray.add(propertyArray.get(i + indx));
            }
            return mergedArray;
        }
        return null;
    }

    public Object metadata() {
        return getEntityProperty(METADATA);
    }


    public boolean isUser() {
        if (JsonUtils.getStringProperty(properties, PROPERTY_TYPE) == USER)
            return true;
        return false;
    }

    public boolean hasAsset() {
        if (getEntityProperty(FILEMETADATA) != null)
            if (this.getProperties().get(FILEMETADATA).findValue("content-length").intValue() > 0)
                return true;
        return false;
    }

    /**
     * Set the location of an entity
     *
     * @param latitude
     * @param longitude
     */
    public void setLocation(double latitude, double longitude) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode(); // will be of type ObjectNode
        rootNode.put("latitude", latitude);
        rootNode.put("longitude", longitude);

        setObjectProperty(properties, "location", rootNode);
    }


}