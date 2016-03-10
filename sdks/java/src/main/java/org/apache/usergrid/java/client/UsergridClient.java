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

import javax.annotation.Nullable;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.usergrid.java.client.filter.ErrorResponseFilter;
import org.apache.usergrid.java.client.model.*;
import org.apache.usergrid.java.client.query.EntityQueryResult;
import org.apache.usergrid.java.client.query.LegacyQueryResult;
import org.apache.usergrid.java.client.query.QueryResult;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.UsergridEnums.UsergridAuthFallBack;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.*;

import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;


/**
 * The Client class for accessing the Usergrid API. Start by instantiating this
 * class though the appropriate constructor.
 */
public class UsergridClient {

    public  static String DEFAULT_BASE_URL = "http://localhost:8080";
    public UsergridClientConfig config;
    private UsergridUser currentUser = null;
    private UsergridAuth tempAuth = null;
    private UsergridAuth appAuth = null;

    public static final String HTTP_POST = "POST";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_GET = "GET";
    public static final String HTTP_DELETE = "DELETE";
    public static final String STR_GROUPS = "groups";
    public static final String STR_USERS = "users";
    public static final String STR_DEFAULT = "default";
    public static final String STR_BLANK = "";

    private static final Logger log = LoggerFactory.getLogger(UsergridClient.class);
    private static final String CONNECTIONS = "connections";
    private static final String CONNECTING = "connecting";
    private static final String STRING_EXPIRES_IN = "expires_in";

    private String clientId;
    private String clientSecret;
    private javax.ws.rs.client.Client restClient;

    public UsergridAuth authForRequests() {
        UsergridAuth authForRequests = null;
        if( tempAuth != null && tempAuth.isValidToken() ) {
            authForRequests = tempAuth;
            tempAuth = null;
        } else if( this.currentUser != null && this.currentUser.userAuth.isValidToken() ) {
            authForRequests = this.currentUser.userAuth;
        } else if( this.appAuth != null && config.authFallBack == UsergridAuthFallBack.APP ) {
            authForRequests = this.appAuth;
        }
        return authForRequests;
    }



    /**
     * Default constructor for instantiating a client.
     */
    public UsergridClient() {
        init();
    }

    public UsergridClient(UsergridClientConfig usergridClientConfig) {
        config = usergridClientConfig;
        init();
        //todo: getCurrentUserFromKeychain??
    }


    /**
     * Instantiate client for a specific app
     *
     * @param organizationId the organization id
     * @param applicationId  the application id or getName
     */
    public UsergridClient(final String organizationId,
                          final String applicationId) {
        new UsergridClient(new UsergridClientConfig(organizationId,applicationId));
    }

    /**
     * Instantiate client for a specific app
     *
     * @param organizationId the organization id
     * @param applicationId  the application id or getName
     */
    public UsergridClient(final String organizationId,
                          final String applicationId,
                          final String baseurl) {
        new UsergridClient(new UsergridClientConfig(organizationId,applicationId,baseurl));
    }

    public UsergridClient(final String orgId, String appId, String baseurl, UsergridAuthFallBack authfallBack, UsergridAppAuth ugappAuth){
        new UsergridClient(new UsergridClientConfig(orgId,appId,baseurl,authfallBack,ugappAuth));
    }

