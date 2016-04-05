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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import org.apache.usergrid.java.client.UsergridEnums.UsergridDirection;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.exception.UsergridException;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.apache.usergrid.java.client.utils.JsonUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.apache.usergrid.java.client.utils.JsonUtils.*;
import static org.apache.usergrid.java.client.utils.MapUtils.newMapWithoutKeys;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = UsergridEntity.class, visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = UsergridUser.class, name = UsergridUser.ENTITY_TYPE)})
public class UsergridEntity {

    private static final String PROPERTY_UUID = "uuid";
    private static final String PROPERTY_TYPE = "type";
    private static final String PROPERTY_NAME = "name";
    private static final String FILE_METADATA = "file-metadata";

    Map<String, JsonNode> properties = new HashMap<>();

    public UsergridEntity() { }

    public UsergridEntity(@NotNull final String type) {
        setType(type);
    }

    @JsonProperty("type")
    @NotNull public String getType() { return getStringProperty(PROPERTY_TYPE); }
    @JsonProperty("type")
    public void setType(@NotNull final String type) { JsonUtils.setStringProperty(properties, PROPERTY_TYPE, type); }

    @Nullable public String getUuid() { return getStringProperty(PROPERTY_UUID); }
    public void setUuid(@NotNull final String uuid) { JsonUtils.setStringProperty(properties, PROPERTY_UUID, uuid); }

    @Nullable public String getName() { return getStringProperty(PROPERTY_NAME); }
    public void setName(@NotNull final String name) { JsonUtils.setStringProperty(properties, PROPERTY_NAME, name); }

