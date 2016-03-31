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

import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.usergrid.java.client.UsergridEnums.Direction;
import org.apache.usergrid.java.client.UsergridEnums.UsergridAuthMode;
import org.apache.usergrid.java.client.UsergridEnums.UsergridHttpMethod;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.model.UsergridUser;
import org.apache.usergrid.java.client.model.UsergridUserAuth;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;


/**
 * The Client class for accessing the Usergrid API. Start by instantiating this
 * class though the appropriate constructor.
 */
public class UsergridClient {

    public static final String STR_GROUPS = "groups";
    public static final String STR_USERS = "users";
    private static final Logger log = LoggerFactory.getLogger(UsergridClient.class);
    private static final String CONNECTIONS = "connections";
    private static final String CONNECTING = "connecting";
    public static String DEFAULT_BASE_URL = "https://api.usergrid.com";

    public UsergridClientConfig config;
    public UsergridRequestManager requestManager;
    private UsergridUser currentUser = null;
    private UsergridAuth tempAuth = null;

    public UsergridClient(UsergridClientConfig usergridClientConfig) {
        config = usergridClientConfig;
        requestManager = new UsergridRequestManager(this);
    }

    /**
     * Instantiate client for a specific app
     *
     * @param organizationId the organization id
     * @param applicationId  the application id or getName
     */
    public UsergridClient(final String organizationId, final String applicationId) {
        this(new UsergridClientConfig(organizationId, applicationId));
    }

    /**
     * Instantiate client for a specific app
     *
     * @param organizationId the organization id
     * @param applicationId  the application id or getName
     */
    public UsergridClient(final String organizationId, final String applicationId, final String baseurl) {
        this(new UsergridClientConfig(organizationId, applicationId, baseurl));
    }

    public UsergridClient(final String orgId, String appId, String baseurl, UsergridAuthMode authfallBack) {
        this(new UsergridClientConfig(orgId, appId, baseurl, authfallBack));
    }

    /**
     * Validate if the usergrid entity is valid.
     *
     * @param e
     */
    public static void ValidateEntity(UsergridEntity e) {
        if (isEmpty(e.getType())) {
            throw new IllegalArgumentException("UsergridEntity is required to have a 'type' property in order to to a PUT");
        }

        if (isEmpty(e.getName()) && isEmpty(e.getUuid())) {
            throw new IllegalArgumentException("UsergridEntity is required to have a 'getName' or 'uuid' property in order to to a PUT");
        }
    }

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

    /**
     * @return the Usergrid API url (default: http://api.usergrid.com)
     */
    public String getBaseUrl() {
        return config.baseUrl;
    }

    /**
     * @param apiUrl the Usergrid API url (default: http://api.usergrid.com)
     */
    public void setBaseUrl(final String apiUrl) {
        this.config.baseUrl = apiUrl;
    }

    /**
     * @param apiUrl the Usergrid API url (default: http://api.usergrid.com)
     * @return Client object for method call chaining
     */
    public UsergridClient withApiUrl(final String apiUrl) {
        this.config.baseUrl = apiUrl;
        return this;
    }

    /**
     * the orgId to set
     *
     * @param organizationId
     * @return
     */
    public UsergridClient withOrganizationId(final String organizationId) {
        this.config.orgId = organizationId;
        return this;
    }

    /**
     * @return the orgId
     */
    public String getOrgId() {
        return config.orgId;
    }

    /**
     * @return the application id or getName
     */
    public String getAppId() {
        return config.appId;
    }


    //TODO:     /// The currently logged in `UsergridUser`.

    /**
     * @param applicationId the application id or getName
     * @return Client object for method call chaining
     */
    public UsergridClient withApplicationId(final String applicationId) {
        this.config.appId = applicationId;
        return this;
    }