    public void init() {
        restClient = ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .register(new ErrorResponseFilter())
                .build();

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


    /**
     * @param applicationId the application id or getName
     * @return Client object for method call chaining
     */
    public UsergridClient withApplicationId(final String applicationId) {
        this.config.appId = applicationId;
        return this;
    }


    //TODO:     /// The currently logged in `UsergridUser`.
    /**
     * getCurrentUser -- curretnuser?
     *
     * @return the logged-in user after a successful authenticateUser request
     */
    public UsergridUser getCurrentUser() {
        return currentUser;
    }

    /**
     * @param loggedInUser the logged-in user, usually not set by host application
     *                     //TODO: called by UsergridClient?
     */
    public void setCurrentUser(final UsergridUser loggedInUser) {
        this.currentUser = loggedInUser;
    }

    /**
     * High-level Usergrid API request.
     *
     * @param method   the HTTP Method
     * @param params   a Map of query parameters
     * @param data     the object to use in the body
     * @param segments the segments/of/the/uri
     * @return a UsergridResponse object
     */
    public UsergridResponse apiRequest(final String method,
                                       final Map<String, Object> params,
                                       Object data,
                                       final String... segments) {
        appAuth();
        // default to JSON
        String contentType = MediaType.APPLICATION_JSON;
        Entity entity = Entity.entity(data == null ? STR_BLANK : data, contentType);

        // create the target from the base API URL
        WebTarget webTarget = restClient.target(config.baseUrl);
        for (String segment : segments)
            if (segment != null)
                webTarget = webTarget.path(segment);

        if ((method.equals(HTTP_GET) || method.equals(HTTP_PUT) || method.equals(HTTP_POST) || method.equals(HTTP_DELETE)) && !isEmpty(params)) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                webTarget = webTarget.queryParam(param.getKey(), param.getValue());
            }
        }

        System.out.println(webTarget);
        Invocation.Builder invocationBuilder = webTarget.request(contentType);
        // todo: need to evaluate other authentication options here as well
        UsergridAuth authForRequest = this.authForRequests();
        if (authForRequest != null && authForRequest.accessToken != null) {
            String auth = BEARER + authForRequest.accessToken;
            invocationBuilder.header(HEADER_AUTHORIZATION, auth);
        }

