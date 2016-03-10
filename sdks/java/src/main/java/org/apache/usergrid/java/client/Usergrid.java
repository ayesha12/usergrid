package org.apache.usergrid.java.client;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.model.UsergridUserAuth;
import org.apache.usergrid.java.client.query.LegacyQueryResult;
import org.apache.usergrid.java.client.query.QueryResult;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;

import javax.jws.soap.SOAPBinding;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;


/**
 * Created by Jeff West on 9/2/15.
 */
public class Usergrid {

    private static final Map<String, UsergridClient> instances_;
    public static final String STR_DEFAULT = "default";
    private static boolean initialized = false;

    static {
        instances_ = new HashMap<>(5);
        instances_.put(STR_DEFAULT, new UsergridClient());
    }

    public static UsergridClient getInstance() {
        return getInstance(STR_DEFAULT);
    }

    public static UsergridClient getInstance(final String id) {

        UsergridClient client = instances_.get(id);

        if (client == null) {
            client = new UsergridClient();
            instances_.put(id, client);
        }

        return client;
    }

    /**
     * Instantiate a new instance of usergrid.
     *
     * @param apiUrl
     * @param orgName
     * @param appName
     */
    public static void initSharedInstance(final String apiUrl, final String orgName, final String appName) {

        if (isEmpty(appName) || isEmpty(orgName) || isEmpty(apiUrl)) {
            throw new IllegalArgumentException("One of the input arguments is empty.");
        } else {
            UsergridClient client = getInstance(STR_DEFAULT);
            UsergridClientConfig conf = new UsergridClientConfig(orgName, appName, apiUrl, null, null);
            client.config = conf;
            initialized = true;
        }

    }


    /**
     * Alias to Usergrid.initSharedInstance.
     *
     * @param apiUrl
     * @param orgName
     * @param appName
     */
    public static void init(final String apiUrl, final String orgName, final String appName) {
        initSharedInstance(apiUrl, orgName, appName);
    }

    /**
     * Returns true if the Usergrid singleton has already been initialized.
     *
     * @return
     */

    public static boolean isInitialized() {
        return initialized;
    }

    public static RequestBuilder collection(final String collection) {
        RequestBuilder builder = new RequestBuilder();
        builder.collection = collection;
        return builder;
    }

    public static UsergridResponse GET(final String type,
                                       final String uriSuffix) {
        return Usergrid.getInstance().GET(type, uriSuffix);
    }

    public static UsergridResponse authorizeAppClient(final String appClientId,
                                                      final String appClientSecret) {

        UsergridAppAuth ugAppAuth = new UsergridAppAuth(appClientId, appClientSecret);
        return getInstance().authenticateApp(ugAppAuth);
    }

    public static UsergridResponse authorizeAppUser(String username, String password) {
        UsergridUserAuth ugUserAuth = new UsergridUserAuth(username, password);
        return getInstance().authenticateUser(ugUserAuth);

    }

    public static void reset() {
        initialized = false;
        instances_.put(STR_DEFAULT, new UsergridClient());
    }

    public UsergridClient usingAuth(UsergridAuth ugAuth) {
        return Usergrid.getInstance().usingAuth(ugAuth);
    }

    public UsergridAuth authForRequests() {
        return Usergrid.getInstance().authForRequests();
    }

    public UsergridResponse authenticateApp(final String clientId, final String clientSecret) {
        return Usergrid.getInstance().authenticateApp(clientId, clientSecret);
    }

    public UsergridResponse authenticateApp(final UsergridAppAuth auth) {
        return Usergrid.getInstance().authenticateApp(auth);
    }

    public UsergridResponse authenticateUser(String username, String password) {
        return Usergrid.getInstance().authenticateUser(username, password);
    }

    public UsergridResponse authenticateUser(UsergridUserAuth auth) {
        return Usergrid.getInstance().authenticateUser(auth);
    }

    public UsergridResponse createEntity(final UsergridEntity usergridEntity) {
        return Usergrid.getInstance().createEntity(usergridEntity);
    }

    public UsergridResponse createEntity(Map<String, Object> properties) {
        return Usergrid.getInstance().createEntity(properties);
    }


    public LegacyQueryResult queryEntities(final String method,
                                           final Map<String, Object> params,
                                           final Object data,
                                           final String... segments) {

        return Usergrid.getInstance().queryEntities(method, params, data, segments);
    }

    public LegacyQueryResult queryUsers() {
        return Usergrid.getInstance().queryUsers();
    }


    public LegacyQueryResult queryUsers(String ql) {
        return Usergrid.getInstance().queryUsers(ql);
    }


    public LegacyQueryResult queryUsersWithinLocation(final float distance,
                                                      final float lattitude,
                                                      final float longitude,
                                                      final String ql) {
        return Usergrid.getInstance().queryUsersWithinLocation(distance, lattitude, longitude, ql);
    }

    public UsergridResponse getEntity(final String type, final String id) {
        return Usergrid.getInstance().getEntity(type, id);
    }

