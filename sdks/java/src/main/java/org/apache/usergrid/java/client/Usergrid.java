package org.apache.usergrid.java.client;

import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.response.UsergridResponse;

import java.util.HashMap;
import java.util.Map;

import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;


/**
 * Created by Jeff West on 9/2/15.
 */
public class Usergrid {

  private static final Map<String, UsergridClient> instances_;
  public static final String STR_DEFAULT = "default";
  private static boolean initialized = false;

  static {
    instances_ = new HashMap<>(5);
    instances_.put(STR_DEFAULT, new UsergridClient());
  }

  public static UsergridClient getInstance() {
    return getInstance(STR_DEFAULT);
  }

  public static UsergridClient getInstance(final String id) {

    UsergridClient client = instances_.get(id);

    if (client == null) {
      client = new UsergridClient();
      instances_.put(id, client);
    }

    return client;
  }

  /**
   * Instantiate a new instance of usergrid.
   * @param apiUrl
   * @param orgName
   * @param appName
     */
  public static void initSharedInstance(final String apiUrl, final String orgName, final String appName) {

    if (isEmpty(appName) || isEmpty(orgName) || isEmpty(apiUrl)){
      throw new IllegalArgumentException("One of the input arguments is empty.");
    }
    else{
      UsergridClient client = getInstance(STR_DEFAULT);
      client.withApiUrl(apiUrl)
              .withOrganizationId(orgName)
              .withApplicationId(appName);
      initialized = true;
    }

  }


  /**
   * Alias to Usergrid.initSharedInstance.
   * @param apiUrl
   * @param orgName
   * @param appName
   */
  public static void init(final String apiUrl, final String orgName, final String appName) {
    initSharedInstance(apiUrl, orgName, appName);
  }

  /**
   * Returns true if the Usergrid singleton has already been initialized.
   * @return
   * */

  public static boolean isInitialized(){
    return initialized;
  }

  public static RequestBuilder collection(final String collection) {
    RequestBuilder builder = new RequestBuilder();
    builder.collection = collection;
    return builder;
  }

  public static UsergridResponse GET(final String type,
                                     final String uriSuffix) {
    return getInstance().GET(type, uriSuffix);
//    RequestBuilder builder = new RequestBuilder();
//    builder.collection = type;
//    builder.uriSuffix = uriSuffix;
//    return builder.GET();
  }

  public static UsergridResponse authorizeAppClient(final String appClientId,
                                                    final String appClientSecret) {

    UsergridAppAuth ugAppAuth = new UsergridAppAuth(appClientId,appClientSecret);
    return getInstance().authenticateApp(ugAppAuth);
  }

  public static void reset() {
    initialized = false;
    instances_.put(STR_DEFAULT, new UsergridClient());
  }
}