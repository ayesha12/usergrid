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
import org.apache.usergrid.java.client.UsergridEnums.Direction;
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

    public static final String STR_UUID = "uuid";
    public static final String PROPERTY_UUID = STR_UUID;
    public static final String PROPERTY_TYPE = "type";
    public static final String STR_NAME = "name";
    public static final String CREATED_TIMESTAMP = "created";
    private static final Logger log = LoggerFactory.getLogger(UsergridEntity.class);
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
        setType(type);
    }

    public UsergridEntity(final UsergridEntity fromCopy) {

    }

    @Nonnull
    public static <T extends UsergridEntity> T toType(@Nonnull final UsergridEntity usergridEntity,
                                                      @Nonnull final Class<T> t) {
        if (usergridEntity == null) {
            return null;
        }

        T newEntity = null;

        if (usergridEntity.getClass().isAssignableFrom(t)) {
            try {
                newEntity = (t.newInstance());
                if ((newEntity.getType() != null)
                        && newEntity.getType().equals(usergridEntity.getType())) {
                    newEntity.properties = usergridEntity.properties;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return newEntity;
    }

    @Nonnull
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

    public void setType(@Nonnull String type) {
        setStringProperty(properties, PROPERTY_TYPE, type);
    }

    public void setUuid(@Nonnull UUID uuid) {
        setUUIDProperty(properties, PROPERTY_UUID, uuid);
    }

    public void setName(@Nonnull String name) {
        setStringProperty(properties, STR_NAME, name);
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
    public void putProperty(@Nonnull final String name,
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
    @Nonnull
    public UsergridEntity   putProperty(@Nonnull String name, @Nonnull String value) {
        setStringProperty(properties, name, value);
        return this;
    }

    @Nonnull
    public UsergridEntity putProperty(@Nonnull String name, @Nonnull Boolean value) {
        setBooleanProperty(properties, name, value);
        return this;
    }

    /**
     * Set the property
     *
     * @param name
     * @param value
     */
    @Nonnull
    public UsergridEntity putProperty(@Nonnull String name, @Nonnull ArrayList value) {
        setArrayProperty(properties, name, value);
        return this;
    }

    /**
     * Set the property
     *
     * @param name
     * @param value
     */
    @Nonnull
    public void putProperty(@Nonnull String name, @Nonnull long value) {
        setLongProperty(properties, name, value);
    }

    /**
     * Set the property
     *
     * @param name
     * @param value
     */
    @Nonnull
    public void putProperty(@Nonnull String name, @Nonnull float value) {
        setFloatProperty(properties, name, value);
    }

    /**
     * Similar to putProperty(), but accepts a json string of properties. Immutable properties will be ignored.
     *
     * @param jsonString
     * @throws JSONException
     */
    @Nonnull
    public void putProperties(@Nonnull String jsonString) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonString);
        Iterator keys = jsonObj.keys();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object value = jsonObj.getJSONObject(key);
            if (value instanceof String)
                putProperty(key, value.toString());
            else if (value instanceof Integer)
                putProperty(key, (Integer) value);
            else if (value instanceof Long)
                putProperty(key, (Long) value);
            else if (value instanceof Float)
                putProperty(key, (Float) value);
            else if (value instanceof JsonNode)
                putProperty(key, (JsonNode) value);
        }
    }

    /**
     * Similar to putProperty(), but accepts a dictionary/hash-map of properties. Immutable properties will be ignored.
     *
     * @param : dictionary/hash-map
     * @throws JSONException
     */
    @Nonnull
    public void putProperties(@Nonnull Map<String, Object> jsonHash) throws JSONException {

        for (String key : jsonHash.keySet()) {
            Object value = jsonHash.get(key);
            if (value instanceof String)
                putProperty(key, value.toString());
            else if (value instanceof Integer)
                putProperty(key, (Integer) value);
            else if (value instanceof Long)
                putProperty(key, (Long) value);
            else if (value instanceof Float)
                putProperty(key, (Float) value);
            else if (value instanceof JsonNode)
                putProperty(key, (JsonNode) value);
        }
    }

    @Override
    public String toString() {
        return toJsonString(this);
    }

    public <T extends UsergridEntity> T toType(Class<T> t) {
        return toType(this, t);
    }

    public void save() {
        save(Usergrid.getInstance());
    }

    public void save(@Nonnull final UsergridClient client) {
        if (this.getUuidString() != null || this.getUuidString() != "")
            client.PUT(this);
        else
            client.POST(this);

    }

    public void remove() {
        remove(Usergrid.getInstance());
    }

    public void remove(@Nonnull final UsergridClient client) {
        client.DELETE(this);
    }

    @Nonnull
    public UsergridResponse connect(@Nonnull final String relationship, @Nonnull final String type, @Nonnull final String name) {
        return connect(Usergrid.getInstance(), relationship, type, name);
    }

    @Nonnull
    public UsergridResponse connect(@Nonnull final UsergridClient client, @Nonnull final String relationship, @Nonnull final String type, @Nonnull final String name) {
        return client.connect(this.getType(), this.getName(), relationship, type, name);
    }

    @Nonnull
    public UsergridResponse connect(@Nonnull final String relationship, @Nonnull final String targetUUId) throws ClientException {
        return connect(Usergrid.getInstance(), relationship, targetUUId);
    }

    @Nonnull
    public UsergridResponse connect(@Nonnull final UsergridClient client, @Nonnull final String relationship, @Nonnull final String targetUuid) throws ClientException {
        return client.connect(this.getType(), this.getName(), relationship, targetUuid);
    }

    @Nonnull
    public UsergridResponse connect(@Nonnull final String relationship, @Nonnull final UsergridEntity target) throws ClientException {
        return connect(Usergrid.getInstance(), relationship, target);
    }

    @Nonnull
    public UsergridResponse connect(@Nonnull final UsergridClient client, @Nonnull final String relationship, @Nonnull final UsergridEntity target) throws ClientException {

        if (target.getUuid() != null) {
            return client.connect(
                    this.getType(),
                    this.getUuid() != null ? this.getUuid().toString() : this.getName(),
                    relationship,
                    target.getUuid().toString());

        } else if (target.getType() != null && target.getName() != null) {
            return client.connect(
                    this.getType(),
                    this.getUuid() != null ? this.getUuid().toString() : this.getName(),
                    relationship,
                    target.getType(),
                    target.getName());

        } else {
            throw new IllegalArgumentException("One of UUID or Type+Name is required for the target entity of the connection");
        }
    }

    @Nonnull
    public UsergridResponse disConnect(@Nonnull final String relationship,
                                       @Nonnull final String targetuuid) throws ClientException {
        return disConnect(Usergrid.getInstance(), relationship, targetuuid);
    }

    @Nonnull
    public UsergridResponse disConnect(@Nonnull final UsergridClient client, @Nonnull final String relationship,
                                       @Nonnull final String targetuuid) throws ClientException {
        return client.disConnect(this.getType(), this.getName(), relationship, targetuuid);
    }

    @Nonnull
    public UsergridResponse disConnect(@Nonnull final String relationship,
                                       @Nonnull final String type, @Nonnull final String name) throws ClientException {
        return disConnect(Usergrid.getInstance(), relationship, type, name);
    }

    @Nonnull
    public UsergridResponse disConnect(@Nonnull final UsergridClient client, @Nonnull final String relationship,
                                       @Nonnull final String type, @Nonnull final String name) throws ClientException {
        return client.disConnect(this.getType(), this.getName(), relationship, type, name);
    }

    @Nonnull
    public UsergridResponse disConnect(@Nonnull final String relationship,
                                       @Nonnull final UsergridEntity target) throws ClientException {
        return disConnect(Usergrid.getInstance(), relationship, target);
    }

    @Nonnull
    public UsergridResponse disConnect(@Nonnull final UsergridClient client, @Nonnull final String relationship,
                                       @Nonnull final UsergridEntity target) throws ClientException {

        if (target.getUuid() != null) {
            return client.disConnect(
                    this,
                    relationship,
                    target);

        } else if (target.getType() != null && target.getName() != null) {
            return client.disConnect(
                    this,
                    relationship,
                    target);

        } else {
            throw new IllegalArgumentException("One of UUID or Type+Name is required for the target entity of the connection");
        }
    }

    @Nonnull
    public List<UsergridEntity> getConnections(@Nonnull final Direction direction, @Nonnull final String relationship) {
        UsergridClient client = Usergrid.getInstance();
        client.ValidateEntity(this);
        UsergridResponse resp = client.getConnections(direction, this, relationship);
        return resp.getEntities();
    }

    @Nonnull
    public UsergridEntity reLoad() {
        return reLoad(Usergrid.getInstance());
    }

    @Nonnull
    public UsergridEntity reLoad(@Nonnull UsergridClient client) {
        return client.GET(this.getType(), this.getName()).first();
    }


    /**
     * Will effectively delete a property when it is set to null.  The property will not be
     * removed from the entity on the server side until a PUT is made
     *
     * @param propertyName
     */
    public void removeEntityProperty(String propertyName) {
        putProperty(propertyName, "");
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
        putProperty(propertyName, aToAdd);

    }

    public void pop(String propertyName) {
        Object entityProperty = getEntityProperty(propertyName);
        if (entityProperty.getClass() == POJONode.class) {
            ArrayList<Object> newArrNode = (ArrayList) ((POJONode) entityProperty).getPojo();
            if (newArrNode.size() == 0)
                return;
            newArrNode.remove(newArrNode.size() - 1);
            putProperty(propertyName, newArrNode);
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
            putProperty(propertyName, newArrNode);
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
        if (getEntityProperty(FILEMETADATA) != null) {
            return ((this.getProperties().get(FILEMETADATA).findValue("content-length").intValue() > 0));
        }
        return false;
    }

    /**
     * Set the location of an entity
     *
     * @param latitude
     * @param longitude
     */
    public void setLocation(@Nonnull final double latitude,@Nonnull final double longitude) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode(); // will be of type ObjectNode
        rootNode.put("latitude", latitude);
        rootNode.put("longitude", longitude);

        setObjectProperty(properties, "location", rootNode);
    }


}