    public UsergridResponse getConnections(Direction direction, UsergridEntity sourceVertex, String relationship) {
        return Usergrid.getInstance().getConnections(direction, sourceVertex, relationship);
    }

    public UsergridResponse deleteEntity(final String type,
                                         final String id) {
        return Usergrid.getInstance().deleteEntity(type, id);
    }

    public UsergridResponse connect(final UsergridEntity sourceVertex,
                                    final String connetionName,
                                    final UsergridEntity targetVertex
    ) {
        return Usergrid.getInstance().connect(sourceVertex, connetionName, targetVertex);
    }

    public UsergridResponse connect(final UsergridEntity sourceVertex,
                                    final String connetionName,
                                    final String targetVertexUUid
    ) {
        return Usergrid.getInstance().connect(sourceVertex, connetionName, targetVertexUUid);
    }


    public UsergridResponse connect(final String connectingEntityType,
                                    final String connectingEntityId,
                                    final String connectionType,
                                    final String connectedEntityId) {

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
    public UsergridResponse connect(final String connectingEntityType,
                                    final String connectingEntityId,
                                    final String connectionType,
                                    final String connectedEntityType,
                                    final String connectedEntityName) {

        return Usergrid.getInstance().connect(connectingEntityType, connectingEntityId, connectionType, connectedEntityType, connectedEntityName);
    }

    public UsergridResponse disconnect(final String connectingEntityType,
                                       final String connectingEntityId,
                                       final String connectionType,
                                       final String connectedEntityId) {

        return disconnect(connectingEntityType, connectingEntityId, connectionType, connectedEntityId);
    }

    public UsergridResponse disconnect(final String connectingEntityType,
                                       final String connectingEntityId,
                                       final String connectionType,
                                       final String connectedEntitytype,
                                       final String connectedEntityName) {

        return Usergrid.getInstance().disconnect(connectingEntityType, connectingEntityId, connectionType, connectedEntitytype, connectedEntityName);
    }


    public UsergridResponse disconnect(final UsergridEntity sourceVertex,
                                       final String connetionName,
                                       final UsergridEntity targetVertex) {
        return Usergrid.getInstance().disconnect(sourceVertex, connetionName, targetVertex);
    }


    public LegacyQueryResult queryEntityConnections(final String connectingEntityType,
                                                    final String connectingEntityId,
                                                    final String connectionType, String ql) {

        return queryEntityConnections(connectingEntityType, connectingEntityId, connectionType, ql);
    }

    protected String makeLocationQL(float distance, double lattitude,
                                    double longitude, String ql) {
        String within = String.format("within %d of %d , %d", distance, lattitude, longitude);
        ql = ql == null ? within : within + " and " + ql;

        return ql;
    }

    public LegacyQueryResult queryEntityConnectionsWithinLocation(final String connectingEntityType,
                                                                  final String connectingEntityId,
                                                                  final String connectionType,
                                                                  final float distance,
                                                                  float latitude,
                                                                  final float longitude,
                                                                  final String ql) {

        return Usergrid.getInstance().queryEntityConnectionsWithinLocation(connectingEntityType, connectingEntityId, connectionType, distance, latitude, longitude, ql);
    }


    public UsergridResponse queryEdgesForVertex(final String srcType,
                                                final String srcID) {

        return Usergrid.getInstance().queryEdgesForVertex(srcType, srcID);
    }

    public UsergridResponse queryCollections() {
        return Usergrid.getInstance().queryCollections();
    }

    public UsergridResponse queryConnection(final String... segments) {
        return Usergrid.getInstance().queryConnection(segments);
    }

    public UsergridResponse PUT(final String type,
                                final String entityId) {
        return Usergrid.getInstance().PUT(type, entityId);
    }

    public UsergridResponse POST(final UsergridEntity e) {
        return Usergrid.getInstance().POST(e);
    }

    public UsergridResponse POST(final String type,
                                 final String entityId) {
        return Usergrid.getInstance().POST(type, entityId);
    }


    public UsergridResponse DELETE(final UsergridEntity e) {
        return Usergrid.getInstance().DELETE(e);
    }

    public UsergridResponse DELETE(final String collection,
                                   final String entityId) {

        return Usergrid.getInstance().DELETE(collection, entityId);
    }

    public UsergridResponse DELETE(final UUID uuid) {
        return Usergrid.getInstance().DELETE(uuid);
    }

    public UsergridResponse GET(final UUID uuid) {
        return Usergrid.getInstance().GET(uuid);
    }

    public QueryResult PUT(final UsergridQuery q, Map<String, Object> fields) {

        return Usergrid.getInstance().PUT(q, fields);
    }

    public QueryResult GET(final UsergridQuery q) {

        return Usergrid.getInstance().GET(q);
    }

    public QueryResult GETFromQuery(final UsergridQuery q) {

        return Usergrid.getInstance().GETFromQuery(q);
    }

    public QueryResult DELETE(final UsergridQuery q) {

        return Usergrid.getInstance().DELETE(q);
    }
}