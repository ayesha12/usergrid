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

import java.util.Map;
import java.util.UUID;

import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;


/**
 * Created by Jeff West on 9/2/15.
 */
public final class Usergrid {

    private static UsergridClient sharedClient;

    public static boolean isInitialized() { return (sharedClient != null); }
    public static void reset() {
        sharedClient = null;
    }

    public static UsergridClient getInstance() {
        if( !Usergrid.isInitialized() ) {
            throw new NullPointerException("Shared client has not been initialized!");
        }
        return Usergrid.sharedClient;
    }

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
    public static UsergridClient initSharedInstance(final String apiUrl, final String orgName, final String appName) {
        return Usergrid.initSharedInstance(new UsergridClientConfig(orgName, appName, apiUrl));
    }

    public static UsergridClient initSharedInstance(final String apiUrl, final String orgName, final String appName, final UsergridAuthMode authMode ) {
        return Usergrid.initSharedInstance(new UsergridClientConfig(orgName, appName, apiUrl, authMode));
    }

    public static UsergridClient init(final String apiUrl, final String orgName, final String appName) {
        return Usergrid.initSharedInstance(new UsergridClientConfig(orgName, appName, apiUrl));
    }

    /**
     * Instantiate a new instance of usergrid.
     *
     * @param ugConfig
     */
    public static UsergridClient initSharedInstance(final UsergridClientConfig ugConfig) {
        if (isEmpty(ugConfig) || isEmpty(ugConfig.appId) || isEmpty(ugConfig.orgId) || isEmpty(ugConfig.baseUrl)) {
            throw new IllegalArgumentException("One of the input arguments is empty.");
        } else if( isInitialized() ){
            System.out.print("The Usergrid shared instance was already initialized. All subsequent initialization attempts (including this) will be ignored.");
        } else {
            sharedClient = new UsergridClient(ugConfig);
        }
        return sharedClient;
    }

    public static RequestBuilder collection(final String collection) {
        RequestBuilder builder = new RequestBuilder();
        builder.collection = collection;
        return builder;
    }

    public static UsergridResponse authorizeAppClient(final String appClientId,
                                                      final String appClientSecret) {

        UsergridAppAuth ugAppAuth = new UsergridAppAuth(appClientId, appClientSecret);
        return getInstance().authenticateApp(ugAppAuth);
    }

    public static UsergridResponse authorizeAppClient(@NotNull UsergridClient client, final String appClientId,
                                                      final String appClientSecret) {

        UsergridAppAuth ugAppAuth = new UsergridAppAuth(appClientId, appClientSecret);
        return client.authenticateApp(ugAppAuth);
    }

    public static UsergridResponse authorizeAppUser(String username, String password) {
        UsergridUserAuth ugUserAuth = new UsergridUserAuth(username, password);
        return getInstance().authenticateUser(ugUserAuth);

    }

    public static UsergridClient usingAuth(UsergridAuth ugAuth) {
        return Usergrid.getInstance().usingAuth(ugAuth);
    }

    public static UsergridClient usingAuth(UsergridClient client,UsergridAuth ugAuth) {
        return client.usingAuth(ugAuth);
    }

    public static UsergridAuth authForRequests() {
        return Usergrid.getInstance().authForRequests();
    }

    public static UsergridResponse authenticateApp(final String clientId, final String clientSecret) {
        return Usergrid.getInstance().authenticateApp(clientId, clientSecret);
    }

    public static UsergridResponse authenticateApp(final UsergridAppAuth auth) {
        return Usergrid.getInstance().authenticateApp(auth);
    }

    public static UsergridResponse authenticateUser(String username, String password) {
        return Usergrid.getInstance().authenticateUser(username, password);
    }

    public static UsergridResponse authenticateUser(UsergridUserAuth auth) {
        return Usergrid.getInstance().authenticateUser(auth);
    }


    public static UsergridResponse GET(@NotNull final String type,
                                       final String uriSuffix) {
        return Usergrid.getInstance().GET(type, uriSuffix);
    }

    public static UsergridResponse GET(@NotNull UsergridClient client,@NotNull final String type,
                                       final String uriSuffix) {
        return client.GET(type, uriSuffix);
    }

    public static UsergridResponse GET(final UUID uuid) {
        return Usergrid.getInstance().GET(uuid);
    }

    public static UsergridResponse GET(@NotNull UsergridClient client, final UUID uuid) {
        return client.GET(uuid);
    }

    public static QueryResult GET(final UsergridQuery q) {

        return Usergrid.getInstance().GET(q);
    }

    public static QueryResult GET(UsergridClient client,final UsergridQuery q) {
        return client.GET(q);
    }
    public static QueryResult GETFromQuery(final UsergridQuery q) {
        return Usergrid.getInstance().GETFromQuery(q);
    }

