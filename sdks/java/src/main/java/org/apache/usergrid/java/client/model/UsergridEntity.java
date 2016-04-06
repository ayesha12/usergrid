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
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import org.apache.usergrid.java.client.UsergridEnums.*;
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
@JsonSubTypes({@JsonSubTypes.Type(value = UsergridUser.class, name = UsergridUser.USER_ENTITY_TYPE)})
public class UsergridEntity {

    Map<String, JsonNode> properties = new HashMap<>();

    public UsergridEntity() { }

    public UsergridEntity(@NotNull final String type) {
        setType(type);
    }

    @Override public String toString() {
        return toJsonString(this);
    }
    public JsonNode toJsonObjectValue() {
        return toJsonNode(this);
    }

    public boolean isUser() { return (this instanceof UsergridUser || this.getType().equalsIgnoreCase(UsergridUser.USER_ENTITY_TYPE)); }

    //TODO: Do these SETTERS need to be setting with JSON Utils
    @NotNull public String getType() { return getStringProperty(UsergridEntityProperties.TYPE.toString()); }
    public void setType(@NotNull final String type) { JsonUtils.setStringProperty(properties, UsergridEntityProperties.TYPE.toString(), type); }

    @Nullable public String getUuid() { return getStringProperty(UsergridEntityProperties.UUID.toString()); }
    public void setUuid(@NotNull final String uuid) { JsonUtils.setStringProperty(properties, UsergridEntityProperties.UUID.toString(), uuid); }

    @Nullable public String getName() { return getStringProperty(UsergridEntityProperties.NAME.toString()); }
    public void setName(@NotNull final String name) { JsonUtils.setStringProperty(properties, UsergridEntityProperties.NAME.toString(), name); }

    @Nullable
    public String uuidOrName() {
        String uuidOrName = this.getUuid();
        if( uuidOrName == null ) {
            uuidOrName = this.getName();
        }
        return uuidOrName;
    }

    @NotNull
    public UsergridResponse reload() {
        return this.reload(Usergrid.getInstance());
    }

    @NotNull
    public UsergridResponse reload(@NotNull final UsergridClient client) {
        return client.GET(this.getType(), this.uuidOrName());
    }

    @NotNull
    public UsergridResponse save() {
        return this.save(Usergrid.getInstance());
    }

    @NotNull
    public UsergridResponse save(@NotNull final UsergridClient client) {
        if( this.getUuid() != null ) {
            return client.PUT(this);
        } else {
            return client.POST(this);
        }
    }

    @NotNull
    public UsergridResponse remove() {
        return this.remove(Usergrid.getInstance());
    }

    @NotNull
    public UsergridResponse remove(@NotNull final UsergridClient client) {
        return client.DELETE(this);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final String relationship, @NotNull final String type, @NotNull final String name) {
        return this.connect(Usergrid.getInstance(), relationship, type, name);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final UsergridClient client, @NotNull final String relationship, @NotNull final String type, @NotNull final String name) {
        return client.connect(this.getType(), this.uuidOrName(), relationship, type, name);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final String relationship, @NotNull final String toUuid) {
        return this.connect(Usergrid.getInstance(), relationship, toUuid);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final UsergridClient client, @NotNull final String relationship, @NotNull final String toUuid) {
        return client.connect(this.getType(),this.uuidOrName(),relationship,toUuid);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final String relationship, @NotNull final UsergridEntity toEntity) {
        return this.connect(Usergrid.getInstance(), relationship, toEntity);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final UsergridClient client, @NotNull final String relationship, @NotNull final UsergridEntity toEntity) {
        return client.connect(this,relationship,toEntity);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final String relationship, @NotNull final String fromUuid) throws UsergridException {
        return this.disconnect(Usergrid.getInstance(), relationship, fromUuid);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final UsergridClient client, @NotNull final String relationship, @NotNull final String fromUuid) {
        return client.disconnect(this.getType(), this.uuidOrName(), relationship, fromUuid);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final String relationship, @NotNull final String type, @NotNull final String fromName) throws UsergridException {
        return this.disconnect(Usergrid.getInstance(), relationship, type, fromName);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final UsergridClient client, @NotNull final String relationship, @NotNull final String type, @NotNull final String fromName) {
        return client.disconnect(this.getType(), this.uuidOrName(), relationship, type, fromName);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final String relationship, @NotNull final UsergridEntity fromEntity) {
        return this.disconnect(Usergrid.getInstance(), relationship, fromEntity);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final UsergridClient client, @NotNull final String relationship, @NotNull final UsergridEntity fromEntity) {
        return client.disconnect(this,relationship,fromEntity);
    }