    @JsonAnySetter
    public void putProperty(@NotNull final String name, @Nullable final JsonNode value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, value);
        }
    }

    public void putProperty(@NotNull final String name, @NotNull final String value) {
        setStringProperty(properties, name, value);
    }

    public void putProperty(@NotNull final String name, final boolean value) {
        setBooleanProperty(properties, name, value);
    }

    public void putProperty(@NotNull final String name, @NotNull final ArrayList value) {
        setArrayProperty(properties, name, value);
    }

    public void putProperty(@NotNull final String name, final long value) {
        setLongProperty(properties, name, value);
    }

    public void putProperty(@NotNull final String name, final float value) {
        setFloatProperty(properties, name, value);
    }

    public void putProperties(@NotNull final String jsonString) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonString);
        Iterator keys = jsonObj.keys();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object value = jsonObj.get(key);
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
    @NotNull
    public void putProperties(@NotNull Map<String, Object> jsonHash) throws JSONException {

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

    public void save() {
        save(Usergrid.getInstance());
    }

    public void save(@NotNull final UsergridClient client) {
        if (this.getUuid() != null || this.getUuid().equalsIgnoreCase(""))
            client.PUT(this);
        else
            client.POST(this);

    }

    public void remove() {
        remove(Usergrid.getInstance());
    }

    public void remove(@NotNull final UsergridClient client) {
        client.DELETE(this);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final String relationship,
                                    @NotNull final String type,
                                    @NotNull final String name) {
        return connect(Usergrid.getInstance(), relationship, type, name);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final UsergridClient client,
                                    @NotNull final String relationship,
                                    @NotNull final String type,
                                    @NotNull final String name) {
        return client.connect(this.getType(), this.getName(), relationship, type, name);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final String relationship,
                                    @NotNull final String targetUUId) throws UsergridException {
        return connect(Usergrid.getInstance(), relationship, targetUUId);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final UsergridClient client,
                                    @NotNull final String relationship,
                                    @NotNull final String targetUuid) throws UsergridException {
        return client.connect(this.getType(), this.getName(), relationship, targetUuid);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final String relationship,
                                    @NotNull final UsergridEntity target) throws UsergridException {
        return connect(Usergrid.getInstance(), relationship, target);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final UsergridClient client,
                                    @NotNull final String relationship,
                                    @NotNull final UsergridEntity target) throws UsergridException {

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
            throw new UsergridException("One of UUID or Type+Name is required for the target entity of the connection");
        }
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final String relationship,
                                       @NotNull final String targetuuid) throws UsergridException {
        return disconnect(Usergrid.getInstance(), relationship, targetuuid);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final UsergridClient client,
                                       @NotNull final String relationship,
                                       @NotNull final String targetuuid) throws UsergridException {
        return client.disconnect(this.getType(), this.getName(), relationship, targetuuid);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final String relationship,
                                       @NotNull final String type,
                                       @NotNull final String name) throws UsergridException {
        return disconnect(Usergrid.getInstance(), relationship, type, name);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final UsergridClient client,
                                       @NotNull final String relationship,
                                       @NotNull final String type,
                                       @NotNull final String name) throws UsergridException {
        return client.disconnect(this.getType(), this.getName(), relationship, type, name);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final String relationship,
                                       @NotNull final UsergridEntity target) throws UsergridException {
        return disconnect(Usergrid.getInstance(), relationship, target);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final UsergridClient client,
                                       @NotNull final String relationship,
                                       @NotNull final UsergridEntity target) throws UsergridException {

        if (target.getUuid() != null) {
            return client.disconnect(
                    this,
                    relationship,
                    target);

        } else if (target.getType() != null && target.getName() != null) {
            return client.disconnect(
                    this,
                    relationship,
                    target);

        } else {
            throw new UsergridException("One of UUID or Type+Name is required for the target entity of the connection");
        }
    }

    @NotNull
    public List<UsergridEntity> getConnections(@NotNull final UsergridDirection direction, @NotNull final String relationship) {
        UsergridClient client = Usergrid.getInstance();
        client.ValidateEntity(this);
        UsergridResponse resp = client.getConnections(direction, this, relationship, null);
        return resp.getEntities();
    }

    @NotNull
    public UsergridEntity reLoad() {
        return reLoad(Usergrid.getInstance());
    }

    @NotNull
    public UsergridEntity reLoad(@NotNull UsergridClient client) {
        return client.GET(this.getType(), this.getName()).first();
    }

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

    public boolean isUser() {
        if (JsonUtils.getStringProperty(properties, PROPERTY_TYPE).equalsIgnoreCase(UsergridUser.ENTITY_TYPE))
            return true;
        return false;
    }

    public boolean hasAsset() {
        if (getEntityProperty(FILE_METADATA) != null) {
            return ((this.getProperties().get(FILE_METADATA).findValue("content-length").intValue() > 0));
        }
        return false;
    }

    /**
     * Set the location of an entity
     *
     * @param latitude
     * @param longitude
     */
    public void setLocation(final double latitude,final double longitude) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode(); // will be of type ObjectNode
        rootNode.put("latitude", latitude);
        rootNode.put("longitude", longitude);

        setObjectProperty(properties, "location", rootNode);
    }

    @JsonIgnore
    public List<String> getPropertyNames() {
        List<String> properties = new ArrayList<>();
        properties.add(PROPERTY_TYPE);
        properties.add(PROPERTY_UUID);
        return properties;
    }

    @JsonAnyGetter
    public Map<String, JsonNode> getProperties() {
        return newMapWithoutKeys(properties, this.getPropertyNames());
    }

    public String getStringProperty(String name) {
        return JsonUtils.getStringProperty(this.properties, name);
    }

    public <T> T getEntityProperty(String name) {
        return JsonUtils.getProperty(this.properties, name);
    }

    @Nullable
    public static <T extends UsergridEntity> T toType(@NotNull final UsergridEntity usergridEntity,
                                                      @NotNull final Class<T> t) {

        T newEntity = null;
        if (usergridEntity.getClass().isAssignableFrom(t)) {
            try {
                newEntity = (t.newInstance());
                if (newEntity.getType().equals(usergridEntity.getType())) {
                    newEntity.properties = usergridEntity.properties;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newEntity;
    }

    @NotNull
    public static <T extends UsergridEntity> List<T> toType(@NotNull final List<UsergridEntity> entities,
                                                            @NotNull final Class<T> t) {
        List<T> l = new ArrayList<>(entities.size());
        for (UsergridEntity usergridEntity : entities) {
            T newEntity = usergridEntity.toType(t);
            if (newEntity != null) {
                l.add(newEntity);
            }
        }
        return l;
    }

    public <T extends UsergridEntity> T toType(Class<T> t) {
        return toType(this, t);
    }
}