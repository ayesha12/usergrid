package org.apache.usergrid.java.client;

import org.apache.usergrid.java.client.UsergridEnums.UsergridHttpMethod;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridUserAuth;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.annotation.Nonnull;
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

    private static final String STR_BLANK = "";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String STRING_EXPIRES_IN = "expires_in";

    public UsergridClient client;
    private javax.ws.rs.client.Client restClient;

    public UsergridRequestManager(UsergridClient client) {
        this.client = client;
        this.restClient = ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .register(ProcessingException.class)
                .build();
    }

    public UsergridResponse performRequest(UsergridRequest request) {
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
        if (authForRequest != null && authForRequest.getAccessToken() != null) {
            String auth = BEARER + authForRequest.getAccessToken();
            invocationBuilder.header(HEADER_AUTHORIZATION, auth);
        }
        try {
            Response response;
            if (method == UsergridHttpMethod.POST || method == UsergridHttpMethod.PUT) {
                response = invocationBuilder.method(method.toString(),entity);
            } else {
                response = invocationBuilder.method(method.toString());
            }
            return UsergridResponse.fromResponse(request,response);
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


    public UsergridResponse authenticateApp(@Nonnull final UsergridAppAuth appAuth) {
        Map<String, Object> data = new HashMap<>();
        data.put("grant_type", "client_credentials");
        data.put("client_id", appAuth.getClientId());
        data.put("client_secret", appAuth.getClientSecret());

        String[] segments = {client.getOrgId(), client.getAppId(), "token"};
        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                client.getBaseUrl(), null, data, segments);
        UsergridResponse response = performRequest(request);
        if (response == null) {
            return null;
        }

        if (!isEmpty(response.getAccessToken())) {
            appAuth.setAccessToken(response.getAccessToken());
            long expiresIn = response.getProperties().get(STRING_EXPIRES_IN).asLong();
            appAuth.setExpiry(System.currentTimeMillis() + expiresIn - 5000);
        } else {
            throw new IllegalArgumentException("bad request : " + response.getResponseError().getErrorDescription()
                    + " status code : " + response.getStatusCode());
        }
        return response;
    }


    public UsergridResponse authenticateUser(@Nonnull UsergridUserAuth userAuth) {
        Map<String, Object> formData = new HashMap<>();
        formData.put("grant_type", "password");
        formData.put("username", userAuth.getUsername());
        formData.put("password", userAuth.getPassword());

        String[] segments = {client.getOrgId(), client.getAppId(), "token"};

        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                client.getBaseUrl(), null, formData, segments);
        UsergridResponse response = performRequest(request);
        if (response == null) {
            return null;
        }

        if (!isEmpty(response.getAccessToken()) && (response.currentUser() != null)) {
            userAuth.setAccessToken(response.getAccessToken());
            userAuth.setExpiry(System.currentTimeMillis() + response.getProperties().get(STRING_EXPIRES_IN).asLong() - 5000);
            response.currentUser().userAuth = userAuth;
        }
        else
        {
            throw new IllegalArgumentException("bad request " + response.getResponseError().getErrorDescription()
                    + " status code : " + response.getStatusCode());
        }
        return response;
    }
}
