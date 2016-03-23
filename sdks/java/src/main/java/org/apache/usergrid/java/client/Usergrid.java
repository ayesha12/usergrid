package org.apache.usergrid.java.client;

import com.sun.istack.internal.NotNull;
import org.apache.usergrid.java.client.UsergridEnums.UsergridAuthMode;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.model.UsergridUserAuth;
import org.apache.usergrid.java.client.query.LegacyQueryResult;
import org.apache.usergrid.java.client.query.QueryResult;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;

import javax.annotation.Nullable;
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
    public static UsergridClient initSharedInstance(@NotNull final String apiUrl, @NotNull final String orgName, @NotNull final String appName) {
        return Usergrid.initSharedInstance(new UsergridClientConfig(orgName, appName, apiUrl));
    }

    public static UsergridClient initSharedInstance(@NotNull final String apiUrl, @NotNull final String orgName, @NotNull final String appName, @NotNull final UsergridAuthMode authMode) {
        return Usergrid.initSharedInstance(new UsergridClientConfig(orgName, appName, apiUrl, authMode));
    }

    public static UsergridClient init(@NotNull final String apiUrl, @NotNull final String orgName, @NotNull final String appName) {
        return Usergrid.initSharedInstance(new UsergridClientConfig(orgName, appName, apiUrl));
    }

    /**
     * Instantiate a new instance of usergrid.
     *
     * @param ugConfig
     */
    public static UsergridClient initSharedInstance(@NotNull final UsergridClientConfig ugConfig) {
        if (isEmpty(ugConfig) || isEmpty(ugConfig.appId) || isEmpty(ugConfig.orgId) || isEmpty(ugConfig.baseUrl)) {
            throw new IllegalArgumentException("One of the input arguments is empty.");
        } else if (isInitialized()) {
            System.out.print("The Usergrid shared instance was already initialized. All subsequent initialization attempts (including this) will be ignored.");
        } else {
            sharedClient = new UsergridClient(ugConfig);
        }
        return sharedClient;
    }

    @NotNull
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

    public static UsergridResponse authorizeAppClient(@NotNull final String appClientId,
                                                      @NotNull final String appClientSecret) {

        UsergridAppAuth ugAppAuth = new UsergridAppAuth(appClientId, appClientSecret);
        return getInstance().authenticateApp(ugAppAuth);
    }

    public static UsergridResponse authorizeAppUser(@NotNull String username,@NotNull String password) {
        UsergridUserAuth ugUserAuth = new UsergridUserAuth(username, password);
        return getInstance().authenticateUser(ugUserAuth);

    }


    public static UsergridResponse authenticateApp(@NotNull final String clientId, @NotNull final String clientSecret) {
        return Usergrid.getInstance().authenticateApp(clientId, clientSecret);
    }

    public static UsergridResponse authenticateApp(@NotNull final UsergridAppAuth auth) {
        return Usergrid.getInstance().authenticateApp(auth);
    }

    public static UsergridResponse authenticateUser(@NotNull final String username, @NotNull final String password) {
        return Usergrid.getInstance().authenticateUser(username, password);
    }

    public static UsergridResponse authenticateUser(@NotNull final UsergridUserAuth auth) {
        return Usergrid.getInstance().authenticateUser(auth);
    }

    public static UsergridClient usingAuth(@NotNull UsergridAuth ugAuth) {
        return Usergrid.getInstance().usingAuth(ugAuth);
    }

    public static UsergridAuth authForRequests() {
        return Usergrid.getInstance().authForRequests();
    }

    public static RequestBuilder collection(@NotNull final String collection) {
        RequestBuilder builder = new RequestBuilder();
        builder.collection = collection;
        return builder;
    }

    public static UsergridResponse GET(@NotNull final String type,
                                       @NotNull final String uriSuffix) {
        return Usergrid.getInstance().GET(type, uriSuffix);
    }

    public static UsergridResponse GET(@NotNull final UUID uuid) {
        return Usergrid.getInstance().GET(uuid);
    }

    public static QueryResult GET(@NotNull final UsergridQuery q) {

        return Usergrid.getInstance().GET(q);
    }

    public static QueryResult GETFromQuery(@NotNull final UsergridQuery q) {
        return Usergrid.getInstance().GETFromQuery(q);
    }

    public static UsergridResponse POST(@NotNull final UsergridEntity e) {
        return Usergrid.getInstance().POST(e);
    }

    public static UsergridResponse POST(@NotNull final String type,
                                        @NotNull final String entityId) {
        return Usergrid.getInstance().POST(type, entityId);
    }

    public static UsergridResponse PUT(@NotNull final String type,
                                       @NotNull final String entityId) {
        return Usergrid.getInstance().PUT(type, entityId);
    }

    public static UsergridResponse PUT(@NotNull final UsergridEntity e) {
        return Usergrid.getInstance().PUT(e);
    }

    public static QueryResult PUT(@NotNull final UsergridQuery q,
                                  @NotNull final Map<String, Object> fields) {
        return Usergrid.getInstance().PUT(q, fields);
    }

    public static UsergridResponse DELETE(@NotNull final UsergridEntity e) {
        return Usergrid.getInstance().DELETE(e);
    }

    public static UsergridResponse DELETE(@NotNull final String collection,
                                          @NotNull final String entityId) {

        return Usergrid.getInstance().DELETE(collection, entityId);
    }

    public static UsergridResponse DELETE(@NotNull final UUID uuid) {
        return Usergrid.getInstance().DELETE(uuid);
    }

    public static QueryResult DELETE(@NotNull final UsergridQuery q) {
        return Usergrid.getInstance().DELETE(q);
    }

    public static UsergridResponse createEntity(@NotNull final UsergridEntity usergridEntity) {
        return Usergrid.getInstance().createEntity(usergridEntity);
    }

    public static UsergridResponse createEntity(@NotNull final Map<String, Object> properties) {
        return Usergrid.getInstance().createEntity(properties);
    }

    public static LegacyQueryResult queryUsersWithinLocation(@NotNull final float distance,
                                                             @NotNull final float lattitude,
                                                             @NotNull final float longitude,
                                                             @NotNull final String ql) {
        return Usergrid.getInstance().queryUsersWithinLocation(distance, lattitude, longitude, ql);
    }

    public static UsergridResponse getEntity(@NotNull final String type, @NotNull final String id) {
        return Usergrid.getInstance().getEntity(type, id);
    }

    public static UsergridResponse getConnections(@NotNull final Direction direction,
                                                  @NotNull final UsergridEntity sourceVertex,
                                                  @NotNull final String relationship) {
        return Usergrid.getInstance().getConnections(direction, sourceVertex, relationship);
    }

    public static UsergridResponse deleteEntity(@NotNull final String type,
                                                @NotNull final String id) {
        return Usergrid.getInstance().deleteEntity(type, id);
    }

    public static UsergridResponse connect(@NotNull final UsergridEntity sourceVertex,
                                           @NotNull final String connetionName,
                                           @NotNull final UsergridEntity targetVertex
    ) {
        return Usergrid.getInstance().connect(sourceVertex, connetionName, targetVertex);
    }


    public static UsergridResponse connect(@NotNull final UsergridEntity sourceVertex,
                                           @NotNull final String connetionName,
                                           @NotNull final String targetVertexUUid
    ) {
        return Usergrid.getInstance().connect(sourceVertex, connetionName, targetVertexUUid);
    }

    public static UsergridResponse connect(@NotNull final String connectingEntityType,
                                           @NotNull final String connectingEntityId,
                                           @NotNull final String connectionType,
                                           @NotNull final String connectedEntityId) {

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
    public static UsergridResponse connect(@NotNull final String connectingEntityType,
                                           @NotNull final String connectingEntityId,
                                           @NotNull final String connectionType,
                                           @NotNull final String connectedEntityType,
                                           @NotNull final String connectedEntityName) {

        return Usergrid.getInstance().connect(connectingEntityType, connectingEntityId, connectionType, connectedEntityType, connectedEntityName);
    }

    public static UsergridResponse disConnect(@NotNull final String connectingEntityType,
                                              @NotNull final String connectingEntityId,
                                              @NotNull final String connectionType,
                                              @NotNull final String connectedEntityId) {

        return disConnect(connectingEntityType, connectingEntityId, connectionType, connectedEntityId);
    }

    public static UsergridResponse disConnect(@NotNull final String connectingEntityType,
                                              @NotNull final String connectingEntityId,
                                              @NotNull final String connectionType,
                                              @NotNull final String connectedEntitytype,
                                              @NotNull final String connectedEntityName) {

        return Usergrid.getInstance().disConnect(connectingEntityType, connectingEntityId, connectionType, connectedEntitytype, connectedEntityName);
    }

    public static UsergridResponse disConnect(@NotNull final UsergridEntity sourceVertex,
                                              @NotNull final String connetionName,
                                              @NotNull final UsergridEntity targetVertex) {
        return Usergrid.getInstance().disConnect(sourceVertex, connetionName, targetVertex);
    }

    public static LegacyQueryResult queryEntityConnections(@NotNull final String connectingEntityType,
                                                           @NotNull final String connectingEntityId,
                                                           @NotNull final String connectionType, String ql) {

        return queryEntityConnections(connectingEntityType, connectingEntityId, connectionType, ql);
    }

    protected static String makeLocationQL(@NotNull float distance, @NotNull double lattitude,
                                           @NotNull double longitude, @Nullable String ql) {
        String within = String.format("within %d of %d , %d", distance, lattitude, longitude);
        ql = ql == null ? within : within + " and " + ql;

        return ql;
    }

    public static LegacyQueryResult queryEntityConnectionsWithinLocation(@NotNull final String connectingEntityType,
                                                                         @NotNull final String connectingEntityId,
                                                                         @NotNull final String connectionType,
                                                                         @NotNull final float distance,
                                                                         @NotNull float latitude,
                                                                         @NotNull final float longitude,
                                                                         @NotNull final String ql) {

        return Usergrid.getInstance().queryEntityConnectionsWithinLocation(connectingEntityType, connectingEntityId, connectionType, distance, latitude, longitude, ql);
    }

}