    @NotNull
    public UsergridResponse getConnections(@NotNull final UsergridDirection direction, @NotNull final String relationship) {
        return this.getConnections(Usergrid.getInstance(),direction,relationship);
    }

    @NotNull
    public UsergridResponse getConnections(@NotNull final UsergridClient client, @NotNull final UsergridDirection direction, @NotNull final String relationship) {
        return client.getConnections(direction,this,relationship);
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
    @JsonAnySetter
    public void putProperty(@NotNull final String name, @Nullable final JsonNode value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, value);
        }
    }
    public void putProperties(@NotNull final String jsonString) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonString);
        Iterator keys = jsonObj.keys();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object value = jsonObj.get(key);
            if (value instanceof String) {
                putProperty(key, value.toString());
            } else if (value instanceof Boolean) {
                putProperty(key, (Boolean) value);
            } else if (value instanceof Integer) {
                putProperty(key, (Integer) value);
            } else if (value instanceof Long) {
                putProperty(key, (Long) value);
            } else if (value instanceof Float) {
                putProperty(key, (Float) value);
            } else if (value instanceof JsonNode) {
                putProperty(key, (JsonNode) value);
            }
        }
    }
    public void putProperties(@NotNull Map<String, Object> properties) throws JSONException {
        for (String key : properties.keySet()) {
            Object value = properties.get(key);
            if (value instanceof String) {
                putProperty(key, value.toString());
            } else if (value instanceof Boolean) {
                putProperty(key, (Boolean) value);
            } else if (value instanceof Integer) {
                putProperty(key, (Integer) value);
            } else if (value instanceof Long) {
                putProperty(key, (Long) value);
            } else if (value instanceof Float) {
                putProperty(key, (Float) value);
            } else if (value instanceof JsonNode) {
                putProperty(key, (JsonNode) value);
            }
        }
    }

    public void removeProperty(String propertyName) {
        putProperty(propertyName, NullNode.getInstance());
    }

    public void removeProperties(String[] propertyList) {
        for( String propertyName : propertyList ) {
            this.removeProperty(propertyName);
        }
    }
    public void prepend(String propertyName, Object arrToInsert) {
        ArrayList<Object> initialArr = getArrayNode(getEntityProperty(propertyName));
        this.insert(propertyName, arrToInsert, 0);
    }



    public void append(String propertyName, ArrayList arrToInsert) {
        ArrayList<Object> initialArr = getArrayNode(getEntityProperty(propertyName));
        this.insert(propertyName, arrToInsert, Integer.MAX_VALUE);
    }

    public void insert(String propertyName, Object arrToInsert, int indx) {
        if (indx < 0) {
            indx = 0;
        }
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

    public void setLocation(final double latitude,final double longitude) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode(); // will be of type ObjectNode
        rootNode.put("latitude", latitude);
        rootNode.put("longitude", longitude);

        setObjectProperty(properties, "location", rootNode);
    }

    @JsonAnyGetter
    public Map<String, JsonNode> getProperties() {
        return this.properties;
    }

    public String getStringProperty(String name) {
        return JsonUtils.getStringProperty(this.properties, name);
    }

    public <T> T getEntityProperty(String name) {
        return JsonUtils.getProperty(this.properties, name);
    }
}