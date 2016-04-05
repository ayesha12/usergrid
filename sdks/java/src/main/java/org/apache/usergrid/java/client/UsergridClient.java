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

import org.apache.usergrid.java.client.UsergridEnums.UsergridDirection;
import org.apache.usergrid.java.client.UsergridEnums.UsergridAuthMode;
import org.apache.usergrid.java.client.UsergridEnums.UsergridHttpMethod;
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

    public static String DEFAULT_BASE_URL = "https://api.usergrid.com";

    private UsergridClientConfig config;
    private UsergridRequestManager requestManager;
    private UsergridUser currentUser = null;
    private UsergridAuth tempAuth = null;

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

    @Nullable public UsergridAppAuth getAppAuth() { return this.config.appAuth; }
    public void setAppAuth(@Nullable final UsergridAppAuth appAuth) { this.config.appAuth = appAuth; }

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
    public UsergridResponse authenticateUser(@NotNull final UsergridUserAuth userAuth) {
        return this.authenticateUser(userAuth,true);
    }

    @NotNull
    public UsergridResponse authenticateUser(@NotNull final UsergridUserAuth userAuth, boolean setAsCurrentUser) {
        UsergridResponse response = requestManager.authenticateUser(userAuth);
        if( setAsCurrentUser ) {
            this.setCurrentUser(response.currentUser());
        }
        return response;
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
    public UsergridClient usingAuth(UsergridAuth ugAuth) {
        this.tempAuth = ugAuth;
        return this;
    }

    @NotNull
    public UsergridClient usingToken(String accessToken) {
        this.tempAuth = new UsergridAuth(accessToken);
        return this;
    }

    @NotNull
    public UsergridResponse resetPassword(@NotNull final UsergridUser user, @NotNull final String oldPassword, @NotNull final String newPassword) {
        Map<String, Object> data = new HashMap<>();
        data.put("newpassword", newPassword);
        data.put("oldpassword", oldPassword);
        String[] segments = { getOrgId(), getAppId(), "users", user.getUsernameOrEmail(), "password"};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE, getBaseUrl(), null, data, segments);
        return requestManager.performRequest(request);
    }

    @NotNull
    public UsergridResponse logoutCurrentUser() {
        return logoutUser(this.currentUser.getUsername(), this.currentUser.userAuth.getAccessToken());
    }

    @NotNull
    public UsergridResponse logoutUserAllTokens(@NotNull final String uuidOrUsername) {
        return logoutUser(uuidOrUsername, null);
    }

    @NotNull
    public UsergridResponse logoutUser(@NotNull final String uuidOrUsername, @Nullable final String token){
        String[] segments = {config.orgId, config.appId, "users", uuidOrUsername, ""};
        int len = segments.length;
        Map<String, Object> param = new HashMap<>();
        if(token != null){
            segments[len-1] = "revoketoken";
            param.put("token",token);
        }
        else{
            segments[len-1] = "revoketokens";
        }
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.PUT, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, param, null, segments);
        return requestManager.performRequest(request);
    }

    @NotNull
    public UsergridResponse sendRequest(@NotNull final UsergridRequest request) {
        return this.requestManager.performRequest(request);
    }

    private void validateNonEmptyParam(final Object param,
                                       final String paramName) {
        if (isEmpty(param)) {
            throw new IllegalArgumentException(paramName + " cannot be null or empty");
        }
    }

    @NotNull //FIXME: THIS IS BROKEN
    public UsergridResponse getConnections(@NotNull final UsergridDirection direction,
                                           @NotNull final UsergridEntity entity,
                                           @NotNull final String relationship) {
        return getConnections(direction,entity.getType(),entity.getName(),relationship,null);
    }

    @NotNull //FIXME: THIS IS BROKEN
    public UsergridResponse getConnections(@NotNull final UsergridDirection direction,
                                           @NotNull final UsergridEntity entity,
                                           @NotNull final String relationship,
                                           @Nullable final UsergridQuery query) {
        ValidateEntity(entity);
        return getConnections(direction,entity.getType(),entity.getName(),relationship,query);
    }

    @NotNull //FIXME: THIS IS BROKEN
    public UsergridResponse getConnections(@NotNull final UsergridDirection direction,
                                           @NotNull final String type,
                                           @NotNull final String uuidOrName,
                                           @NotNull final String relationship,
                                           @Nullable final UsergridQuery query) {
        String[] segments1 = {config.orgId, config.appId,
                type, uuidOrName, direction.connectionValue(), relationship};

        UsergridRequest request1 = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, query, segments1);

        String[] segments2 = {config.orgId, config.appId,
                type, uuidOrName, direction.connectionValue(), relationship};

        UsergridRequest request2 = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, query, segments2);

        switch (direction) {
            case OUT:
                return requestManager.performRequest(request1);
            case IN:
                return requestManager.performRequest(request2);

        }
        return null; // invalid connection getName.
    }

    @NotNull //FIXME: THIS IS BROKEN
    public UsergridResponse getConnections(@NotNull final UsergridDirection direction,
                                           @NotNull final String uuid,
                                           @NotNull final String relationship,
                                           @Nullable final UsergridQuery query) {
        //TODO : check valid uuid.

        String[] segments1 = {config.orgId, config.appId,
                uuid, direction.connectionValue(), relationship};

        UsergridRequest request1 = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, query, segments1);

        String[] segments2 = {config.orgId, config.appId,
                uuid, direction.connectionValue(), relationship};

        UsergridRequest request2 = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, query, segments2);
        switch (direction) {
            case OUT:
                return requestManager.performRequest(request1);
            case IN:
                return requestManager.performRequest(request2);

        }
        return null; // invalid connection getName.
    }

    @NotNull
    public UsergridResponse connect(@NotNull final UsergridEntity entity,
                                    @NotNull final String relationship,
                                    @NotNull final UsergridEntity to) {
        ValidateEntity(entity);
        ValidateEntity(to);
        return this.connect(entity.getType(), entity.getUuid(), relationship, to.getType(), to.getUuid());
    }

    @NotNull
    public UsergridResponse connect(@NotNull final String entityType,
                                    @NotNull final String entityId,
                                    @NotNull final String relationship,
                                    @NotNull final String toType,
                                    @NotNull final String toName) {
        String[] segments = {config.orgId, config.appId, entityType, entityId,
                relationship, toType, toName};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }

    @NotNull
    public UsergridResponse connect(@NotNull final String entityType,
                                    @NotNull final String entityId,
                                    @NotNull final String relationship,
                                    @NotNull final String toId) {
        String[] segments = {config.orgId, config.appId, entityType,
                entityId, relationship, toId};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse disconnect(@NotNull final String entityType,
                                       @NotNull final String entityId,
                                       @NotNull final String relationship,
                                       @NotNull final String fromUuid) {
        String[] segments = {config.orgId, config.appId, entityType, entityId, relationship, fromUuid};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return this.sendRequest(request);
    }


    @NotNull
    public UsergridResponse disconnect(@NotNull final String entityType,
                                       @NotNull final String entityId,
                                       @NotNull final String relationship,
                                       @NotNull final String fromType,
                                       @NotNull final String fromName) {
        String[] segments = {config.orgId, config.appId, entityType,
                entityId, relationship, fromType, fromName};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return this.sendRequest(request);
    }

    /**
     * @param sourceVertex
     * @param targetVertex
     * @param connetionName
     * @return
     */
    public UsergridResponse disconnect(final UsergridEntity sourceVertex,
                                       final String connetionName,
                                       final UsergridEntity targetVertex) {
        ValidateEntity(sourceVertex);
        ValidateEntity(targetVertex);
        return this.disconnect(sourceVertex.getType(), sourceVertex.getUuid().toString(), connetionName, targetVertex.getUuid().toString());
    }


    /**
     * GET an entity using the collection getName and (getName|uuid)
     *
     * @param collection the getName of the collection
     * @param uuidOrName   the entity ID
     * @return UsergridResponse
     */
    public UsergridResponse GET(@NotNull final String collection, @NotNull final String uuidOrName) {

        String[] segments = {config.orgId, config.appId, collection, uuidOrName};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }

    /**
     * GET an entity using the Type
     *
     * @param type
     * @return UsergridResponse
     */

    public UsergridResponse GET(@NotNull final String type) {
        String[] segments = {config.orgId, config.appId, type};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return sendRequest(request);
    }


    /**
     * GET by Query
     *
     * @param query the UsergridQuery object
     * @return QueryResult
     */
    public UsergridResponse GET(final UsergridQuery query) {

        String[] segments = {config.orgId, config.appId, query.getCollectionName()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.GET,
                MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl,
                null,
                null,
                segments);
        request.query = query;
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse PUT(@NotNull final String type, @NotNull final String uuidOrName, @NotNull final Map<String, Object> jsonBody) {
        return new UsergridResponse(); //TODO: Write this method.
    }

    @NotNull
    public UsergridResponse PUT(@NotNull final String type, @NotNull final Map<String, Object> jsonBody) {
        return new UsergridResponse(); //TODO: Write this method.
    }

    @NotNull
    public UsergridResponse PUT(final UsergridEntity e) {
        ValidateEntity(e);
        String[] segments = {config.orgId, config.appId, e.getType(),
                e.getUuid() != null ? e.getUuid() : e.getName()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.PUT, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, e.getProperties(), segments);
        return this.sendRequest(request);
    }

    @NotNull
    public UsergridResponse PUT(final UsergridQuery q, Map<String, Object> jsonBody) { //TODO: Fix this method
        String[] segments = {config.orgId, config.appId, q.getCollectionName()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.PUT, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        request.query = q;

        return requestManager.performRequest(request);
    }

    @NotNull
    public UsergridResponse POST(final @NotNull UsergridEntity e) {
        String[] segments = {config.orgId, config.appId, e.getType()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, e, segments);
        return requestManager.performRequest(request);
    }

    @NotNull //FIXME: THIS IS BROKEN
    public UsergridResponse POST(@NotNull final List<UsergridEntity> entities) {
        return new UsergridResponse();
    }

    @NotNull //FIXME: THIS IS BROKEN
    public UsergridResponse POST(@NotNull final String type, @NotNull final String uuidOrName, @NotNull final Map<String, Object> jsonBody) {
        String[] segments = {config.orgId, config.appId, type, uuidOrName};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return this.sendRequest(request);
    }

    @NotNull //FIXME: THIS IS BROKEN
    public UsergridResponse POST(@NotNull final String type, @NotNull final Map<String, Object> jsonBody) {
        return new UsergridResponse();
    }

    @NotNull //FIXME: THIS IS BROKEN
    public UsergridResponse POST(@NotNull final String type, @NotNull final List<Map<String, Object>> jsonBodies) {
        return new UsergridResponse();
    }

    /**
     * Deletes the specified Entity.  Will throw an Invalid Argument if the UUID and getName are null
     *
     * @param entity the entity to delete.  It must have a type and UUID or getName attribute on the object
     * @return UsergridResponse
     */
    public UsergridResponse DELETE(final UsergridEntity entity) {
        ValidateEntity(entity);

        String[] segments = {config.orgId, config.appId, entity.getType(),
                entity.getUuid() == null ? entity.getName() : entity.getUuid()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }


    /**
     * Deletes an entity (if uri is the getName|uuid of the endity or a set of entities if the URI is a QL.
     *
     * @param type the getName of the collection
     * @param uuidOrName   the ID of the entity
     * @return UsergridResponse
     */
    public UsergridResponse DELETE(@NotNull final String type, @NotNull final String uuidOrName) {
        String[] segments = {config.orgId, config.appId, type, uuidOrName};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return this.sendRequest(request);
    }

    /**
     * DELETE by Query
     *
     * @param query the UsergridQuery object
     * @return QueryResult
     */
    public UsergridResponse DELETE(final UsergridQuery query) {
        String[] segments = {config.orgId, config.appId, query.getCollectionName()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        request.query = query;
        return requestManager.performRequest(request);
    }

    public void ValidAppArguments() {
        //TODO: need to add any other checks? Return void ?
        if (isEmpty(config.appId)) {
            throw new IllegalArgumentException("No application id specified");
        }
        if (isEmpty(config.orgId)) {
            throw new IllegalArgumentException("No organization id specified");
        }
    }

    public static void ValidateEntity(@NotNull final UsergridEntity entity) throws IllegalArgumentException {
        if (isEmpty(entity.getType())) {
            throw new IllegalArgumentException("UsergridEntity is required to have a 'type' property in order to to a PUT");
        }

        if (isEmpty(entity.getName()) && isEmpty(entity.getUuid())) {
            throw new IllegalArgumentException("UsergridEntity is required to have a 'name' or 'uuid' property in order to to a PUT");
        }
    }
}
