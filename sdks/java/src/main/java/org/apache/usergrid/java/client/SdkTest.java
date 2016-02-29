package org.apache.usergrid.java.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;

import java.util.Map;
//import org.apache.usergrid.java.client.query


/**
 * Created by ApigeeCorporation on 6/26/15.
 */
public class SdkTest {

  public static final String APP_CLIENT_ID = "b3U66ne33W4OEeWXmAIj6QFb-Q";
  public static final String APP_CLIENT_SECRET = "b3U6PxbpQiTrXKCWu0n1CjK1uTZXuG4";
  public static final String USERGRID_URL = "https://ug21.e2e.apigee.net/";
  public static final String ORG_NAME = "usergrid";
  public static final String APP_NAME = "ayesha";
  public static final String COLLECTION = "restaurants";
  private static final String ACESS_TOKEN = "YWMtDr76uNmREeWNJnlLhjx02wAAAVMuQ0e7OjLidK0hb5bfAFLeuc2DxvNcj6w";

  public static void main(String[] args) {

    // below is the sample code
//    Usergrid.initialize(USERGRID_URL, ORG_NAME, APP_NAME);

    // if you want to have direct access, you can get an instance of the singleton
    UsergridClient client = Usergrid.getInstance();

    System.out.println(Usergrid.isInitialized());

    Usergrid.initSharedInstance(USERGRID_URL,ORG_NAME,APP_NAME);
    System.out.println(client.getAppId());
    System.out.println(Usergrid.authorizeAppClient(APP_CLIENT_ID,APP_CLIENT_SECRET));
//    System.out.println("////////");

    UsergridClient c = new UsergridClient(ORG_NAME,APP_NAME);

    c.setBaseUrl(USERGRID_URL);
    c.setAccessToken(ACESS_TOKEN);
    System.out.println("access_token " + c.getAccessToken());
    System.out.println("baseurl " + c.getClientSecret());
    System.out.println("baseurl1 " );




    System.out.println(c.apiRequest("GET",null,null,ORG_NAME,APP_NAME));
    UsergridResponse resp = c.getEntity("restaurants","amici");


    System.out.println("resp : " + resp);
    UsergridEntity e = c.getEntity("restaurants","amici1").first();
//    System.out.println(c.getConnections(Direction.IN,e,"visited").first());
//    System.out.println("-----------------");
//
////    System.out.println(e.getEntityProperty("file-metadata").toString());
//    System.out.println(e.hasAsset());
//    System.out.println("..................");
//        Map<String, Object> params = null;
//    params.put("Accept","plain/text");
//    System.out.println(c.apiRequest("GET",null,"plain/text",ORG_NAME,APP_NAME,"restaurants","amici"));
//    System.out.println("////////");


    c.appAuth();
//    UsergridResponse r = Usergrid.authorizeAppClient(APP_CLIENT_ID, APP_CLIENT_SECRET);

//    System.out.println(r.getAccessToken());
//
//    // new query builder
    UsergridQuery q = null;
//
//    UsergridEntity e = Usergrid.GET("pets", "max").first();
//    e.PUT();
//
//    QueryResult qr = null;
//
//    for (int x = 0; x < 20; x++) {
//      UsergridEntity pet = new UsergridEntity(COLLECTION);
//      pet.setProperty("getName", "pet-" + x);
//      pet.setProperty("age", x);
//      pet.setLocation(-1, -2);
//      pet.PUT();
//    }
//
//    try {
//      Thread.sleep(3000);
//    } catch (InterruptedException e1) {
//      e1.printStackTrace();
//    }
//
//    System.out.println("GETTING by query...");
//
    // new query builder
//    q = new UsergridQuery.Builder()
//        .collection(COLLECTION)
//        .limit(500)
//        .locationWithin(5.0, -1, -2)
//        .build();
    //not(" distance ", "500")





//    q = new UsergridQuery.Builder().collection("restaurants").not().contains("getName","amici").or().contains(" getName ","amici1").build();
//    q = new UsergridQuery.Builder ().collection("restaurants").contains("getName","r5 r6").build();

//    q = new UsergridQuery.Builder().collection("restaurants").ql("select *  where distance = 500 or getName = 'amici1'").build();

//    q = new UsergridQuery.Builder().collection("restaurants").fromString("select *  where distance = 500 or getName = 'amici1' ").build();
//
//
////    System.out.println(new UsergridQuery.Builder().collection("restaurants").ql("select *  where getName = 'blaze'").build().GET());
//    System.out.println(c.GETFromQuery(q).getEntities());

//    System.out.println(new UsergridQuery("restaurants").qfromString("select *  where distance = 500 or getName = 'amici1' ")); //correct
//    System.out.println("////////");

//
//    // singleton operation
//    qr = Usergrid.getInstance().GET(q);
//
//    boolean keepGoing = true;
//
//    while (keepGoing) {
//
//      for (UsergridEntity entity : qr) {
//        System.out.println(entity);
//      }
//
//      if (qr.hasMorePages())
//        qr = qr.retrieveNextPage();
//      else
//        keepGoing = false;
//    }
//
//    HashMap<String, Object> update = new HashMap<>();
//    update.put("JeffWest", "WuzHere");
//    System.out.println("PUT!");
//    QueryResult rput = q.PUT(update);
//
//    System.out.println("PUT SIzE: " + rput.getEntities().size());
//
//    System.out.println("DELETE!");
//    QueryResult rdel = q.DELETE();
//
//    System.out.println("DELETE SIzE: " + rdel.getEntities().size());
  }
}