    /**
     * getCurrentUser -- curretnuser?
     *
     * @return the logged-in user after a successful authenticateUser request
     */
    public UsergridUser getCurrentUser() {
        return currentUser;
    }
//
//    /**
//     * High-level Usergrid API request.
//     *
//     * @param method   the HTTP Method
//     * @param params   a Map of query parameters
//     * @param data     the object to use in the body
//     * @param segments the segments/of/the/uri
//     * @return a UsergridResponse object
//     */
//    public UsergridResponse apiRequest(final String method,
//                                       final Map<String, Object> params,
//                                       Object data,
//                                       final String... segments) {
//
//        ValidAppArguments();
//        // default to JSON
//        String contentType = MediaType.APPLICATION_JSON;
//        Entity entity = Entity.entity(data == null ? STR_BLANK : data, contentType);
//
//        // create the target from the base API URL
//        WebTarget webTarget = restClient.target(config.baseUrl);
//        for (String segment : segments)
//            if (segment != null)
//                webTarget = webTarget.path(segment);
//
//        if ((method.equals(HTTP_GET) || method.equals(HTTP_PUT) || method.equals(HTTP_POST) || method.equals(HTTP_DELETE)) && !isEmpty(params)) {
//            for (Map.Entry<String, Object> param : params.entrySet()) {
//                webTarget = webTarget.queryParam(param.getKey(), param.getValue());
//            }
//        }
//
//        System.out.println(webTarget);
//        Invocation.Builder invocationBuilder = webTarget.request(contentType);
//        // todo: need to evaluate other authentication options here as well
//        UsergridAuth authForRequest = this.authForRequests();
//        if (authForRequest != null && authForRequest.accessToken != null) {
//            String auth = BEARER + authForRequest.accessToken;
//            invocationBuilder.header(HEADER_AUTHORIZATION, auth);
//        }
//
//        try {
//            if (Objects.equals(method, HTTP_POST) || Objects.equals(method, HTTP_PUT)) {
//
//                UsergridResponse response = invocationBuilder.method(method,
//                        entity,
//                        UsergridResponse.class);
//                return response;
//
//            } else {
//                return invocationBuilder.method(method,
//                        null,
//                        UsergridResponse.class);
//            }
//        } catch (Exception badRequestException) {
//            return UsergridResponse.fromException(badRequestException);
//        }
//
//    }

    /**
     * @param loggedInUser the logged-in user, usually not set by host application
     *                     //TODO: called by UsergridClient?
     */
    public void setCurrentUser(final UsergridUser loggedInUser) {
        this.currentUser = loggedInUser;
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

    public UsergridResponse authenticateUser(String username, String password) {
        UsergridUserAuth auth = new UsergridUserAuth(username, password);
        return authenticateUser(auth);
    }

    /**
     * Log the user in and get a valid access token.
     *
     * @param auth : UsergridUserAuth object containing the username and password
     * @return non-null UsergridResponse if request succeeds, check getError() for
     * "invalid_grant" to see if access is denied.
     */
    @Nullable
    public UsergridResponse authenticateUser(UsergridUserAuth auth) {
        config.userAuth = auth;
        UsergridResponse response = requestManager.AuthenticateUser();
        return response;
    }

    @Nullable
    public UsergridResponse authenticateApp(final String clientId, final String clientSecret) {
        UsergridAppAuth ugAppAuth = new UsergridAppAuth(clientId, clientSecret);
        return authenticateApp(ugAppAuth);
    }

    /**
     * Log the app in with it's client id and client secret key. Not recommended
     * for production apps.
     *
     * @param auth :   UsergridAppAuth containing the client id and secret.
     * @return non-null UsergridResponse if request succeeds, check getError() for
     * "invalid_grant" to see if access is denied.
     */
    @Nullable
    public UsergridResponse authenticateApp(UsergridAppAuth auth) {
        this.config.appAuth = auth;
        return requestManager.AuthenticateApp();
    }

    public UsergridClient usingAuth(UsergridAuth ugAuth) {
        this.tempAuth = ugAuth;
        return this;
    }

    public UsergridClient usingToken(String ugToken) {
        this.tempAuth = new UsergridAuth(ugToken,null);
        return this;
    }


    /**
     * Change the password for the currently logged in user. You must supply the
     * old password and the new password.
     *
     * @param username    the app username for whom to change the password
     * @param oldPassword the user's old password
     * @param newPassword the user's new password
     * @return UsergridResponse which encapsulates the API response
     */
    public UsergridResponse changePassword(final String username,
                                           final String oldPassword,
                                           final String newPassword) {

        Map<String, Object> data = new HashMap<>();
        data.put("newpassword", newPassword);
        data.put("oldpassword", oldPassword);

        String[] segments = {config.orgId, config.appId, STR_USERS, username, "password"};

        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, data, segments);
        return requestManager.performRequest(request);
    }

