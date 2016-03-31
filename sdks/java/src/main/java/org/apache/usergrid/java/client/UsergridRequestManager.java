package org.apache.usergrid.java.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.usergrid.java.client.UsergridEnums.UsergridHttpMethod;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.apache.usergrid.java.client.response.UsergridResponseError;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;

/**
 * Created by ApigeeCorporation on 9/2/15.
 */
public class UsergridRequestManager {

    public static final String STR_BLANK = "";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    private static final String STRING_EXPIRES_IN = "expires_in";
    public UsergridClient client;
    public javax.ws.rs.client.Client restClient;
    private static ObjectMapper _MAPPER = new ObjectMapper();
    private Response response;

    public UsergridRequestManager(UsergridClient client) {
        this.client = client;
        this.restClient = ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .register(ProcessingException.class)
//                .register(new ErrorResponseFilter())
                .build();
    }

    /**
     * High-level Usergrid API request.
     *
     * @param request : UsergridRequest object.
     * @return a UsergridResponse object
     */

    public UsergridResponse performRequest(UsergridRequest request) {
        UsergridResponse ugResponse = new UsergridResponse();
        UsergridHttpMethod method = request.method;
        MediaType contentType = request.contentType;
        Entity entity = Entity.entity(request.data == null ? STR_BLANK : request.data, contentType);

        // create the target from the base API URL
        String url = request.baseUrl;
        if (request.query != null) {
            url += request.query.build();
        }

        WebTarget webTarget = this.restClient.target(url);
        for (String segment : request.segments)
            if (segment != null)
                webTarget = webTarget.path(segment);

        if (!isEmpty(request.parameters)) {
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
            if (method == UsergridHttpMethod.POST || method == UsergridHttpMethod.PUT) {
                response = invocationBuilder.method(method.toString(),entity);
            } else {
                response = invocationBuilder.method(method.toString());
            }
            ugResponse = response.readEntity(UsergridResponse.class);
            ugResponse.setStatusIntCode(response.getStatus());

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                ugResponse.responseError = new UsergridResponseError(response.getStatusInfo().getReasonPhrase(),
                        response.getStatus(),
                        response.getStatusInfo().getFamily().toString(),
                        response.getStatusInfo().toString());
            }
            ugResponse.setOk(response.getStatusInfo().getReasonPhrase());
            ugResponse.headers = UsergridResponse.putMultivaluedMap(response.getHeaders());

            return ugResponse;

        } catch (Exception requestException) {
            return UsergridResponse.fromException(requestException);
        }
    }

    private void validateNonEmptyParam(final Object param,
                                       final String paramName) {
        if (isEmpty(param)) {
            throw new IllegalArgumentException(paramName + " cannot be null or empty");
        }
    }

    private void validateNotNull(final Object param,
                                       final String paramName) {
        if (isEmpty(param)) {
            throw new NullPointerException(paramName + " cannot be null or empty");
        }
    }


    public UsergridResponse AuthenticateApp() {

        validateNonEmptyParam(client.config.appAuth.clientId, "client identifier");
        validateNonEmptyParam(client.config.appAuth.clientSecret, "client secret");

        Map<String, Object> data = new HashMap<>();
        data.put("grant_type", "client_credentials");
        data.put("client_id", client.config.appAuth.clientId);
        data.put("client_secret", client.config.appAuth.clientSecret);
        validateNotNull(client.config.orgId,"org id");
        validateNotNull(client.config.appId,"app id");
        String[] segments = {client.config.orgId, client.config.appId, "token"};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                client.config.baseUrl, null, data, segments);
        UsergridResponse response = performRequest(request);
        if (response == null) {
            return null;
        }

        if (!isEmpty(response.getAccessToken())) {
            client.config.appAuth.setAccessToken(response.getAccessToken());
            client.config.appAuth.setTokenExpiry(System.currentTimeMillis() + response.getProperties().get(STRING_EXPIRES_IN).asLong() - 5000);
        }
        else
        {
            throw new IllegalArgumentException("bad request : " + response.responseError.getErrorDescription()
                    + " status code : " + response.getStatusIntCode());
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

        validateNotNull(client.config.orgId,"org id");
        validateNotNull(client.config.appId,"app id");

        String[] segments = {client.config.orgId, client.config.appId, "token"};

        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                client.config.baseUrl, null, formData, segments);
        UsergridResponse response = performRequest(request);
        if (response == null) {
            return null;
        }

        if (!isEmpty(response.getAccessToken()) && (response.currentUser() != null)) {
            client.config.userAuth.setAccessToken(response.getAccessToken());
            client.config.userAuth.setTokenExpiry(System.currentTimeMillis() + response.getProperties().get(STRING_EXPIRES_IN).asLong() - 5000);
            response.currentUser().userAuth = client.config.userAuth;
            client.setCurrentUser(response.currentUser());
        }
        else
        {
            throw new IllegalArgumentException("bad request " + response.responseError.getErrorDescription()
                    + " status code : " + response.getStatusIntCode());
        }
        return response;
    }
}
