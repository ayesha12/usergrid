package org.apache.usergrid.java.client;

import org.apache.usergrid.java.client.UsergridEnums.UsergridAuthMode;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.model.UsergridUserAuth;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.apache.usergrid.java.client.UsergridEnums.Direction;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;


/**
 * Created by Jeff West on 9/2/15.
 */
public final class Usergrid {

    private static UsergridClient sharedClient;

    private Usergrid() {
        // Private constructor because we only have static methods.
    }

    /**
     * Instantiate a new instance of usergrid.
     *
     * @param apiUrl
     * @param orgName
     * @param appName
     */
    public static UsergridClient initSharedInstance(@Nonnull final String apiUrl, @Nonnull final String orgName, @Nonnull final String appName) {
        return Usergrid.initSharedInstance(new UsergridClientConfig(orgName, appName, apiUrl));
    }

    public static UsergridClient initSharedInstance(@Nonnull final String apiUrl, @Nonnull final String orgName, @Nonnull final String appName, @Nonnull final UsergridAuthMode authMode) {
        return Usergrid.initSharedInstance(new UsergridClientConfig(orgName, appName, apiUrl, authMode));
    }

    public static UsergridClient init(@Nonnull final String apiUrl, @Nonnull final String orgName, @Nonnull final String appName) {
        return Usergrid.initSharedInstance(new UsergridClientConfig(orgName, appName, apiUrl));
    }

    /**
     * Instantiate a new instance of usergrid.
     *
     * @param ugConfig
     */
    public static UsergridClient initSharedInstance(@Nonnull final UsergridClientConfig ugConfig) {
        if (isEmpty(ugConfig) || isEmpty(ugConfig.appId) || isEmpty(ugConfig.orgId) || isEmpty(ugConfig.baseUrl)) {
            throw new IllegalArgumentException("One of the input arguments is empty.");
        } else if (isInitialized()) {
            System.out.print("The Usergrid shared instance was already initialized. All subsequent initialization attempts (including this) will be ignored.");
        } else {
            sharedClient = new UsergridClient(ugConfig);
        }
        return sharedClient;
    }

    @Nonnull
    public static boolean isInitialized() {
        return (sharedClient != null);
    }

    public static void reset() {
        sharedClient = null;
    }

    public static UsergridClient getInstance() {
        if (!Usergrid.isInitialized()) {
            throw new NullPointerException("Shared client has not been initialized!");
        }
        return Usergrid.sharedClient;
    }

    public static UsergridResponse authorizeAppClient(@Nonnull final String appClientId,
                                                      @Nonnull final String appClientSecret) {

        UsergridAppAuth ugAppAuth = new UsergridAppAuth(appClientId, appClientSecret);
        return getInstance().authenticateApp(ugAppAuth);
    }

    public static UsergridResponse authorizeAppUser(@Nonnull String username,@Nonnull String password) {
        UsergridUserAuth ugUserAuth = new UsergridUserAuth(username, password);
        return getInstance().authenticateUser(ugUserAuth);

    }


    public static UsergridResponse authenticateApp(@Nonnull final String clientId, @Nonnull final String clientSecret) {
        return Usergrid.getInstance().authenticateApp(clientId, clientSecret);
    }

    public static UsergridResponse authenticateApp(@Nonnull final UsergridAppAuth auth) {
        return Usergrid.getInstance().authenticateApp(auth);
    }

    public static UsergridResponse authenticateUser(@Nonnull final String username, @Nonnull final String password) {
        return Usergrid.getInstance().authenticateUser(username, password);
    }

    public static UsergridResponse authenticateUser(@Nonnull final UsergridUserAuth auth) {
        return Usergrid.getInstance().authenticateUser(auth);
    }

    public static UsergridClient usingAuth(@Nonnull UsergridAuth ugAuth) {
        return Usergrid.getInstance().usingAuth(ugAuth);
    }

    public static UsergridAuth authForRequests() {
        return Usergrid.getInstance().authForRequests();
    }

    public static UsergridResponse GET(@Nonnull final String type,
                                       @Nonnull final String uriSuffix) {
        return Usergrid.getInstance().GET(type, uriSuffix);
    }

    public static UsergridResponse GET(@Nonnull final UUID uuid) {
        return Usergrid.getInstance().GET(uuid);
    }

    public static UsergridResponse GET(@Nonnull final UsergridQuery q) {

        return Usergrid.getInstance().GET(q);
    }

