package org.apache.usergrid.java.client;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Jeff West on 9/2/15.
 */
@SuppressWarnings("unused")
public class UsergridAuth {

    private String accessToken = null;
    private Long expiry = null;
    private boolean usingToken = false;

    public UsergridAuth() { }

    public UsergridAuth(@Nullable final String accessToken) {
        this.usingToken = true;
        setAccessToken(accessToken);
    }

    public UsergridAuth(@Nullable final String accessToken, @Nullable final Long expiry) {
        this.usingToken = true;
        setAccessToken(accessToken);
        setExpiry(expiry);
    }

    public void destroy() {
        setAccessToken(null);
        setExpiry(null);
    }

    @Nullable public String getAccessToken() { return accessToken; }
    public void setAccessToken(@Nullable final String accessToken) {
        this.accessToken = accessToken;
    }

    @Nullable public Long getExpiry() { return expiry; }
    public void setExpiry(@Nullable final Long tokenExpiry) { this.expiry = tokenExpiry; }

    public boolean isValidToken() { return (hasToken() && !isExpired()); }

    public boolean hasToken() { return accessToken != null; }

    public boolean isExpired() {
        if (expiry != null) {
            Long currentTime = System.currentTimeMillis() / 1000;
            return ((expiry / 1000) < currentTime);
        } else {
            return !this.usingToken;
        }
    }
}