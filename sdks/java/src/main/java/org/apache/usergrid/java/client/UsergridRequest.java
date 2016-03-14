package org.apache.usergrid.java.client;

import org.apache.usergrid.java.client.UsergridEnums.UsergridHttpMethod;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ApigeeCorporation on 9/2/15.
 */
public class UsergridRequest {

    public UsergridHttpMethod method;
    public String baseUrl;

    public UsergridQuery query;
    public UsergridAuth auth;

    public MediaType contentType;
    public String collection;
    public String name;
    public Map<String, Object> headers;
    public Map<String, Object> parameters;
    public Object data;
    public String[] segments;

    public UsergridRequest(final UsergridHttpMethod method, final MediaType contentType, final String url,
                           Map<String, Object> params, Object data,
                           Map<String, Object> headers, UsergridQuery ql, final String... segments) {
        this.method = method;
        this.contentType = contentType;
        this.baseUrl = url;
        this.parameters = params;
        this.data = data;
        this.headers = headers;
        this.query = ql;
        this.segments = segments;

    }

    public UsergridRequest(final UsergridHttpMethod method, final MediaType contentType, final String url,
                           Map<String, Object> params, Object data,
                           final String... segments) {
        this.method = method;
        this.contentType = contentType;
        this.baseUrl = url;
        this.parameters = params;
        this.data = data;
        this.headers = null;
        this.query = null;
        this.segments = segments;

    }

    public String buildURI() {
        return "";
    }

    public UsergridRequest collection(final String collection) {
        this.collection = collection;
        return this;
    }

    public UsergridRequest entity(final String name_uuid) {
        this.name = name_uuid;
        return this;
    }


    public UsergridRequest header(final String name, final String value) {

        if (this.headers == null) {
            this.headers = new HashMap<>(13);
        }

        this.headers.put(name, value);

        return this;
    }


    public UsergridResponse GET() {
        String uri = buildURI();
        return null;
    }

    public UsergridResponse GET(String uri) {
        return null;
    }
}
