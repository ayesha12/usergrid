package org.apache.usergrid.java.client;

import org.apache.usergrid.java.client.response.UsergridResponse;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ApigeeCorporation on 9/2/15.
 */
public class RequestBuilder {

    public String collection;
    public String name;
    public Map<String, String> headers;
    public Map<String, String> parameters;
    public String uriSuffix;

    public String buildURI() {
        return "";
    }

    public RequestBuilder collection(@Nonnull final String collection) {
        this.collection = collection;
        return this;
    }

    public RequestBuilder entity(@Nonnull final String name_uuid) {
        this.name = name_uuid;
        return this;
    }

    public RequestBuilder parameter(@Nonnull final String name, @Nonnull final String value) {

        return this;
    }

    public RequestBuilder header(@Nonnull final String name, @Nonnull final String value) {

        if (this.headers == null) {
            this.headers = new HashMap<>(13);
        }

        this.headers.put(name, value);

        return this;
    }

    public RequestBuilder headers(@Nonnull final Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public RequestBuilder parameters(@Nonnull final Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }


    public UsergridResponse GET() {
        String uri = buildURI();
        return null;

    }

    //TODO: ?
    public UsergridResponse GET(String uri) {
        return null;
    }
}