    public static UsergridResponse POST(@Nonnull final UsergridEntity e) {
        return Usergrid.getInstance().POST(e);
    }

    public static UsergridResponse POST(@Nonnull final String type,
                                        @Nonnull final String entityId) {
        return Usergrid.getInstance().POST(type, entityId);
    }

    public static UsergridResponse PUT(@Nonnull final String type,
                                       @Nonnull final String entityId) {
        return Usergrid.getInstance().PUT(type, entityId);
    }

    public static UsergridResponse PUT(@Nonnull final UsergridEntity e) {
        return Usergrid.getInstance().PUT(e);
    }

    public static UsergridResponse PUT(@Nonnull final UsergridQuery q,
                                  @Nonnull final Map<String, Object> fields) {
        return Usergrid.getInstance().PUT(q, fields);
    }

    public static UsergridResponse DELETE(@Nonnull final UsergridEntity e) {
        return Usergrid.getInstance().DELETE(e);
    }

    public static UsergridResponse DELETE(@Nonnull final String collection,
                                          @Nonnull final String entityId) {

        return Usergrid.getInstance().DELETE(collection, entityId);
    }

    public static UsergridResponse DELETE(@Nonnull final UUID uuid) {
        return Usergrid.getInstance().DELETE(uuid);
    }

    public static UsergridResponse DELETE(@Nonnull final UsergridQuery q) {
        return Usergrid.getInstance().DELETE(q);
    }


    public static UsergridResponse getConnections(@Nonnull final Direction direction,
                                                  @Nonnull final UsergridEntity sourceVertex,
                                                  @Nonnull final String relationship) {
        return Usergrid.getInstance().getConnections(direction, sourceVertex, relationship);
    }

    public static UsergridResponse deleteEntity(@Nonnull final String type,
                                                @Nonnull final String id) {
        return Usergrid.getInstance().deleteEntity(type, id);
    }

    public static UsergridResponse connect(@Nonnull final UsergridEntity sourceVertex,
                                           @Nonnull final String connetionName,
                                           @Nonnull final UsergridEntity targetVertex
    ) {
        return Usergrid.getInstance().connect(sourceVertex, connetionName, targetVertex);
    }


    public static UsergridResponse connect(@Nonnull final UsergridEntity sourceVertex,
                                           @Nonnull final String connetionName,
                                           @Nonnull final String targetVertexUUid
    ) {
        return Usergrid.getInstance().connect(sourceVertex, connetionName, targetVertexUUid);
    }

    public static UsergridResponse connect(@Nonnull final String connectingEntityType,
                                           @Nonnull final String connectingEntityId,
                                           @Nonnull final String connectionType,
                                           @Nonnull final String connectedEntityId) {

        return Usergrid.getInstance().connect(connectingEntityType, connectingEntityId, connectionType, connectedEntityId);
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
    public static UsergridResponse connect(@Nonnull final String connectingEntityType,
                                           @Nonnull final String connectingEntityId,
                                           @Nonnull final String connectionType,
                                           @Nonnull final String connectedEntityType,
                                           @Nonnull final String connectedEntityName) {

        return Usergrid.getInstance().connect(connectingEntityType, connectingEntityId, connectionType, connectedEntityType, connectedEntityName);
    }

    public static UsergridResponse disConnect(@Nonnull final String connectingEntityType,
                                              @Nonnull final String connectingEntityId,
                                              @Nonnull final String connectionType,
                                              @Nonnull final String connectedEntityId) {

        return disConnect(connectingEntityType, connectingEntityId, connectionType, connectedEntityId);
    }

    public static UsergridResponse disConnect(@Nonnull final String connectingEntityType,
                                              @Nonnull final String connectingEntityId,
                                              @Nonnull final String connectionType,
                                              @Nonnull final String connectedEntitytype,
                                              @Nonnull final String connectedEntityName) {

        return Usergrid.getInstance().disConnect(connectingEntityType, connectingEntityId, connectionType, connectedEntitytype, connectedEntityName);
    }

    public static UsergridResponse disConnect(@Nonnull final UsergridEntity sourceVertex,
                                              @Nonnull final String connetionName,
                                              @Nonnull final UsergridEntity targetVertex) {
        return Usergrid.getInstance().disConnect(sourceVertex, connetionName, targetVertex);
    }


}