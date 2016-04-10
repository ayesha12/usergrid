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
import com.fasterxml.jackson.databind.node.*;
import org.apache.usergrid.java.client.UsergridEnums.*;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.apache.usergrid.java.client.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.apache.usergrid.java.client.utils.JsonUtils.*;

@SuppressWarnings("unused")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = UsergridEntity.class, visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = UsergridUser.class, name = UsergridUser.USER_ENTITY_TYPE)})
public class UsergridEntity {

    @NotNull private String type;
    @Nullable private String uuid;
    @Nullable private String name;
    @Nullable private Long created;
    @Nullable private Long modified;

    @NotNull protected Map<String, JsonNode> properties = new HashMap<>();

    public UsergridEntity(@JsonProperty("type") @NotNull final String type) {
        this.type = type;
    }

    public UsergridEntity(@NotNull final String type, @Nullable final String name) {
        this(type);
        if( name != null ) {
            this.name = name;
        }
    }

    public UsergridEntity(@NotNull final String type, @Nullable final String name, @NotNull final Map<String, JsonNode> properties) {
        this(type,name);
        this.properties = new HashMap<>(properties);
    }

    @NotNull @Override public String toString() {
        return toJsonString(this);
    }
    @NotNull public JsonNode toJsonObjectValue() {
        return toJsonNode(this);
    }

    @JsonIgnore
    public boolean isUser() { return (this instanceof UsergridUser || this.getType().equalsIgnoreCase(UsergridUser.USER_ENTITY_TYPE)); }

    @NotNull public String getType() { return this.type; }
    private void setType(@NotNull final String type) { this.type = type; }

    @Nullable public String getUuid() { return this.uuid; }
    private void setUuid(@NotNull final String uuid) { this.uuid = uuid; }

    @Nullable public String getName() { return this.name; }
    protected void setName(@Nullable final String name) { this.name = name; }

    @Nullable public Long getCreated() { return this.created; }
    private void setCreated(@NotNull final Long created) { this.created = created; }

    @Nullable public Long getModified() { return this.modified; }
    private void setModified(@NotNull final Long modified) { this.modified = modified; }

