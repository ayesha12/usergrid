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
package org.apache.usergrid.java.client;

import org.apache.usergrid.java.client.UsergridEnums.*;
import org.apache.usergrid.java.client.exception.UsergridException;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.model.UsergridUser;
import org.apache.usergrid.java.client.model.UsergridUserAuth;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;

@SuppressWarnings("unused")
public class UsergridClient {

    @NotNull public static String DEFAULT_BASE_URL = "https://api.usergrid.com";

    @NotNull private UsergridClientConfig config;
    @Nullable private UsergridUser currentUser = null;
    @Nullable private UsergridAuth tempAuth = null;

    @NotNull private final UsergridRequestManager requestManager;

    public UsergridClient(@NotNull final UsergridClientConfig config) {
        this.config = config;
        this.requestManager = new UsergridRequestManager(this);
    }

    public UsergridClient(@NotNull final String orgId, @NotNull final String appId) {
        this(new UsergridClientConfig(orgId, appId));
    }

    public UsergridClient(@NotNull final String orgId, @NotNull final String appId, @NotNull final String baseUrl) {
        this(new UsergridClientConfig(orgId, appId, baseUrl));
    }

    public UsergridClient(@NotNull final String orgId, @NotNull final String appId, @NotNull final String baseUrl, @NotNull final UsergridAuthMode authMode) {
        this(new UsergridClientConfig(orgId, appId, baseUrl, authMode));
    }

    @NotNull public UsergridClientConfig getConfig() { return this.config; }
    public void setConfig(@NotNull final UsergridClientConfig config) { this.config = config; }

    @NotNull public String getAppId() { return this.config.appId; }
    public void setAppId(@NotNull final String appId) { this.config.appId = appId; }

    @NotNull public String getOrgId() { return this.config.orgId; }
    public void setOrgId(@NotNull final String orgId) { this.config.orgId = orgId; }

    @NotNull public String getBaseUrl() { return this.config.baseUrl; }
    public void setBaseUrl(@NotNull final String baseUrl) { this.config.baseUrl = baseUrl; }

    @NotNull public String clientAppUrl() { return getBaseUrl() + "/" + getOrgId() + "/" + getAppId(); }

    @NotNull public UsergridAuthMode getAuthMode() { return this.config.authMode; }
    public void setAuthMode(@NotNull final UsergridAuthMode authMode) { this.config.authMode = authMode; }

    @Nullable public UsergridUser getCurrentUser() { return this.currentUser; }
    public void setCurrentUser(@Nullable final UsergridUser currentUser) { this.currentUser = currentUser; }

    @Nullable public UsergridUserAuth getUserAuth() { return (this.currentUser != null) ? this.currentUser.userAuth : null; }

    @Nullable public UsergridAppAuth getAppAuth() { return this.config.appAuth; }
    public void setAppAuth(@Nullable final UsergridAppAuth appAuth) { this.config.appAuth = appAuth; }

    @Nullable
    public UsergridAuth authForRequests() {
        UsergridAuth authForRequests = null;
        if (tempAuth != null) {
            if (tempAuth.isValidToken()) {
                authForRequests = tempAuth;
            }
            tempAuth = null;
        } else {
            switch (config.authMode) {
                case USER: {
                    if (this.currentUser != null && this.currentUser.userAuth != null && this.currentUser.userAuth.isValidToken()) {
                        authForRequests = this.currentUser.userAuth;
                    }
                    break;
                }
                case APP: {
                    if (this.config.appAuth != null && this.config.appAuth.isValidToken()) {
                        authForRequests = this.config.appAuth;
                    }
                    break;
                }
            }
        }
        return authForRequests;
    }

    @NotNull
    public UsergridClient usingAuth(UsergridAuth auth) {
        this.tempAuth = auth;
        return this;
    }

    @NotNull
    public UsergridClient usingToken(String accessToken) {
        this.tempAuth = new UsergridAuth(accessToken);
        return this;
    }

    @NotNull
    public UsergridResponse authenticateApp() {
        if( this.config.appAuth == null ) {
            throw new UsergridException("Invalid UsergridAppAuth. UsergridClient's appAuth is nil.");
        }
        return this.authenticateApp(this.config.appAuth);
    }

    @NotNull
    public UsergridResponse authenticateApp(UsergridAppAuth auth) {
        this.config.appAuth = auth;
        return this.requestManager.authenticateApp(auth);
    }