        try {
            if (Objects.equals(method, HTTP_POST) || Objects.equals(method, HTTP_PUT)) {

                UsergridResponse response = invocationBuilder.method(method,
                        entity,
                        UsergridResponse.class);
                return response;

            } else {
                return invocationBuilder.method(method,
                        null,
                        UsergridResponse.class);
            }
        } catch (Exception badRequestException) {
            return UsergridResponse.fromException(badRequestException);
        }

    }


    public void appAuth() {
        //TODO: need to add any other checks? Return void ?
        if (isEmpty(config.appId)) {
            throw new IllegalArgumentException("No application id specified");
        }
        if (isEmpty(config.orgId)) {
            throw new IllegalArgumentException("No organization id specified");
        }
    }

    public UsergridResponse authenticateUser(String username,String password) {
        UsergridUserAuth auth = new UsergridUserAuth(username,password);
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

        String email = auth.username;
        String password = auth.password;
        validateNonEmptyParam(email, "email");
        validateNonEmptyParam(password, "password");

        Map<String, Object> formData = new HashMap<>();
        formData.put("grant_type", "password");
        formData.put("username", email);
        formData.put("password", password);

        UsergridResponse response = apiRequest(HTTP_POST, null, formData, config.orgId, config.appId, "token");

        if (response == null) {
            return null;

        }

        if (!isEmpty(response.getAccessToken()) && (response.currentUser() != null)) {

            auth.setAccessToken(response.getAccessToken());
            auth.setTokenExpiry(response.getProperties().get(STRING_EXPIRES_IN).asLong() - 5);
            response.currentUser().userAuth = auth;

            setCurrentUser(response.currentUser());

            currentOrganization = null; //TODO : should this be set to null ?
            log.info("Client.authenticateUser(): Access token: " + this.appAuth.accessToken);
        } else {
            log.info("Client.authenticateUser(): Response: " + response);
        }

        return response;
    }

    public UsergridClient usingAuth(UsergridAuth ugAuth){
        this.tempAuth = ugAuth;
        return this;
    }


    /**
     *
     * // Or with a callback:
     TODO: UsergridClient.authenticateUser(auth, function(callback) {
     });

     */

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

        return apiRequest(HTTP_POST, null, data, config.orgId, config.appId, STR_USERS, username, "password");

    }

    /**
     * Log the user in with their numeric pin-code and get a valid access token.
     *
     * @param email
     * @param pin
     * @return non-null UsergridResponse if request succeeds, check getError() for
     * "invalid_grant" to see if access is denied.
     */
    @Nullable
    public UsergridResponse authorizeAppUserViaPin(final String email,
                                                   final String pin) {


        validateNonEmptyParam(email, "email");
        validateNonEmptyParam(pin, "pin");

        currentUser = null;
        currentOrganization = null;

        Map<String, Object> formData = new HashMap<>();
        formData.put("grant_type", "pin");
        formData.put("username", email);
        formData.put("pin", pin);

        UsergridResponse response = apiRequest(HTTP_POST, formData, null, config.orgId, config.appId, "token");

        if (response == null) {
            return null;
        }

        if (!isEmpty(response.getAccessToken()) && (response.currentUser() != null)) {
            currentUser = response.currentUser();
            this.appAuth.setAccessToken(response.getAccessToken());
            currentOrganization = null;
            log.info("Client.authenticateUser(): Access token: " + this.appAuth.accessToken);
        } else {
            log.info("Client.authenticateUser(): Response: " + response);
        }

        return response;
    }

    /**
     * Log the user in with their Facebook access token retrived via Facebook
     * OAuth.
     *
     * @param fb_access_token the access token from Facebook
     * @return non-null UsergridResponse if request succeeds, check getError() for
     * "invalid_grant" to see if access is denied.
     */
    @Nullable
    public UsergridResponse authorizeAppUserViaFacebook(final String fb_access_token) {

        validateNonEmptyParam(fb_access_token, "Facebook token");

        currentUser = null;
        currentOrganization = null;
        Map<String, Object> formData = new HashMap<>();
        formData.put("fb_access_token", fb_access_token);
        UsergridResponse response = apiRequest(HTTP_POST, formData, null, config.orgId, config.appId, "auth", "facebook");

        if (response == null) {
            return null;
        }

        if (!isEmpty(response.getAccessToken()) && (response.currentUser() != null)) {

            currentUser = response.currentUser();
            this.appAuth.setAccessToken(response.getAccessToken());
            currentOrganization = null;
            log.info("Client.authorizeAppUserViaFacebook(): Access token: " + this.appAuth.accessToken);

        } else {

            log.info("Client.authorizeAppUserViaFacebook(): Response: " + response);
        }

        return response;
    }


    @Nullable
    public UsergridResponse authenticateApp(final String clientId, final String clientSecret){
        UsergridAppAuth ugAppAuth = new UsergridAppAuth(clientId,clientSecret);
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
    public UsergridResponse authenticateApp(UsergridAppAuth auth){
//    final String clientId,
//                                            final String clientSecret)

        clientId = auth.clientId;
        clientSecret = auth.clientSecret;
        validateNonEmptyParam(clientId, "client identifier");
        validateNonEmptyParam(clientSecret, "client secret");

        currentUser = null;
        currentOrganization = null;
        Map<String, Object> data = new HashMap<>();
        data.put("grant_type", "client_credentials");
        data.put("client_id", clientId);
        data.put("client_secret", clientSecret);
        UsergridResponse response = apiRequest(HTTP_POST, null, data , config.orgId, config.appId, "token");

        if (response == null) {
            return null;
        }

        if (!isEmpty(response.getAccessToken())) {
            auth.setAccessToken(response.getAccessToken());
            auth.setTokenExpiry(response.getProperties().get(STRING_EXPIRES_IN).asLong() - 5);
            this.appAuth = auth;
            log.info("Client.authenticateApp(): Access token: " + this.appAuth.accessToken);
        } else {

            log.info("Client.authenticateApp(): Response: " + response);
        }

        return response;
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

        return apiRequest(HTTP_POST, null, usergridEntity, config.orgId, config.appId, usergridEntity.getType());
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

        return apiRequest(HTTP_POST, null, properties, config.orgId, config.appId, properties.get("type").toString());
    }



    /**
     * Perform a query request and return a query object. The QueryResult object
     * provides a simple way of dealing with result sets that need to be
     * iterated or paged through.
     *
     * @param method
     * @param params
     * @param data
     * @param segments
     * @return
     */
    public LegacyQueryResult queryEntities(final String method,
                                           final Map<String, Object> params,
                                           final Object data,
                                           final String... segments) {

        return new EntityQueryResult(this, apiRequest(method, params, data, segments), method, params, data, segments);
    }

    /**
     * Perform a query of the users collection.
     *
     * @return
     */
    public LegacyQueryResult queryUsers() {
        UsergridQuery q = new UsergridQuery.Builder()
                .collection("users")
                .desc("created")
                .build();

        return queryEntities(HTTP_GET, null, null, config.orgId, config.appId, STR_USERS);
    }

    /**
     * Perform a query of the users collection using the provided query command.
     * For example: "getName contains 'ed'".
     *
     * @param ql
     * @return
     */
    public LegacyQueryResult queryUsers(String ql) {

        Map<String, Object> params = new HashMap<>();
        params.put("ql", ql);

        return queryEntities(HTTP_GET, params, null, config.orgId, config.appId, STR_USERS);
    }

    /**
     * Perform a query of the users collection within the specified distance of
     * the specified location and optionally using the provided query command.
     * For example: "getName contains 'ed'".
     *
     * @return
     */
    public LegacyQueryResult queryUsersWithinLocation(final float distance,
                                                      final float lattitude,
                                                      final float longitude,
                                                      final String ql) {

        Map<String, Object> params = new HashMap<>();
        params.put("ql", this.makeLocationQL(distance, lattitude, longitude, ql));

        return queryEntities(HTTP_GET, params, null, config.orgId, config.appId, STR_USERS);
    }

    public UsergridResponse getEntity(final String type, final String id) {

        return apiRequest(HTTP_GET, null, null, config.orgId, config.appId, type, id);
    }

    public UsergridResponse getConnections(Direction direction, UsergridEntity sourceVertex, String relationship){

        ValidateEntity(sourceVertex);
        switch (direction) {
            case OUT:
                return apiRequest(HTTP_GET,null,null, config.orgId, config.appId,
                        sourceVertex.getType(),sourceVertex.getUuidString(),CONNECTIONS,relationship);
            case IN:
               return apiRequest(HTTP_GET,null,null, config.orgId, config.appId,
                       sourceVertex.getType(),sourceVertex.getUuidString(),CONNECTING,relationship);

        }

        return null; // invalid connection getName.
    }

    public UsergridResponse deleteEntity(final String type,
                                         final String id) {

        return apiRequest(HTTP_DELETE, null, null, config.orgId, config.appId, type, id);
    }

    /**
     * Queries the users for the specified group.
     *
     * @param groupId
     * @return
     */
    public LegacyQueryResult queryUsersForGroup(final String groupId) {

        return queryEntities(HTTP_GET, null, null, config.orgId, config.appId, STR_GROUPS, groupId, STR_USERS);
    }

    /**
     * Adds a user to the specified groups.
     *
     * @param userId
     * @param groupId
     * @return
     */
    public UsergridResponse addUserToGroup(final String userId,
                                           final String groupId) {


        return apiRequest(HTTP_POST, null, null, config.orgId, config.appId, STR_GROUPS, groupId, STR_USERS, userId);
    }

    /**
     * Creates a group with the specified group path. Group paths can be slash
     * ("/") delimited like file paths for hierarchical group relationships.
     *
     * @param groupPath
     * @return
     */
    public UsergridResponse createGroup(final String groupPath) {
        return createGroup(groupPath, null);
    }

    /**
     * Creates a group with the specified group path and group title. Group
     * paths can be slash ("/") delimited like file paths for hierarchical group
     * relationships.
     *
     * @param groupPath
     * @param groupTitle
     * @return
     */
    public UsergridResponse createGroup(final String groupPath,
                                        final String groupTitle) {

        return createGroup(groupPath, groupTitle, null);
    }

    /**
     * Create a group with a path, title and getName
     *
     * @param groupPath
     * @param groupTitle
     * @param groupName
     * @return
     */
    public UsergridResponse createGroup(final String groupPath,
                                        final String groupTitle,
                                        final String groupName) {

        UsergridEntity group = new UsergridEntity(groupName);
        group.putproperty("path", groupPath);


        if (groupTitle != null) {
            group.putproperty("title", groupTitle);
        }

        if (groupName != null) {
            group.putproperty("getName", groupName);
        }

        return this.createEntity(group);
    }

    /**
     * Perform a query of the users collection using the provided query command.
     * For example: "getName contains 'ed'".
     *
     * @param ql
     * @return
     */
    public LegacyQueryResult queryGroups(final String ql) {

        Map<String, Object> params = new HashMap<>();
        params.put("ql", ql);

        return queryEntities(HTTP_GET, params, null, config.orgId, config.appId, STR_GROUPS);
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
     * @param sourceVertex  The source entity/vertex of the connection
     * @param targetVertexUUid  The target entity/vertex UUID.
     * @param connetionName The getName of the connection/edge
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

        return apiRequest(HTTP_POST, null, null, config.orgId, config.appId, connectingEntityType, connectingEntityId, connectionType, connectedEntityId);
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

        return apiRequest(HTTP_POST, null, null, config.orgId, config.appId, connectingEntityType, connectingEntityId, connectionType, connectedEntityType, connectedEntityName);
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
    public UsergridResponse disconnect(final String connectingEntityType,
                                       final String connectingEntityId,
                                       final String connectionType,
                                       final String connectedEntityId) {

        return apiRequest(HTTP_DELETE, null, null, config.orgId, config.appId, connectingEntityType, connectingEntityId, connectionType, connectedEntityId);
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
    public UsergridResponse disconnect(final String connectingEntityType,
                                       final String connectingEntityId,
                                       final String connectionType,
                                       final String connectedEntitytype,
                                       final String connectedEntityName) {

        return apiRequest(HTTP_DELETE, null, null, config.orgId, config.appId, connectingEntityType, connectingEntityId, connectionType, connectedEntitytype,connectedEntityName);
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
     * QueryResult the connected entities.
     *
     * @param connectingEntityType
     * @param connectingEntityId
     * @param connectionType
     * @param ql
     * @return
     */
    public LegacyQueryResult queryEntityConnections(final String connectingEntityType,
                                                    final String connectingEntityId,
                                                    final String connectionType, String ql) {

        Map<String, Object> params = new HashMap<>();
        params.put("ql", ql);

        return queryEntities(HTTP_GET, params, null, config.orgId, config.appId, connectingEntityType, connectingEntityId, connectionType);
    }

    protected String makeLocationQL(float distance, double lattitude,
                                    double longitude, String ql) {
        String within = String.format("within %d of %d , %d", distance, lattitude, longitude);
        ql = ql == null ? within : within + " and " + ql;

        return ql;
    }

    /**
     * QueryResult the connected entities within distance of a specific point.
     *
     * @param connectingEntityType
     * @param connectingEntityId
     * @param connectionType
     * @param distance
     * @param latitude
     * @param longitude
     * @return
     */
    public LegacyQueryResult queryEntityConnectionsWithinLocation(final String connectingEntityType,
                                                                  final String connectingEntityId,
                                                                  final String connectionType,
                                                                  final float distance,
                                                                  float latitude,
                                                                  final float longitude,
                                                                  final String ql) {

        Map<String, Object> params = new HashMap<>();
        params.put("ql", makeLocationQL(distance, latitude, longitude, ql));

        return queryEntities(HTTP_GET, params, null, config.orgId, config.appId, connectingEntityType, connectingEntityId, connectionType);
    }


    public UsergridResponse queryEdgesForVertex(final String srcType,
                                                final String srcID) {

        return apiRequest(HTTP_GET, null, null, config.orgId, config.appId, srcType, srcID);
    }


    public UsergridResponse queryCollections() {

        return apiRequest(HTTP_GET, null, null, this.config.orgId, this.config.appId);
    }

    public UsergridResponse queryConnection(final String... segments) {

        String[] paramPath = new String[10];
        paramPath[0] = this.config.orgId;
        paramPath[1] = this.config.appId;
        System.arraycopy(segments, 0, paramPath, 2, segments.length);

        return apiRequest(HTTP_GET, null, null, paramPath);
    }



  /*
   -------------------------------------
   --------- ENTITY OPERATIONS ---------
   -------------------------------------
   */

    /**
     * PUT (update) an entity, requires the type and one of (getName | uuid) to be set on the entity
     *
     * @param e the entity to update
     * @return UsergridResponse
     */
    public UsergridResponse PUT(final UsergridEntity e) {

        ValidateEntity(e);
        return apiRequest(HTTP_PUT, null, e.getProperties(), config.orgId, config.appId, e.getType(), e.getUuidString() != null ? e.getUuidString() : e.getName());
    }

    /**
     * is used to update entities in a Usergrid collection.
     *
     * @param type     - collection
     * @param entityId - uuid or getName
     * @return
     */
    public UsergridResponse PUT(final String type,
                                final String entityId) {
        return apiRequest(HTTP_PUT, null, null, config.orgId, config.appId, type, entityId);
    }

    //TODO: UsergridClient.PUT("<type>", bodyObject); // excluding uuid or getName will result in a new record being created


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

    /**
     * Creates an entity using a UsergridResponse object reference
     *
     * @param e the entity which will be created
     * @return UsergridResponse
     */
    public UsergridResponse POST(final @NonNull UsergridEntity e) {

        ValidateEntity(e);
        return apiRequest(HTTP_POST, null, e, this.config.orgId, this.config.appId, e.getType());
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
        return apiRequest(HTTP_POST, null, null, config.orgId, config.appId, type, entityId);
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
        return apiRequest(HTTP_DELETE, null, null, config.orgId, config.appId, e.getType(), e.getUuid() == null ? e.getName() : e.getUuidString());
    }



    /*
   -------------------------------------
   --------- STRING OPERATIONS ---------
   -------------------------------------
   */

    /**
     * Deletes an entity (if uri is the getName|uuid of the endity or a set of entities if the URI is a QL.
     *
     * @param collection the getName of the collection
     * @param entityId   the ID of the entity
     * @return UsergridResponse
     */
    public UsergridResponse DELETE(final String collection,
                                   final String entityId) {

        return apiRequest(HTTP_DELETE, null, null, config.orgId, config.appId, collection, entityId);
    }

    public UsergridResponse DELETE(final UUID uuid) {

        return apiRequest(HTTP_DELETE, null, null, config.orgId, config.appId, uuid.toString());
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

        return apiRequest(HTTP_GET, null, null, config.orgId, config.appId, collection, entityId);
    }

    /**
     * GET an entity using the UUID
     *
     * @param uuid
     * @return UsergridResponse
     */

    public UsergridResponse GET(final UUID uuid) {
        return apiRequest(HTTP_GET, null, null, config.orgId, config.appId, uuid.toString());
    }



    /*
   -------------------------------------
   ---------- QUERY OPERATIONS ---------
   -------------------------------------
   */


    /**
     * PUT by Query
     *
     * @param q      the UsergridQuery object
     * @param fields the fields to be applied as an update to the results (entities) of the query
     * @return QueryResult
     */
    public QueryResult PUT(final UsergridQuery q, Map<String, Object> fields) {

        return new QueryResult(this,
                HTTP_PUT,
                apiRequest(HTTP_PUT, q.params(), fields, config.orgId, config.appId, q.getCollectionName()),
                q,
                fields);
    }

    /**
     * GET by Query
     *
     * @param q the UsergridQuery object
     * @return QueryResult
     */
    public QueryResult GET(final UsergridQuery q) {

        return new QueryResult(this,
                HTTP_GET,
                apiRequest(HTTP_GET, q.params(), null, config.orgId, config.appId, q.getCollectionName()),
                q);
    }

    public QueryResult GETFromQuery(final UsergridQuery q) {

        return new QueryResult(this,
                HTTP_GET,
                apiRequest(HTTP_GET, q.params(), null, config.orgId, config.appId, q.getCollectionName()),
                q);
    }


    /**
     * DELETE by Query
     *
     * @param q the UsergridQuery object
     * @return QueryResult
     */
    public QueryResult DELETE(final UsergridQuery q) {

        return new QueryResult(this,
                HTTP_DELETE,
                apiRequest(HTTP_DELETE, q.params(), null, config.orgId, config.appId, q.getCollectionName()),
                q);
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
