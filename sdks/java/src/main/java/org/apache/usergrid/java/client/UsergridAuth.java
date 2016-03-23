package org.apache.usergrid.java.client;

/**
 * Created by Jeff West on 9/2/15.
 */
public class UsergridAuth {

    /**
     * TODO: These should be private with getters/setters.
     * In java start with private and add getters and setters and proceed from there.  Only use public if you absolutely need to.
     */
    public String accessToken = null;
    public boolean usingToken = false;
    public Long token_expiry;

    public UsergridAuth() {
    }

    public UsergridAuth(String aToken, Long expiryTime) {
        this.usingToken = true;
        setAccessToken(aToken);
        setTokenExpiry(expiryTime);
    }

    public boolean hasToken() {
        return (accessToken != null); // TODO: Why not just return the result instead of the if statement. e.g. return (accessToken != null);
    }

    public boolean isExprired() { // TODO: Methods need to be camel cased.
        boolean isExpired = false;
        Long currTime = System.currentTimeMillis() / 1000;
        if (token_expiry / 1000 < currTime)  // TODO: Why not just return the result instead of the if statement. e.g. return !(token_expiry / 1000 < currTime);
            isExpired = false;
        else
            isExpired = true; //  todo : why !this.usingToken in swift ?
        return isExpired;
    }

    public boolean isValidToken() {
        return (hasToken() && !isExprired());

    }

    public void setAccessToken(String acToken) {
        this.accessToken = acToken;
    }

    public void setTokenExpiry(Long tokenExpriy) {
        this.token_expiry = tokenExpriy;
    }

    /**
     * Destroys/removes the access token and expiry.
     */
    public void destroy() {
        this.accessToken = null;
        this.token_expiry = null;
    }

}