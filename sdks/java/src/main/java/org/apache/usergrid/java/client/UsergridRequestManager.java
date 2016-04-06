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

import org.apache.usergrid.java.client.UsergridEnums.UsergridHttpMethod;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridAuth;
import org.apache.usergrid.java.client.model.UsergridUser;
import org.apache.usergrid.java.client.model.UsergridUserAuth;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;

public class UsergridRequestManager {

    public final UsergridClient client;
    private final javax.ws.rs.client.Client restClient;

    public UsergridRequestManager(@NotNull final UsergridClient client) {
        this.client = client;
        this.restClient = ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .register(ProcessingException.class)
                .build();
    }

    @NotNull
    public UsergridResponse performRequest(@NotNull final UsergridRequest request) {
        UsergridHttpMethod method = request.getMethod();
        MediaType contentType = request.getContentType();
        Entity entity = Entity.entity(request.getData() == null ? "" : request.getData(), contentType);

        String url = request.getBaseUrl();
        if (request.getQuery() != null) {
            url += request.getQuery().build();
        }

        WebTarget webTarget = this.restClient.target(url);
        if( request.getSegments() != null ) {
            for (String segment : request.getSegments()) {
                webTarget = webTarget.path(segment);
            }
        }

        if (!isEmpty(request.getParameters())) {
            for (Map.Entry<String, Object> param : request.getParameters().entrySet()) {
                webTarget = webTarget.queryParam(param.getKey(), param.getValue());
            }
        }

        Invocation.Builder invocationBuilder = webTarget.request(contentType);
        UsergridAuth authForRequest = client.authForRequests();
        if (authForRequest != null && authForRequest.getAccessToken() != null) {
            String auth = "Bearer " + authForRequest.getAccessToken();
            invocationBuilder.header("Authorization", auth);
        }
        UsergridResponse usergridResponse;
        try {
            Response response;
            if (method == UsergridHttpMethod.POST || method == UsergridHttpMethod.PUT) {
                response = invocationBuilder.method(method.toString(),entity);
            } else {
                response = invocationBuilder.method(method.toString());
            }
            usergridResponse = UsergridResponse.fromResponse(request,response);
        } catch (Exception requestException) {
            usergridResponse = UsergridResponse.fromException(requestException);
        }
        usergridResponse.setClient(this.client);
        return usergridResponse;
    }

    @NotNull
    public UsergridResponse authenticateApp(@NotNull final UsergridAppAuth appAuth) {
        Map<String, Object> data = new HashMap<>();
        data.put("grant_type", "client_credentials");
        data.put("client_id", appAuth.getClientId());
        data.put("client_secret", appAuth.getClientSecret());

        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE, client.clientAppUrl(), null, data, "token");
        UsergridResponse response = performRequest(request);

        if (!isEmpty(response.getAccessToken()) && !isEmpty(response.getExpires())) {
            appAuth.setAccessToken(response.getAccessToken());
            long expiresIn = response.getExpires();
            appAuth.setExpiry(System.currentTimeMillis() + expiresIn - 5000);
        }
        return response;
    }

    @NotNull
    public UsergridResponse authenticateUser(@NotNull final UsergridUserAuth userAuth) {
        Map<String, Object> formData = new HashMap<>();
        formData.put("grant_type", "password");
        formData.put("username", userAuth.getUsername());
        formData.put("password", userAuth.getPassword());

        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE, client.clientAppUrl(), null, formData, "token");
        UsergridResponse response = performRequest(request);
        UsergridUser responseUser = response.user();
        if (!isEmpty(response.getAccessToken()) && !isEmpty(response.getExpires()) && responseUser != null) {
            userAuth.setAccessToken(response.getAccessToken());
            long expiresIn = response.getExpires();
            userAuth.setExpiry(System.currentTimeMillis() + expiresIn - 5000);
            responseUser.userAuth = userAuth;
        }
        return response;
    }
}
