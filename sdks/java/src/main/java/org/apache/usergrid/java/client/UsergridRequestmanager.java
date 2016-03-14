package org.apache.usergrid.java.client;

import org.apache.usergrid.java.client.response.UsergridResponse;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.usergrid.java.client.UsergridEnums.UsergridHttpMethod;

import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;

/**
 * Created by ApigeeCorporation on 9/2/15.
 */
public class UsergridRequestmanager {
    static UsergridClient client;
    public static final String STR_BLANK = "";
    private javax.ws.rs.client.Client restClient;

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_GET = "GET";
    public static final String HTTP_DELETE = "DELETE";
    private static final String STRING_EXPIRES_IN = "expires_in";
    public UsergridRequestmanager(UsergridClient client){
        this.client = client;
    }


    public static void ValidAppArguments() {
       if(isEmpty(client)){
           throw new IllegalArgumentException("No client specified");
       }
        if (isEmpty(client.config.appId)) {
            throw new IllegalArgumentException("No application id specified");
        }
        if (isEmpty(client.config.orgId)) {
            throw new IllegalArgumentException("No organization id specified");
        }
    }


    /**
     * High-level Usergrid API request.
     * @param request : UsergridRequest object.
     * @return a UsergridResponse object
     */

    public static UsergridResponse performRequest(UsergridRequest request){
        ValidAppArguments();

        UsergridHttpMethod method = request.method;
        MediaType contentType = request.contentType;
        Entity entity = Entity.entity(request.data == null ? STR_BLANK : request.data, contentType);

        // create the target from the base API URL
        WebTarget webTarget = client.restClient.target(request.baseUrl);
        for (String segment : request.segments)
            if (segment != null)
                webTarget = webTarget.path(segment);

        if ((method.toString().equals(HTTP_GET) || method.toString().equals(HTTP_PUT)
                || method.toString().equals(HTTP_POST) || method.toString().equals(HTTP_DELETE)) && !isEmpty(request.parameters)) {
            for (Map.Entry<String, Object> param : request.parameters.entrySet()) {
                webTarget = webTarget.queryParam(param.getKey(), param.getValue());
            }
        }

        System.out.println(webTarget);
        Invocation.Builder invocationBuilder = webTarget.request(contentType);

        UsergridAuth authForRequest = client.authForRequests();
        if (authForRequest != null && authForRequest.accessToken != null) {
            String auth = BEARER + authForRequest.accessToken;
            invocationBuilder.header(HEADER_AUTHORIZATION, auth);
        }

        try {
            if (Objects.equals(method.toString(), HTTP_POST) || Objects.equals(method.toString(), HTTP_PUT)) {
                UsergridResponse response = invocationBuilder.method(method.toString(), entity, UsergridResponse.class);
                return response;
            } else {
                return invocationBuilder.method(method.toString(), null, UsergridResponse.class);
            }
        } catch (Exception badRequestException) {
            return UsergridResponse.fromException(badRequestException);
        }
    }

    private void validateNonEmptyParam(final Object param,
                                       final String paramName) {
        if (isEmpty(param)) {
            throw new IllegalArgumentException(paramName + " cannot be null or empty");
        }
    }



    public UsergridResponse AuthenticateApp() {

        validateNonEmptyParam(client.config.appAuth.clientId, "client identifier");
        validateNonEmptyParam(client.config.appAuth.clientSecret, "client secret");

        Map<String, Object> data = new HashMap<>();
        data.put("grant_type", "client_credentials");
        data.put("client_id", client.config.appAuth.clientId);
        data.put("client_secret", client.config.appAuth.clientSecret);
        String[] segments = {client.config.orgId, client.config.appId, "token"};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST,MediaType.APPLICATION_JSON_TYPE,
                client.config.baseUrl,null,data,segments);
        UsergridResponse response = performRequest(request);
        if (response == null) {
            return null;
        }

        if (!isEmpty(response.getAccessToken())) {
            client.config.appAuth.setAccessToken(response.getAccessToken());
            client.config.appAuth.setTokenExpiry(response.getProperties().get(STRING_EXPIRES_IN).asLong() - 5);
        }
        return response;
    }


    public UsergridResponse AuthenticateUser() {

        validateNonEmptyParam(client.config.userAuth.username, "username");
        validateNonEmptyParam(client.config.userAuth.password, "password");

        Map<String, Object> formData = new HashMap<>();
        formData.put("grant_type", "password");
        formData.put("username", client.config.userAuth.username);
        formData.put("password", client.config.userAuth.password);

        String[] segments = {client.config.orgId, client.config.appId, "token"};

        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST,MediaType.APPLICATION_JSON_TYPE,
                client.config.baseUrl,null,formData,segments);
        UsergridResponse response = performRequest(request);
        if (response == null) {
            return null;
        }

        if (!isEmpty(response.getAccessToken()) && (response.currentUser() != null)) {
            client.config.userAuth.setAccessToken(response.getAccessToken());
            client.config.userAuth.setTokenExpiry(response.getProperties().get(STRING_EXPIRES_IN).asLong() - 5);
            response.currentUser().userAuth = client.config.userAuth;
            client.setCurrentUser(response.currentUser());
        }
        return response;
    }
}
