package org.apache.usergrid.java.client;

import org.apache.usergrid.java.client.response.UsergridResponse;

import java.util.HashMap;
import java.util.LongSummaryStatistics;
import java.util.Map;

import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;


/**
 * Created by Jeff West on 9/2/15.
 */
public class UsergridAuth {

  public static String accessToken = null;
  public static boolean usingToken = false;
  public static Long token_expiry;


  public UsergridAuth(){
  }

  public UsergridAuth(String aToken,Long expiryTime){
    this.usingToken = true;
    setAccessToken(aToken);
    setTokenExpiry(expiryTime);
  }

  public boolean hasToken(){
    if(accessToken != null)
      return true;
    return false;
  }

  public boolean isexprired(){
      boolean isExpired = false;
      Long currTime = System.currentTimeMillis()/1000 ;
      if(token_expiry/1000 < currTime )
        isExpired = false;
    else
        isExpired = true; //  todo : why !this.usingToken in swift ?
    return  isExpired;
  }

  //TODO
  public boolean isValidToken(){
//        String tkn = this.getAccessToken();
    return false;
  }

  public void setAccessToken(String acToken){
    this.accessToken = acToken;
  }

  public void setTokenExpiry(Long tokenExpriy){
    this.token_expiry = tokenExpriy;
  }

  /**
   * Destroys/removes the access token and expiry.
   */
  public void destroy(){
    this.accessToken = null;
    this.token_expiry = null;
  }

}