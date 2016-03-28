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
package org.apache.usergrid.java.client.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.UsergridEnums;
import org.apache.usergrid.java.client.UsergridRequest;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.model.UsergridUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.usergrid.java.client.utils.JsonUtils.toJsonString;

public class UsergridResponse {

    private static final Logger log = LoggerFactory.getLogger(UsergridEntity.class);
    private final Map<String, JsonNode> properties = new HashMap<String, JsonNode>();
    public UsergridResponseError responseError = null;
    private String accessToken;
    private String path;
    public String uri;
    public String status;
    public long timestamp;
    private List<UsergridEntity> entities;
    private UUID next;
    private String cursor;
    private String action;
    private List<Object> list;
    private Object data;
    private Map<String, UUID> applications;
    private Map<String, JsonNode> metadata;
    private Map<String, List<String>> params;
    public UUID last;
    public UsergridUser user;
    public int statuscode;
    public Map<String, JsonNode> header;

    public static UsergridResponse fromException(Exception ex) {
        UsergridResponse response = new UsergridResponse();
        if (ex instanceof ClientErrorException) {
            ClientErrorException clientError = (ClientErrorException) ex;
            response.responseError = new UsergridResponseError(clientError.getResponse().getStatusInfo().toString(), clientError.getResponse().getStatus(),
                    clientError.getResponse().toString(), clientError.getClass().toString());
        } else
            response.responseError = new UsergridResponseError(ex.getClass().toString(), 0, ex.getMessage(), ex.getCause().toString());
        return response;
    }

    @JsonAnyGetter
    @JsonSerialize(include = Inclusion.NON_NULL)
    public Map<String, JsonNode> getProperties() {
        return properties;
    }

    @JsonSerialize(include = Inclusion.NON_NULL)
    @JsonAnySetter
    public void setProperty(@Nonnull final  String key, @Nonnull final JsonNode value) {
        properties.put(key, value);
    }

    @JsonProperty("access_token")
    @JsonSerialize(include = Inclusion.NON_NULL)
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty("access_token")
    public void setAccessToken(@Nonnull final String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonSerialize(include = Inclusion.NON_NULL)
    public String getUri() {
        return uri;
    }

    public void setUri(@Nonnull final String uri) {
        this.uri = uri;
    }

    @JsonSerialize(include = Inclusion.NON_NULL)
    public String getStatus() {
        return status;
    }

    public void setStatus(@Nonnull final String status) {
        this.status = status;
    }

    // TODO : this can be null. @Nullable
    @JsonSerialize(include = Inclusion.NON_NULL)
    public List<UsergridEntity> getEntities() {
        return entities;
    }

    public void setEntities(@Nonnull final List<UsergridEntity> entities) {
        this.entities = entities;
    }

    @JsonSerialize(include = Inclusion.NON_NULL)
    public int getEntityCount() {
        if (entities == null) {
            return 0;
        }
        return entities.size();
    }

    public <T extends UsergridEntity> List<T> getEntities(Class<T> t) {
        return UsergridEntity.toType(entities, t);
    }

    @JsonSerialize(include = Inclusion.NON_NULL)
    public UUID getNext() {
        return next;
    }

    public void setNext(@Nonnull final UUID next) {
        this.next = next;
    }

    //TODO : can be null
    @JsonSerialize(include = Inclusion.NON_NULL)
    public String getCursor() {
        return cursor;
    }

    public void setCursor(@Nonnull final String cursor) {
        this.cursor = cursor;
    }

    //TODO : can be null
    @JsonSerialize(include = Inclusion.NON_NULL)
    public List<Object> getList() {
        return list;
    }

    public void setList(@Nonnull final List<Object> list) {
        this.list = list;
    }

    //todo : can be null ?
    @JsonSerialize(include = Inclusion.NON_NULL)
    public Object getData() {
        return data;
    }

    public void setData(@Nonnull final Object data) {
        this.data = data;
    }

    @JsonSerialize(include = Inclusion.NON_NULL)
    public Map<String, JsonNode> getMetadata() {
        return metadata;
    }

    public void setMetadata(@Nonnull final Map<String, JsonNode> metadata) {
        this.metadata = metadata;
    }

    @Nullable
    public Map<String, JsonNode> getHeaders() {
        return this.header;
    }

    public void setHeaders(@Nonnull final Map<String, JsonNode> headers) {
        this.header = headers;
    }

    @JsonSerialize(include = Inclusion.NON_NULL)
    public Map<String, List<String>> getParams() {
        return params;
    }

    public void setParams(@Nonnull final Map<String, List<String>> params) {
        this.params = params;
    }

    //TODO: @Nullable ?
    public UsergridUser currentUser() {
        return user;
    }

    public void setUser(@Nonnull final UsergridUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return toJsonString(this);
    }

    /**
     * get the first entity in the 'entities' array in the response
     *
     * @return A UsergridEntity if the entities array has elements, null otherwise
     */
    @Nullable
    public UsergridEntity first() {
        if (getEntities() != null && getEntities().size() > 0) {
            return getEntities().get(0);
        }

        return null;
    }


    /**
     * .entity is an alias for .first
     *
     * @return
     */

    @Nullable
    public UsergridEntity entity() {
        return first();
    }

    /**
     * get the last entity in the 'entities' array in the response
     *
     * @return A UsergridEntity if the entities array has elements, null otherwise
     */
    @Nullable
    public UsergridEntity last() {
        if (getEntities() != null && getEntities().size() > 0) {
            return getEntities().get(getEntities().size() - 1);
        }

        return null;
    }

    public boolean hasNextPage() {
        if (getCursor() != null)
            return true;
        return false;
    }

    public List<UsergridEntity> loadNextpage() {
        if (hasNextPage()) {
            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("cursor", getCursor());
            UsergridClient client = Usergrid.getInstance();

            String[] segments = {client.getOrgId(), client.getAppId(), this.first().getType()};

            UsergridRequest request = new UsergridRequest(UsergridEnums.UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                    client.config.baseUrl, paramsMap, null, segments);
            UsergridResponse resp = client.requestManager.performRequest(request); //client.apiRequest("GET",paramsMap,null,client.getOrgId(),client.getAppId(),this.first().getType());
            return resp.entities;
        }
        log.info("there are no more enetities to load. Cursor is empty.");
        return null;
    }

    @JsonSerialize(include = Inclusion.NON_NULL)
    public int getStatusIntCode() {
        return this.statuscode;
    }

    public void setStatusIntCode(int status) {
        this.statuscode = status;
    }

    @JsonSerialize(include = Inclusion.NON_NULL)
    public boolean ok() {
        if (this.statuscode < 400)
            return true;
        return false;
    }
}