    public void logoutCurrentUser() {
        logoutUser(this.config.userAuth.username,this.config.userAuth.accessToken);
    }


    public UsergridResponse logoutUser(String userName,String token){
        String[] segments = {config.orgId, config.appId, STR_USERS, userName, ""};
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

    private void validateNonEmptyParam(final Object param,
                                       final String paramName) {
        if (isEmpty(param)) {
            throw new IllegalArgumentException(paramName + " cannot be null or empty");
        }
    }

    /**
     * Create a new usergridEntity on the server.
     *
     * @param usergridEntity
     * @return an UsergridResponse with the new usergridEntity in it.
     */
    public UsergridResponse createEntity(final UsergridEntity usergridEntity) {

        if (isEmpty(usergridEntity.getType())) {
            throw new IllegalArgumentException("Missing usergridEntity type");
        }


        String[] segments = {config.orgId, config.appId, usergridEntity.getType()};

        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, usergridEntity, segments);
        return requestManager.performRequest(request);
    }

    /**
     * Create a new entity on the server from a set of properties. Properties
     * must include a "type" property.
     *
     * @param properties
     * @return an UsergridResponse with the new entity in it.
     */
    public UsergridResponse createEntity(Map<String, Object> properties) {


        if (isEmpty(properties.get("type"))) {
            throw new IllegalArgumentException("Missing entity type");
        }

        String[] segments = {config.orgId, config.appId, properties.get("type").toString()};

        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, properties, segments);

