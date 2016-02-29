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

//  private static final Map<String, UsergridClient> instances_;
  public static final String STR_DEFAULT = "default";
  private static boolean initialized = false;

  //
  public static String accessToken = null;
  public static Long expiry;
  public static boolean hasToken = false;
  private static boolean usingToken = false;
  public static boolean isValidToken = false;
  private static Long token_expiry;


  public UsergridAuth(){

  }

  public UsergridAuth(String aToken,Long expiryTime){
    this.usingToken = true;
    this.accessToken = aToken;
    this.expiry = expiryTime;
  }



  public boolean isexprired(){
      boolean isExpired = false;
      if(expiry < 0 )
        isExpired = false;
    else
        isExpired = !this.usingToken;
    return  isExpired;
  }
  public boolean hasToken(){
    if(accessToken != null)
      return true;
    return false;
  }




  public boolean isValidToken(){
//        String tkn = this.getAccessToken();
    return false;
  }

  /**
   * The access token retrieved
   * @return
   */
  public String token(){
    return  Usergrid.getInstance().getAccessToken();
  }

  /**
   * Date object for when the token expires. //TODO : int or obj?
   * @return
   */
  public Long tokenExpiry(){
    return this.token_expiry;
  }

  public void setTokenExpiry(Long tokenExpriy){
    this.token_expiry = tokenExpriy;
  }

  /**
   * Destroys/removes the access token and expiry.
   */
  public void destroy(){
    this.accessToken = null;
    this.expiry = null;
  }

}