    public void setLocation(final double latitude, final double longitude) {
        ObjectNode rootNode = JsonUtils.createObjectNode();
        rootNode.put("latitude", latitude);
        rootNode.put("longitude", longitude);
        setObjectProperty(this.properties, "location", rootNode);
    }

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
        String uuidOrName = this.uuidOrName();
        if( uuidOrName == null ) {
            return UsergridResponse.fromError(client,  "No UUID or name found.", "The entity object must have a `uuid` or `name` assigned.");
        }
        UsergridResponse response = client.GET(this.getType(), uuidOrName);
        if( response.ok() ) {
            UsergridEntity responseEntity = response.first();
            if( responseEntity != null ) {
                this.properties = new HashMap<>(responseEntity.properties);
            }
        }
        return response;
    }

    @NotNull
    public UsergridResponse save() {
        return this.save(Usergrid.getInstance());
    }

    @NotNull
    public UsergridResponse save(@NotNull final UsergridClient client) {
        UsergridResponse response;
        if( this.getUuid() != null ) {
            response = client.PUT(this);
        } else {
            response = client.POST(this);
        }
        if( response.ok() ) {
            UsergridEntity responseEntity = response.first();
            if( responseEntity != null ) {
                this.properties = new HashMap<>(responseEntity.properties);
            }
        }
        return response;
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
    public UsergridResponse connect(@NotNull final String relationship, @NotNull final UsergridEntity toEntity) {
        return this.connect(Usergrid.getInstance(), relationship, toEntity);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final UsergridClient client, @NotNull final String relationship, @NotNull final UsergridEntity toEntity) {
        return client.connect(this,relationship,toEntity);
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

    public void removeProperty(@NotNull final String name) {
        putProperty(name, NullNode.getInstance());
    }

    public void removeProperties(@NotNull final List<String> names) {
        for( String propertyName : names ) {
            this.removeProperty(propertyName);
        }
    }

    public void putProperty(@NotNull final String name, @NotNull final String value) {
        this.putProperty(name, JsonNodeFactory.instance.textNode(value));
    }
    public void putProperty(@NotNull final String name, final boolean value) {
        this.putProperty(name, JsonNodeFactory.instance.booleanNode(value));
    }
    public void putProperty(@NotNull final String name, @NotNull final List value) {
        this.putProperty(name, JsonNodeFactory.instance.pojoNode(value));
    }
    public void putProperty(@NotNull final String name, final int value) {
        this.putProperty(name, JsonNodeFactory.instance.numberNode(value));
    }
    public void putProperty(@NotNull final String name, final long value) {
        this.putProperty(name, JsonNodeFactory.instance.numberNode(value));
    }
    public void putProperty(@NotNull final String name, final float value) {
        this.putProperty(name, JsonNodeFactory.instance.numberNode(value));
    }
    public void putProperty(@NotNull final String name, @Nullable final JsonNode value) {
        UsergridEntityProperties entityProperty = UsergridEntityProperties.fromString(name);
        if( entityProperty != null && !entityProperty.isMutableForEntity(this) ) {
            return;
        }
        this.internalPutProperty(name,value);
    }
    public void putProperties(@NotNull final String jsonString) {
        try {
            JsonNode jsonNode = JsonUtils.mapper.readTree(jsonString);
            this.putProperties(jsonNode);
        } catch( Exception ignore ) {}
    }
    public void putProperties(@NotNull final Map<String, Object> properties) {
        try {
            JsonNode jsonNode = JsonUtils.mapper.valueToTree(properties);
            this.putProperties(jsonNode);
        } catch( Exception ignore ) {}
    }
    public void putProperties(@NotNull final JsonNode jsonNode) {
        Iterator<Map.Entry<String,JsonNode>> keys = jsonNode.fields();
        while (keys.hasNext()) {
            Map.Entry<String,JsonNode> entry = keys.next();
            this.putProperty(entry.getKey(),entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    public void append(@NotNull final String name, @NotNull final Object value) {
        this.append(name, (value instanceof List) ? (List<Object>) value : Collections.singletonList(value));
    }

    public void append(@NotNull final String name, @NotNull final List<Object> value) {
        this.insert(name, value, Integer.MAX_VALUE);
    }

    @SuppressWarnings("unchecked")
    public void insert(@NotNull final String name, @NotNull final Object value) {
        this.insert(name, (value instanceof List) ? (List<Object>) value : Collections.singletonList(value), 0);
    }

    @SuppressWarnings("unchecked")
    public void insert(@NotNull final String name, @NotNull final Object value, final int index) {
        this.insert(name, (value instanceof List) ? (List<Object>) value : Collections.singletonList(value), index);
    }

    public void insert(@NotNull final String name, @NotNull final List<Object> value) {
        this.insert(name,value,0);
    }

    public void insert(@NotNull final String name, @NotNull final List<Object> value, final int index) {
        int indexToInsert = index;
        if (indexToInsert < 0) {
            indexToInsert = 0;
        }
        Object propertyValue = this.getEntityProperty(name);
        if( propertyValue != null ) {
            ArrayList<Object> propertyArrayValue = this.convertToList(propertyValue);
            propertyArrayValue = this.insertIntoArray(propertyArrayValue,value,indexToInsert);
            this.putProperty(name, propertyArrayValue);
        } else {
            this.putProperty(name, value);
        }
    }

    public void pop(@NotNull final String name) {
        ArrayList<Object> arrayToPop = this.getArrayToPopOrShift(name);
        if( arrayToPop != null && !arrayToPop.isEmpty() ) {
            arrayToPop.remove(arrayToPop.size() - 1);
            this.putProperty(name, arrayToPop);
        }
    }

    public void shift(@NotNull final String name) {
        ArrayList<Object> arrayToShift = this.getArrayToPopOrShift(name);
        if( arrayToShift != null && !arrayToShift.isEmpty() ) {
            arrayToShift.remove(0);
            this.putProperty(name, arrayToShift);
        }
    }

    @Nullable
    public <T> T getEntityProperty(@NotNull final String name) {
        return JsonUtils.getProperty(this.properties, name);
    }

    @Nullable
    public JsonNode getJsonNodeProperty(@NotNull final String name) {
        return this.getProperties().get(name);
    }

    @Nullable
    public String getStringProperty(@NotNull final String name) {
        return JsonUtils.getStringProperty(this.getProperties(), name);
    }

    @Nullable
    public Boolean getBooleanProperty(@NotNull final String name) {
        Boolean booleanValue = null;
        Object object = JsonUtils.getProperty(this.getProperties(), name);
        if( object instanceof Boolean ) {
            booleanValue = (Boolean)object;
        }
        return booleanValue;
    }

    @Nullable
    public Number getNumberProperty(@NotNull final String name) {
        Number numberValue = null;
        Object object = JsonUtils.getProperty(this.getProperties(), name);
        if( object instanceof Number ) {
            numberValue = (Number)object;
        }
        return numberValue;
    }

    @Nullable
    public Integer getIntegerProperty(@NotNull final String name) {
        Integer integerValue = null;
        Object object = JsonUtils.getProperty(this.getProperties(), name);
        if( object instanceof Number ) {
            integerValue = ((Number)object).intValue();
        }
        return integerValue;
    }

    @Nullable
    public Float getFloatProperty(@NotNull final String name) {
        Float floatValue = null;
        Object object = JsonUtils.getProperty(this.getProperties(), name);
        if( object instanceof Number ) {
            floatValue = ((Number)object).floatValue();
        }
        return floatValue;
    }

    @Nullable
    public Long getLongProperty(@NotNull final String name) {
        Long longValue = null;
        Object object = JsonUtils.getProperty(this.getProperties(), name);
        if( object instanceof Number ) {
            longValue = ((Number)object).longValue();
        }
        return longValue;
    }

    @JsonAnyGetter @NotNull
    private Map<String, JsonNode> getProperties() {
        return this.properties;
    }

    @JsonAnySetter
    private void internalPutProperty(@NotNull final String name, @Nullable final JsonNode value) {
        if (value == null) {
            properties.put(name, NullNode.instance);
        } else {
            properties.put(name, value);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private ArrayList<Object> getArrayToPopOrShift(@NotNull final String name) {
        Object entityProperty = getEntityProperty(name);
        ArrayList<Object> arrayToPopOrShift = null;
        if (entityProperty instanceof POJONode) {
            Object objectValue = ((POJONode) entityProperty).getPojo();
            if (objectValue instanceof List) {
                arrayToPopOrShift = new ArrayList<>((List) objectValue);
            } else {
                arrayToPopOrShift = new ArrayList<>();
                arrayToPopOrShift.add(objectValue);
            }
        } else if( entityProperty instanceof ArrayNode ) {
            arrayToPopOrShift = JsonUtils.convertToArrayList((ArrayNode)entityProperty);
        } else if( entityProperty instanceof List ) {
            arrayToPopOrShift = new ArrayList<>((List) entityProperty);
        }
        return arrayToPopOrShift;
    }

    @NotNull
    private ArrayList<Object> convertToList(@NotNull final Object value) {
        ArrayList<Object> arrayList = new ArrayList<>();
        if( value instanceof ArrayNode ) {
            arrayList = JsonUtils.convertToArrayList((ArrayNode)value);
        } else if (value instanceof POJONode) {
            Object objectValue = ((POJONode) value).getPojo();
            if( objectValue instanceof List ) {
                arrayList.addAll((List)objectValue);
            } else {
                arrayList.add(objectValue);
            }
        } else if (value instanceof List) {
            arrayList.addAll((List)value);
        } else {
            arrayList.add(value);
        }
        return arrayList;
    }

    @NotNull
    private ArrayList<Object> insertIntoArray(@NotNull final List<Object> propertyArrayNode, @NotNull final List<Object> arrayToInsert, final int index) {
        ArrayList<Object> mergedArray = new ArrayList<>();
        if (propertyArrayNode.size() <= 0 || arrayToInsert.isEmpty()) {
            mergedArray.addAll(arrayToInsert);
        }  else if ( index <= 0 ) {
            mergedArray.addAll(arrayToInsert);
            mergedArray.addAll(propertyArrayNode);
        } else if ( index > 0 ) {
            mergedArray.addAll(propertyArrayNode);
            if ( index > propertyArrayNode.size() ) {
                mergedArray.addAll(arrayToInsert);
            } else {
                mergedArray.addAll(index,arrayToInsert);
            }
        }
        return mergedArray;
    }
}