    @NotNull
    public UsergridResponse authenticateUser(@NotNull final UsergridUserAuth userAuth) {
        return this.authenticateUser(userAuth,true);
    }

    @NotNull
    public UsergridResponse authenticateUser(@NotNull final UsergridUserAuth userAuth, boolean setAsCurrentUser) {
        UsergridResponse response = this.requestManager.authenticateUser(userAuth);
        if( response.ok() && setAsCurrentUser ) {
            this.setCurrentUser(response.user());
        }
        return response;
    }

    @NotNull
    public UsergridResponse resetPassword(@NotNull final UsergridUser user, @NotNull final String oldPassword, @NotNull final String newPassword) {
        Map<String, Object> data = new HashMap<>();
        data.put("newpassword", newPassword);
        data.put("oldpassword", oldPassword);
        String[] pathSegments = { "users", user.usernameOrEmail(), "password"};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), null, data, pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse logoutCurrentUser() throws UsergridException {
        if( this.currentUser == null || this.currentUser.uuidOrUsername() == null ) {
            throw new UsergridException("UsergridClient's currentUser is not valid. UsergridClient's currentUser is null or has no uuid or username.");
        }
        return logoutUser(this.currentUser.uuidOrUsername(), this.currentUser.userAuth.getAccessToken());
    }

    @NotNull
    public UsergridResponse logoutUserAllTokens(@NotNull final String uuidOrUsername) {
        return logoutUser(uuidOrUsername, null);
    }

    @NotNull
    public UsergridResponse logoutUser(@NotNull final String uuidOrUsername, @Nullable final String token){
        String[] pathSegments = {"users", uuidOrUsername, ""};
        int len = pathSegments.length;
        Map<String, Object> param = new HashMap<>();
        if(token != null){
            pathSegments[len-1] = "revoketoken";
            param.put("token",token);
        }
        else{
            pathSegments[len-1] = "revoketokens";
        }
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.PUT, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), param, null, pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse sendRequest(@NotNull final UsergridRequest request) {
        return this.requestManager.performRequest(request);
    }