    public static QueryResult GETFromQuery(UsergridClient client,final UsergridQuery q) {
        return client.GETFromQuery(q);
    }

    public static UsergridResponse POST(final UsergridEntity e) {
        return Usergrid.getInstance().POST(e);
    }

    public static UsergridResponse POST(UsergridClient client,final UsergridEntity e) {
        return client.POST(e);
    }

    public static UsergridResponse POST(final String type,
                                        final String entityId) {
        return Usergrid.getInstance().POST(type, entityId);
    }

    public static UsergridResponse POST(UsergridClient client,final String type,
                                        final String entityId) {
        return client.POST(type, entityId);
    }

    public static UsergridResponse PUT(final String type,
                                       final String entityId) {
        return Usergrid.getInstance().PUT(type, entityId);
    }

    public static UsergridResponse PUT(UsergridClient client,final String type,
                                       final String entityId) {
        return client.PUT(type, entityId);
    }

    public static QueryResult PUT(final UsergridQuery q, Map<String, Object> fields) {

        return Usergrid.getInstance().PUT(q, fields);
    }

    public static UsergridResponse DELETE(final UsergridEntity e) {
        return Usergrid.getInstance().DELETE(e);
    }

    public static UsergridResponse DELETE(UsergridClient client,final UsergridEntity e) {
        return client.DELETE(e);
    }

    public static UsergridResponse DELETE(final String collection,
                                          final String entityId) {

        return Usergrid.getInstance().DELETE(collection, entityId);
    }

    public static UsergridResponse DELETE(UsergridClient client, final String collection,
                                          final String entityId) {

        return client.DELETE(collection, entityId);
    }

    public static UsergridResponse DELETE(final UUID uuid) {
        return Usergrid.getInstance().DELETE(uuid);
    }

    public static UsergridResponse DELETE(UsergridClient client, final UUID uuid) {
        return client.DELETE(uuid);
    }

    public static QueryResult DELETE(final UsergridQuery q) {
        return Usergrid.getInstance().DELETE(q);
    }

    public static QueryResult DELETE(UsergridClient client,final UsergridQuery q) {
        return client.DELETE(q);
    }

    public static UsergridResponse createEntity(final UsergridEntity usergridEntity) {
        return Usergrid.getInstance().createEntity(usergridEntity);
    }

    public static UsergridResponse createEntity(UsergridClient client,final UsergridEntity usergridEntity) {
        return client.createEntity(usergridEntity);
    }

    public static UsergridResponse createEntity(Map<String, Object> properties) {
        return Usergrid.getInstance().createEntity(properties);
    }

    public static UsergridResponse createEntity(UsergridClient client,Map<String, Object> properties) {
        return client.createEntity(properties);
    }

    public static LegacyQueryResult queryUsers() {
        return Usergrid.getInstance().queryUsers();
    }

    public static LegacyQueryResult queryUsers(UsergridClient client) {
        return client.queryUsers();
    }

    public static LegacyQueryResult queryUsers(String ql) {
        return Usergrid.getInstance().queryUsers(ql);
    }

    public static LegacyQueryResult queryUsers(UsergridClient client,String ql) {
        return Usergrid.getInstance().queryUsers(ql);
    }

    public static LegacyQueryResult queryUsersWithinLocation(final float distance,
                                                             final float lattitude,
                                                             final float longitude,
                                                             final String ql) {
        return Usergrid.getInstance().queryUsersWithinLocation(distance, lattitude, longitude, ql);
    }

    public static LegacyQueryResult queryUsersWithinLocation(UsergridClient client,final float distance,
                                                             final float lattitude,
                                                             final float longitude,
                                                             final String ql) {
        return client.queryUsersWithinLocation(distance, lattitude, longitude, ql);
    }

    public static UsergridResponse getEntity(final String type, final String id) {
        return Usergrid.getInstance().getEntity(type, id);
    }

    public static UsergridResponse getEntity(UsergridClient client,final String type, final String id) {
        return client.getEntity(type, id);
    }

    public static UsergridResponse getConnections(Direction direction, UsergridEntity sourceVertex, String relationship) {
        return Usergrid.getInstance().getConnections(direction, sourceVertex, relationship);
    }

    public static UsergridResponse getConnections(UsergridClient client,Direction direction, UsergridEntity sourceVertex, String relationship) {
        return client.getConnections(direction, sourceVertex, relationship);
    }

    public static UsergridResponse deleteEntity(final String type,
                                                final String id) {
        return Usergrid.getInstance().deleteEntity(type, id);
    }

    public static UsergridResponse deleteEntity(UsergridClient client,final String type,
                                                final String id) {
        return client.deleteEntity(type, id);
    }

    public static UsergridResponse connect(final UsergridEntity sourceVertex,
                                           final String connetionName,
                                           final UsergridEntity targetVertex
    ) {
        return Usergrid.getInstance().connect(sourceVertex, connetionName, targetVertex);
    }

