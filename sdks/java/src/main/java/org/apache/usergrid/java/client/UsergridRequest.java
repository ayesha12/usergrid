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
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.MediaType;
import java.util.Map;

@SuppressWarnings("unused")
public class UsergridRequest {

    @NotNull private UsergridHttpMethod method;
    @NotNull private String baseUrl;
    @NotNull private MediaType contentType;

    @Nullable private UsergridQuery query;
    @Nullable private Map<String, Object> headers;
    @Nullable private Map<String, Object> parameters;
    @Nullable private Object data;
    @Nullable private String[] pathSegments;

    @NotNull
    public UsergridHttpMethod getMethod() { return method; }
    public void setMethod(@NotNull final UsergridHttpMethod method) { this.method = method; }

    @NotNull
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(@NotNull final String baseUrl) { this.baseUrl = baseUrl; }

    @NotNull
    public MediaType getContentType() { return contentType; }
    public void setContentType(@NotNull final MediaType contentType) { this.contentType = contentType; }

    @Nullable
    public UsergridQuery getQuery() { return query; }
    public void setQuery(@Nullable final UsergridQuery query) { this.query = query; }

    @Nullable
    public Map<String,Object> getHeaders() { return headers; }
    public void setHeaders(@Nullable final Map<String,Object> headers) { this.headers = headers; }

    @Nullable
    public Map<String,Object> getParameters() { return parameters; }
    public void setParameters(@Nullable final Map<String,Object> parameters) { this.parameters = parameters; }

    @Nullable
    public Object getData() { return data; }
    public void setData(@Nullable final Object data) { this.data = data; }

    @Nullable
    public String[] getPathSegments() { return pathSegments; }
    public void setPathSegments(@Nullable final String[] pathSegments) { this.pathSegments = pathSegments; }

    public UsergridRequest(@NotNull final UsergridHttpMethod method,
                           @NotNull final MediaType contentType,
                           @NotNull final String url,
                           @Nullable final UsergridQuery query,
                           @Nullable final String... pathSegments) {
        this.method = method;
        this.contentType = contentType;
        this.baseUrl = url;
        this.query = query;
        this.pathSegments = pathSegments;
    }

    public UsergridRequest(@NotNull final UsergridHttpMethod method,
                           @NotNull final MediaType contentType,
                           @NotNull final String url,
                           @Nullable final String... pathSegments) {
        this.method = method;
        this.contentType = contentType;
        this.baseUrl = url;
        this.pathSegments = pathSegments;
    }

    public UsergridRequest(@NotNull final UsergridHttpMethod method,
                           @NotNull final MediaType contentType,
                           @NotNull final String url,
                           @Nullable final Map<String, Object> params,
                           @Nullable final Object data,
                           @Nullable final String... pathSegments) {
        this.method = method;
        this.contentType = contentType;
        this.baseUrl = url;
        this.parameters = params;
        this.data = data;
        this.headers = null;
        this.query = null;
        this.pathSegments = pathSegments;
    }

    public UsergridRequest(@NotNull final UsergridHttpMethod method,
                           @NotNull final MediaType contentType,
                           @NotNull final String url,
                           @Nullable final Map<String, Object> params,
                           @Nullable final Object data,
                           @Nullable final Map<String, Object> headers,
                           @Nullable final UsergridQuery query,
                           @Nullable final String... pathSegments) {
        this.method = method;
        this.contentType = contentType;
        this.baseUrl = url;
        this.parameters = params;
        this.data = data;
        this.headers = headers;
        this.query = query;
        this.pathSegments = pathSegments;
    }
}