    @NotNull
    public UsergridResponse GET(@NotNull final String collection, @NotNull final String uuidOrName) {
        String[] pathSegments = {collection, uuidOrName};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse GET(@NotNull final String type) {
        String[] pathSegments = {type};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse GET(@NotNull final UsergridQuery query) {
        String[] pathSegments = {query.getCollectionName()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), query, pathSegments);
        return this.sendRequest(request);
    }

    @NotNull //TODO: REALLY TEST THIS
    public UsergridResponse PUT(@NotNull final String type, @NotNull final String uuidOrName, @NotNull final Map<String, Object> jsonBody) {
        String[] pathSegments = { type, uuidOrName };
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.PUT, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), null, jsonBody, pathSegments);
        return this.sendRequest(request);
    }

    @NotNull //TODO: REALLY TEST THIS
    public UsergridResponse PUT(@NotNull final String type, @NotNull final Map<String, Object> jsonBody) {
        String uuidOrName = null;
        Object uuid = jsonBody.get(UsergridEntityProperties.UUID.toString());
        if( uuid != null ) {
            uuidOrName = uuid.toString();
        } else {
            Object name = jsonBody.get(UsergridEntityProperties.NAME.toString());
            if( name != null ) {
                uuidOrName = name.toString();
            }
        }
        if( uuidOrName == null ) {
            throw new UsergridException("jsonBody not valid. The `jsonBody` must contain a valid value for either `uuid` or `name`.");
        }
        String[] pathSegments = { type, uuidOrName };
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.PUT, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), null, jsonBody, pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse PUT(@NotNull final UsergridEntity entity) {
        ValidateEntity(entity);
        String[] pathSegments = { entity.getType(), entity.uuidOrName() };
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.PUT, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), null, entity, pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse PUT(@NotNull final UsergridQuery query, @NotNull final Map<String, Object> jsonBody) { //TODO: Fix this method
        String[] pathSegments = {query.getCollectionName()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.PUT, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), query, pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse POST(final @NotNull UsergridEntity entity) {
        String[] pathSegments = {entity.getType()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), null, entity, pathSegments);
        return this.sendRequest(request);
    }

    @NotNull //TODO: REALLY TEST THIS
    public UsergridResponse POST(@NotNull final List<UsergridEntity> entities) {
        if( entities.isEmpty() ) {
            throw new UsergridException("entities array is empty.");
        }
        String[] pathSegments = {entities.get(0).getType()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), null, entities, pathSegments);
        return this.sendRequest(request);
    }

    @NotNull //TODO: REALLY TEST THIS
    public UsergridResponse POST(@NotNull final String type, @NotNull final String uuidOrName, @NotNull final Map<String, Object> jsonBody) {
        String[] pathSegments = {type, uuidOrName};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), null, jsonBody, pathSegments);
        return this.sendRequest(request);
    }

    @NotNull //TODO: REALLY TEST THIS
    public UsergridResponse POST(@NotNull final String type, @NotNull final Map<String, Object> jsonBody) {
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), null, jsonBody, type);
        return this.sendRequest(request);
    }

    @NotNull //TODO: REALLY TEST THIS
    public UsergridResponse POST(@NotNull final String type, @NotNull final List<Map<String, Object>> jsonBodies) {
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), null, jsonBodies, type);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse DELETE(@NotNull final UsergridEntity entity) {
        ValidateEntity(entity);
        String[] pathSegments = {entity.getType(), entity.uuidOrName()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse DELETE(@NotNull final String type, @NotNull final String uuidOrName) {
        String[] pathSegments = {type, uuidOrName};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse DELETE(@NotNull final UsergridQuery query) {
        String[] pathSegments = {query.getCollectionName()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), query, pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final UsergridEntity entity, @NotNull final String relationship, @NotNull final UsergridEntity to) {
        ValidateEntity(entity);
        ValidateEntity(to);
        return this.connect(entity.getType(), entity.uuidOrName(), relationship, to.getType(), to.uuidOrName());
    }

    @NotNull
    public UsergridResponse connect(@NotNull final String entityType, @NotNull final String entityId, @NotNull final String relationship, @NotNull final String toType, @NotNull final String toName) {
        String[] pathSegments = {entityType, entityId, relationship, toType, toName};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final String entityType, @NotNull final String entityId, @NotNull final String relationship, @NotNull final String toId) {
        String[] pathSegments = { entityType, entityId, relationship, toId};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final String entityType, @NotNull final String entityId, @NotNull final String relationship, @NotNull final String fromUuid) {
        String[] pathSegments = {entityType, entityId, relationship, fromUuid};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), pathSegments);
        return this.sendRequest(request);
    }


    @NotNull
    public UsergridResponse disconnect(@NotNull final String entityType, @NotNull final String entityId, @NotNull final String relationship, @NotNull final String fromType, @NotNull final String fromName) {
        String[] pathSegments = {entityType, entityId, relationship, fromType, fromName};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final UsergridEntity entity, @NotNull final String relationship, @NotNull final UsergridEntity from) {
        ValidateEntity(entity);
        ValidateEntity(from);
        return this.disconnect(entity.getType(), entity.uuidOrName(), relationship, from.getType(), from.uuidOrName());
    }

    @NotNull
    public UsergridResponse getConnections(@NotNull final UsergridDirection direction, @NotNull final UsergridEntity entity, @NotNull final String relationship) {
        return this.getConnections(direction,entity.getType(),entity.uuidOrName(),relationship,null);
    }

    @NotNull
    public UsergridResponse getConnections(@NotNull final UsergridDirection direction, @NotNull final UsergridEntity entity, @NotNull final String relationship, @Nullable final UsergridQuery query) {
        ValidateEntity(entity);
        return this.getConnections(direction,entity.getType(),entity.uuidOrName(),relationship,query);
    }

    @NotNull
    public UsergridResponse getConnections(@NotNull final UsergridDirection direction, @NotNull final String type, @NotNull final String uuidOrName, @NotNull final String relationship, @Nullable final UsergridQuery query) {
        String[] pathSegments = {type, uuidOrName, direction.connectionValue(), relationship};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), query, pathSegments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse getConnections(@NotNull final UsergridDirection direction, @NotNull final String uuid, @NotNull final String relationship, @Nullable final UsergridQuery query) {
        String[] pathSegments = {uuid, direction.connectionValue(), relationship};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE, this.clientAppUrl(), query, pathSegments);
        return this.sendRequest(request);
    }

    private static void ValidateEntity(@NotNull final UsergridEntity entity) throws UsergridException {
        if (isEmpty(entity.getType())) {
            throw new UsergridException("UsergridEntity is required to have a 'type' property.");
        }
        if (isEmpty(entity.getName()) && isEmpty(entity.getUuid())) {
            throw new UsergridException("UsergridEntity is required to have a 'name' or 'uuid' property.");
        }
    }
}