    public static UsergridResponse connect(UsergridClient client,final UsergridEntity sourceVertex,
                                           final String connetionName,
                                           final UsergridEntity targetVertex
    ) {
        return client.connect(sourceVertex, connetionName, targetVertex);
    }

    public static UsergridResponse connect(final UsergridEntity sourceVertex,
                                           final String connetionName,
                                           final String targetVertexUUid
    ) {
        return Usergrid.getInstance().connect(sourceVertex, connetionName, targetVertexUUid);
    }

    public static UsergridResponse connect(UsergridClient client,final UsergridEntity sourceVertex,
                                           final String connetionName,
                                           final String targetVertexUUid
    ) {
        return client.connect(sourceVertex, connetionName, targetVertexUUid);
    }


    public static UsergridResponse connect(final String connectingEntityType,
                                           final String connectingEntityId,
                                           final String connectionType,
                                           final String connectedEntityId) {

        return Usergrid.getInstance().connect(connectingEntityType, connectingEntityId, connectionType, connectedEntityId);
    }

    public static UsergridResponse connect(UsergridClient client,final String connectingEntityType,
                                           final String connectingEntityId,
                                           final String connectionType,
                                           final String connectedEntityId) {

        return client.connect(connectingEntityType, connectingEntityId, connectionType, connectedEntityId);
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
    public static UsergridResponse connect(final String connectingEntityType,
                                           final String connectingEntityId,
                                           final String connectionType,
                                           final String connectedEntityType,
                                           final String connectedEntityName) {

        return Usergrid.getInstance().connect(connectingEntityType, connectingEntityId, connectionType, connectedEntityType, connectedEntityName);
    }

    public static UsergridResponse connect(UsergridClient client,final String connectingEntityType,
                                           final String connectingEntityId,
                                           final String connectionType,
                                           final String connectedEntityType,
                                           final String connectedEntityName) {

        return client.connect(connectingEntityType, connectingEntityId, connectionType, connectedEntityType, connectedEntityName);
    }

    public static UsergridResponse disconnect(final String connectingEntityType,
                                              final String connectingEntityId,
                                              final String connectionType,
                                              final String connectedEntityId) {

        return disconnect(connectingEntityType, connectingEntityId, connectionType, connectedEntityId);
    }

    public static UsergridResponse disconnect(final String connectingEntityType,
                                              final String connectingEntityId,
                                              final String connectionType,
                                              final String connectedEntitytype,
                                              final String connectedEntityName) {

        return Usergrid.getInstance().disconnect(connectingEntityType, connectingEntityId, connectionType, connectedEntitytype, connectedEntityName);
    }

    public static UsergridResponse disconnect(UsergridClient client,final String connectingEntityType,
                                              final String connectingEntityId,
                                              final String connectionType,
                                              final String connectedEntitytype,
                                              final String connectedEntityName) {

        return client.disconnect(connectingEntityType, connectingEntityId, connectionType, connectedEntitytype, connectedEntityName);
    }


    public static UsergridResponse disconnect(final UsergridEntity sourceVertex,
                                              final String connetionName,
                                              final UsergridEntity targetVertex) {
        return Usergrid.getInstance().disconnect(sourceVertex, connetionName, targetVertex);
    }

    public static UsergridResponse disconnect(UsergridClient client,final UsergridEntity sourceVertex,
                                              final String connetionName,
                                              final UsergridEntity targetVertex) {
        return client.disconnect(sourceVertex, connetionName, targetVertex);
    }

    public static LegacyQueryResult queryEntityConnections(final String connectingEntityType,
                                                           final String connectingEntityId,
                                                           final String connectionType, String ql) {

        return queryEntityConnections(connectingEntityType, connectingEntityId, connectionType, ql);
    }

    protected static String makeLocationQL(float distance, double lattitude,
                                           double longitude, String ql) {
        String within = String.format("within %d of %d , %d", distance, lattitude, longitude);
        ql = ql == null ? within : within + " and " + ql;

        return ql;
    }

    public static LegacyQueryResult queryEntityConnectionsWithinLocation(final String connectingEntityType,
                                                                         final String connectingEntityId,
                                                                         final String connectionType,
                                                                         final float distance,
                                                                         float latitude,
                                                                         final float longitude,
                                                                         final String ql) {

        return Usergrid.getInstance().queryEntityConnectionsWithinLocation(connectingEntityType, connectingEntityId, connectionType, distance, latitude, longitude, ql);
    }

    public static UsergridResponse queryCollections() {
        return Usergrid.getInstance().queryCollections();
    }

    public static UsergridResponse queryConnection(final String... segments) {
        return Usergrid.getInstance().queryConnection(segments);
    }


}