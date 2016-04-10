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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.UsergridEnums;
import org.apache.usergrid.java.client.UsergridRequest;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.model.UsergridUser;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.utils.JsonUtils;
import org.apache.usergrid.java.client.utils.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static org.apache.usergrid.java.client.utils.JsonUtils.toJsonString;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsergridResponse {

    @Nullable private UsergridClient client;
    @NotNull private Map<String, JsonNode> properties = new HashMap<>();

    private int statusCode = 0;
    @Nullable private JsonNode responseJson = null;
    @Nullable private String cursor;
    @Nullable private List<UsergridEntity> entities;
    @Nullable private Map<String, String> headers;
    @Nullable private UsergridQuery query;
    @Nullable private UsergridResponseError responseError = null;

    @Nullable private String accessToken;
    @Nullable private Long expires;

    public boolean ok() { return (statusCode > 0 && statusCode < 400); }
    public int count()  { return (entities == null) ? 0 : entities.size(); }
    public boolean hasNextPage() { return (cursor != null); }
    @Override public String toString() {
        return toJsonString(this);
    }

    @Nullable
    public UsergridEntity first() { return (entities == null || entities.isEmpty()) ? null : entities.get(0); }
    @Nullable
    public UsergridEntity entity() {
        return first();
    }
    @Nullable
    public UsergridEntity last() { return (entities == null || entities.isEmpty()) ? null : entities.get(entities.size() - 1); }

    @Nullable
    public UsergridUser user() {
        UsergridEntity entity = this.first();
        if( entity != null && entity instanceof UsergridUser ) {
            return (UsergridUser) entity;
        }
        return null;
    }

    @Nullable
    public List<UsergridUser> users() {
        ArrayList<UsergridUser> users = null;
        if( entities != null && !entities.isEmpty() ) {
            for( UsergridEntity entity : entities ) {
                if( entity instanceof UsergridUser ) {
                    if( users == null )  {
                        users = new ArrayList<>();
                    }
                    users.add((UsergridUser)entity);
                }
            }
        }
        return users;
    }

    public int getStatusCode() { return this.statusCode; }

    @Nullable @JsonIgnore
    public UsergridClient getClient() {
        return client;
    }
    @JsonIgnore public void setClient(@Nullable final UsergridClient client) { this.client = client; }

    @Nullable
    public JsonNode getResponseJson() {
        return responseJson;
    }
    private void setResponseJson(@Nullable final JsonNode responseJson) {this.responseJson = responseJson; }

    @Nullable
    public UsergridQuery getQuery() {
        return query;
    }
    private void setQuery(@Nullable final UsergridQuery query) { this.query = query; }

    @Nullable
    public UsergridResponseError getResponseError() {
        return responseError;
    }
    private void setResponseError(@Nullable final UsergridResponseError responseError) { this.responseError = responseError; }

    @Nullable
    public Map<String, String> getHeaders() {
        return headers;
    }
    private void setHeaders(@Nullable final Map<String, String> headers) { this.headers = headers; }

    @Nullable
    public List<UsergridEntity> getEntities() { return entities; }
    private void setEntities(@NotNull final List<UsergridEntity> entities) { this.entities = entities; }

    @Nullable @JsonProperty("cursor")
    public String getCursor() {
        return cursor;
    }
    @JsonProperty("cursor")
    private void setCursor(@NotNull final String cursor) { this.cursor = cursor; }

    @Nullable @JsonProperty("access_token")
    public String getAccessToken() { return this.accessToken; }
    @JsonProperty("access_token")
    private void setAccessToken(@NotNull final String accessToken) { this.accessToken = accessToken; }

    @Nullable @JsonProperty("expires_in")
    public Long getExpires() { return this.expires; }
    @JsonProperty("expires_in")
    private void setExpires(@NotNull final Long expires) { this.expires = expires; }

    @JsonProperty("user")
    private void setUser(@NotNull final UsergridUser user) {
        if( this.entities == null ) {
            this.entities = new ArrayList<>();
        }
        this.entities.add(user);
    }

    @NotNull @JsonAnyGetter
    public Map<String, JsonNode> getProperties() {
        return properties;
    }
    @JsonAnySetter
    private void setProperty(@NotNull final  String key, @NotNull final JsonNode value) {
        properties.put(key, value);
    }

    @NotNull
    public UsergridResponse loadNextPage() {
        UsergridClient client = this.client;
        UsergridEntity entity = this.first();
        if( this.hasNextPage() && client != null && entity != null ) {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("cursor", getCursor());
            UsergridRequest request = new UsergridRequest(UsergridEnums.UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE, this.client.clientAppUrl(), paramsMap, null, null, this.getQuery(), entity.getType());
            return client.sendRequest(request);
        } else {
            return UsergridResponse.fromError(client,"Error Loading Next Page.","Unable to load next page.");
        }
    }

    @NotNull
    public static UsergridResponse fromResponse(@Nullable final UsergridClient client, @NotNull final UsergridRequest request, @NotNull final Response requestResponse) {
        UsergridResponse response;
        try {
            String responseJsonString = requestResponse.readEntity(String.class);
            JsonNode responseJson = JsonUtils.mapper.readTree(responseJsonString);
            if ( responseJson.has("error") )  {
                response = new UsergridResponse();
                response.responseError = JsonUtils.fromJsonNode(responseJson,UsergridResponseError.class);
            } else {
                response = JsonUtils.fromJsonNode(responseJson,UsergridResponse.class);
            }
            response.responseJson = responseJson;
        } catch ( Exception e ) {
            response = UsergridResponse.fromException(client,e);
        }
        response.client = client;
        response.statusCode = requestResponse.getStatus();
        response.headers = MapUtils.putMultivaluedMap(requestResponse.getHeaders());
        response.query = request.getQuery();
        return response;
    }

    @NotNull
    public static UsergridResponse fromError(@Nullable final UsergridClient client, @NotNull final String errorName, @NotNull final String errorDescription) {
        UsergridResponse response = new UsergridResponse();
        response.client = client;
        response.responseError = new UsergridResponseError(errorName,errorDescription);
        return response;
    }

    @NotNull
    public static UsergridResponse fromException(@Nullable final UsergridClient client, @NotNull final Exception ex) {
        UsergridResponse response = new UsergridResponse();
        response.client = client;
        if (ex instanceof ClientErrorException)  {
            ClientErrorException clientError = (ClientErrorException) ex;
            response.statusCode = clientError.getResponse().getStatus();
            response.responseError = new UsergridResponseError(clientError.getResponse().getStatusInfo().toString(),
                    clientError.getResponse().toString(), clientError.getClass().toString());
        }  else  {
            response.responseError = new UsergridResponseError(ex.getClass().toString(), ex.getMessage(), ex.getCause().toString());
        }
        return response;
    }
}