        return requestManager.performRequest(request);
    }

    public UsergridResponse getEntity(final String type, final String id) {

        String[] segments = {config.orgId, config.appId, type, id};

        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }

    public UsergridResponse getConnections(Direction direction, UsergridEntity sourceVertex, String relationship) {

        ValidateEntity(sourceVertex);
        return getConnections(direction,sourceVertex.getType(),sourceVertex.getName(),relationship,null);
    }

    public UsergridResponse getConnections(@Nonnull Direction direction, @Nonnull String sourceVertexType,
                                           @Nonnull String sourceVertexName, @Nonnull String relationship,
                                           @Nullable UsergridQuery query) {
        String[] segments1 = {config.orgId, config.appId,
                sourceVertexType, sourceVertexName, CONNECTIONS, relationship};

        UsergridRequest request1 = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, query, segments1);

        String[] segments2 = {config.orgId, config.appId,
                sourceVertexType, sourceVertexName, CONNECTING, relationship};

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

    public UsergridResponse getConnections(@Nonnull Direction direction,
                                           @Nonnull String sourceVertexuuid, @Nonnull String relationship,
                                           @Nullable UsergridQuery query) {
        //TODO : check valid uuid.

        String[] segments1 = {config.orgId, config.appId,
                sourceVertexuuid, CONNECTIONS, relationship};

        UsergridRequest request1 = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, query, segments1);

        String[] segments2 = {config.orgId, config.appId,
                sourceVertexuuid, CONNECTING, relationship};

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

    public UsergridResponse deleteEntity(final String type,
                                         final String id) {


        String[] segments = {config.orgId, config.appId, type, id};

        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }

    /**
     * Create a connection between two entities
     *
     * @param sourceVertex  The source entity/vertex of the connection
     * @param targetVertex  The target entity/vertex of the connection
     * @param connetionName The getName of the connection/edge
     * @return
     */
    public UsergridResponse connect(final UsergridEntity sourceVertex,
                                    final String connetionName,
                                    final UsergridEntity targetVertex
    ) {
        ValidateEntity(sourceVertex);
        ValidateEntity(targetVertex);
        return this.connect(sourceVertex.getType(), sourceVertex.getUuid().toString(), connetionName, targetVertex.getUuid().toString());
    }

    /**
     * Create a connection between two entities
     *
     * @param sourceVertex     The source entity/vertex of the connection
     * @param targetVertexUUid The target entity/vertex UUID.
     * @param connetionName    The getName of the connection/edge
     * @return
     */
    public UsergridResponse connect(final UsergridEntity sourceVertex,
                                    final String connetionName,
                                    final String targetVertexUUid
    ) {
        ValidateEntity(sourceVertex);
        return this.connect(sourceVertex.getType(), sourceVertex.getUuid().toString(), connetionName, targetVertexUUid);
    }

    /**
     * Connect two entities together.
     *
     * @param connectingEntityType
     * @param connectingEntityId
     * @param connectionType
     * @param connectedEntityId
     * @return
     */
    public UsergridResponse connect(final String connectingEntityType,
                                    final String connectingEntityId,
                                    final String connectionType,
                                    final String connectedEntityId) {


        String[] segments = {config.orgId, config.appId, connectingEntityType,
                connectingEntityId, connectionType, connectedEntityId};

        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);

        return requestManager.performRequest(request);

    }

    /**
     * Connect two entities together using type and getName
     *
     * @param connectingEntityType
     * @param connectingEntityId
     * @param connectionType
     * @param connectedEntityName
     * @param connectedEntityType
     * @return
     */
    public UsergridResponse connect(final String connectingEntityType,
                                    final String connectingEntityId,
                                    final String connectionType,
                                    final String connectedEntityType,
                                    final String connectedEntityName) {

        String[] segments = {config.orgId, config.appId, connectingEntityType, connectingEntityId,
                connectionType, connectedEntityType, connectedEntityName};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }

    /**
     * Disconnect two entities.
     *
     * @param connectingEntityType
     * @param connectingEntityId
     * @param connectionType
     * @param connectedEntityId
     * @return
     */
    public UsergridResponse disConnect(final String connectingEntityType,
                                       final String connectingEntityId,
                                       final String connectionType,
                                       final String connectedEntityId) {

        String[] segments = {config.orgId, config.appId, connectingEntityType, connectingEntityId, connectionType, connectedEntityId};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }

    /**
     * Disconnect two entities.
     *
     * @param connectingEntityType
     * @param connectingEntityId
     * @param connectionType
     * @param connectedEntitytype
     * @param connectedEntityName
     * @return
     */
    public UsergridResponse disConnect(final String connectingEntityType,
                                       final String connectingEntityId,
                                       final String connectionType,
                                       final String connectedEntitytype,
                                       final String connectedEntityName) {

        String[] segments = {config.orgId, config.appId, connectingEntityType,
                connectingEntityId, connectionType, connectedEntitytype, connectedEntityName};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }

    /**
     * @param sourceVertex
     * @param targetVertex
     * @param connetionName
     * @return
     */
    public UsergridResponse disConnect(final UsergridEntity sourceVertex,
                                       final String connetionName,
                                       final UsergridEntity targetVertex) {
        ValidateEntity(sourceVertex);
        ValidateEntity(targetVertex);
        return this.disConnect(sourceVertex.getType(), sourceVertex.getUuid().toString(), connetionName, targetVertex.getUuid().toString());
    }


    /**
     * GET an entity using the collection getName and (getName|uuid)
     *
     * @param collection the getName of the collection
     * @param entityId   the entity ID
     * @return UsergridResponse
     */
    public UsergridResponse GET(final String collection,
                                final String entityId) {

        String[] segments = {config.orgId, config.appId, collection, entityId};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }

    /**
     * GET an entity using the UUID
     *
     * @param uuid
     * @return UsergridResponse
     */

    public UsergridResponse GET(final UUID uuid) {

        String[] segments = {config.orgId, config.appId, uuid.toString()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }


    /**
     * GET by Query
     *
     * @param q the UsergridQuery object
     * @return QueryResult
     */
    public UsergridResponse GET(final UsergridQuery q) {

        String[] segments = {config.orgId, config.appId, q.getCollectionName()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.GET,
                MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl,
                null,
                null,
                segments);
        request.query = q;

        return requestManager.performRequest(request);
//        return new QueryResult(this,
//                UsergridHttpMethod.GET.toString(),
//                requestManager.performRequest(request),
//                q);
    }

    /**
     * PUT (update) an entity, requires the type and one of (getName | uuid) to be set on the entity
     *
     * @param e the entity to update
     * @return UsergridResponse
     */
    public UsergridResponse PUT(final UsergridEntity e) {

        ValidateEntity(e);

        String[] segments = {config.orgId, config.appId, e.getType(),
                e.getUuidString() != null ? e.getUuidString() : e.getName()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.PUT, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, e.getProperties(), segments);
        return requestManager.performRequest(request);
    }

    //TODO: UsergridClient.PUT("<type>", bodyObject); // excluding uuid or getName will result in a new record being created

    /**
     * is used to update entities in a Usergrid collection.
     *
     * @param type     - collection
     * @param entityId - uuid or getName
     * @return
     */
    public UsergridResponse PUT(final String type,
                                final String entityId) {

        String[] segments = {config.orgId, config.appId, type, entityId};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.PUT, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }

    /**
     * PUT by Query
     *
     * @param q      the UsergridQuery object
     * @param fields the fields to be applied as an update to the results (entities) of the query
     * @return QueryResult
     */
    public UsergridResponse PUT(final UsergridQuery q, Map<String, Object> fields) {


        String[] segments = {config.orgId, config.appId, q.getCollectionName()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.PUT, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        request.query = q;

        return requestManager.performRequest(request);
    }

    /**
     * Creates an entity using a UsergridResponse object reference
     *
     * @param e the entity which will be created
     * @return UsergridResponse
     */
    public UsergridResponse POST(final @NonNull UsergridEntity e) {

        // ValidateEntity(e);

        String[] segments = {config.orgId, config.appId, e.getType()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, e, segments);
        return requestManager.performRequest(request);
    }


    /**
     * Creates an entity using a UsergridResponse object reference
     *
     * @param type - collection
     *             entityId - uuid or getName
     * @return UsergridResponse
     */
    public UsergridResponse POST(final String type,
                                 final String entityId) {

        String[] segments = {config.orgId, config.appId, type, entityId};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }

    //TODO: UsergridClient.POST("<type>", bodyObject); // excluding uuid or getName will result in a new record being created

    /**
     * Deletes the specified Entity.  Will throw an Invalid Argument if the UUID and getName are null
     *
     * @param e the entity to delete.  It must have a type and UUID or getName attribute on the object
     * @return UsergridResponse
     */
    public UsergridResponse DELETE(final UsergridEntity e) {
        ValidateEntity(e);

        String[] segments = {config.orgId, config.appId, e.getType(),
                e.getUuid() == null ? e.getName() : e.getUuidString()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);
    }


    /**
     * Deletes an entity (if uri is the getName|uuid of the endity or a set of entities if the URI is a QL.
     *
     * @param collection the getName of the collection
     * @param entityId   the ID of the entity
     * @return UsergridResponse
     */
    public UsergridResponse DELETE(final String collection,
                                   final String entityId) {

        String[] segments = {config.orgId, config.appId, collection, entityId};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);

    }

    public UsergridResponse DELETE(final UUID uuid) {

        String[] segments = {config.orgId, config.appId, uuid.toString()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        return requestManager.performRequest(request);

    }

    /**
     * DELETE by Query
     *
     * @param q the UsergridQuery object
     * @return QueryResult
     */
    public UsergridResponse DELETE(final UsergridQuery q) {


        String[] segments = {config.orgId, config.appId, q.getCollectionName()};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                config.baseUrl, null, null, segments);
        request.query = q;


        return requestManager.performRequest(request);
    }


    /*
  TODO: UsergridClient("<init-token>");
  Extended UsergridClient.
  UsergridClient.PATCH()

   */

  /*
    TODO: UsergridClient.tempAuth()
        UsergridClient.authFallback()

          UsergridClient.paginationPreloadPages()
          UsergridClient.paginationCacheTimeout()
          UsergridClient.paginationCursors()


          UsergridClient.GET("<type>", "<primaryIndex>"); --> what is primary index?
          //handle bodyobject - json?
          UsergridClient.PUT("<type>", bodyObject); // excluding uuid or getName will result in a new record being created
          UsergridClient.PUT(bodyObject); // including type -- default type?
          UsergridClient.PUT(q, bodyObject); --> update multiple entities
          UsergridClient.DELETE(["<uuid1>", "<uuid2>"]); --> how to delete multiple entities?
          UsergridClient.DELETE("<type>", "<primaryIndex>"); -> what is primary index??

          UsergridClient.uploadAsset();
          UsergridClient.downloadAsset();

          UsergridClient.getConnections(direction, entity, "relationship"); -> directions : OUT : connection , IN - connecting
          UsergridClient.usingAuth();
        //TODO:  return values for all the auth functions.

 */


}
