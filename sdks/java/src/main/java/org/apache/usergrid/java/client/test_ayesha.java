package org.apache.usergrid.java.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.model.UsergridUser;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.codehaus.jettison.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//import org.apache.usergrid.java.client.query


/**
 * Created by ApigeeCorporation on 6/26/15.
 */
public class test_ayesha {

    public static final String APP_CLIENT_ID = "b3U6ETo16hOkEeWr70FRIvzssA" ; // "b3U66ne33W4OEeWXmAIj6QFb-Q";
    public static final String APP_CLIENT_SECRET = "b3U6fBkR5o4B9S5Lv93rPY5JYk17rDc";// "b3U6PxbpQiTrXKCWu0n1CjK1uTZXuG4";
    public static final String USERGRID_URL = "https://api.usergrid.com/"; //"https://ug21.e2e.apigee.net/";
  public static final String ORG_NAME = "ayesha";
  public static final String APP_NAME = "sandbox";
  public static final String USER_NAME = "Blueprints_usergrid_0302";
  public static final String USER_PASSWORD = "Blueprints_usergrid_0302";
  public static final String COLLECTION = "restaurants";
  private static final String ACESS_TOKEN = "YWMtGTE_qOGMEeW69X-GHPBO4wAAAVNikI1rupOxmyYtv6xLeNRIR_foLcVswE0";

  public static void main(String[] args) throws JSONException {

    // below is the sample code
//    Usergrid.initialize(USERGRID_URL, ORG_NAME, APP_NAME);

    // if you want to have direct access, you can get an instance of the singleton
    UsergridClient client = Usergrid.getInstance();

    System.out.println(Usergrid.isInitialized());

    Usergrid.initSharedInstance(USERGRID_URL,ORG_NAME,APP_NAME);
    System.out.println(client.getAppId());
//    UsergridResponse resp = Usergrid.authorizeAppUser(USER_NAME,USER_PASSWORD);


     System.out.println(Usergrid.authorizeAppClient(APP_CLIENT_ID,APP_CLIENT_SECRET));
    System.out.println("////////");

    UsergridEntity e = new UsergridEntity("people");
    e.putproperty("name", "entity1");

    e.putproperty("location","sj");

    UsergridResponse r = e.POST();
    System.out.println("baseurl1 " + r.getError());



    UsergridClient c = Usergrid.getInstance();
//    c.createEntity(e);

//    UsergridClient c = new UsergridClient(ORG_NAME,APP_NAME);

//    c.setBaseUrl(USERGRID_URL);
//    c.setAccessToken(ACESS_TOKEN);
//    System.out.println("access_token " + c.getAccessToken());
//    System.out.println("baseurl " + c.getClientSecret());
//    System.out.println("baseurl1 " );
////
//
//    Map<String,Object>param = new HashMap<String, Object>();
//    param.put("access_token",c.getAccessToken());
//      System.out.println(c.apiRequest("GET",param,null,"management/me"));


//    UsergridResponse r = c.apiRequest("GET",null,null,ORG_NAME,APP_NAME,"restaurants");


    System.out.println("has next page : " + r.first() );
//    System.out.println("has enity 1 : " + r);
//    System.out.println("has next page : " + r.loadNextpage() );
//    UsergridUser e1 = new UsergridUser("test4","Test@123r4678");
////    boolean s = e1.checkAvailable("abc@gmail.com",null);
////    System.out.println(s);
//
////    UsergridUser entity = new UsergridUser(
//    e1.create();
//
//    e1.getType();
//    System.out.println(e1);
//    e1.remove();
//
//



//    System.out.println(c.apiRequest("GET",null,null,ORG_NAME,APP_NAME));
//    UsergridResponse resp = c.getEntity("restaurants","amici");
//    System.out.println("entity amici : " + resp.entity());
////    UsergridEntity amici = resp.entity();
////    amici.putproperty("menu","pasta");
////    amici.save();
//
//    resp = c.getEntity("restaurants","amici");
//    System.out.println("entity amici with menu : " + resp.entity());
//
//
//
//     e = c.getEntity("shoes","nike").first();
//    System.out.println("entity nike: " + e);
//
////    int[] intarr = {3,4,5};
//////    e.p
//    ArrayList<Integer> arr = new ArrayList<Integer>();
//    arr.add(20);
//    arr.add(30);
////
//    e.putproperty("arr1", arr);
//    e.save();
//
//    e.remove();
//
//////
////    ArrayList<Integer> arr1 = new ArrayList<Integer>();
////    arr1.add(24);
////    arr1.add(35);
////
//////    e.append("sarr",arr1);
//////    e.PUT();
//////    e.insert("sarr",arr,0);
//////    e.PUT();
////
//////    ArrayList<Integer> arr2 = new ArrayList<Integer>();
//////    arr2.add(22);
//////    arr2.add(33);
//////
//////    e.prepend("sarr",arr2);
//////    e.PUT();
////
//////    System.out.println("add property" + e.putproperty("samplearray",arr));
////
////    System.out.println("properties " + e.getEntityProperty("arr1").getClass());
////
////    ArrayNode anode = new ArrayNode(JsonNodeFactory.instance);
////    anode.add(3).add(4);
////
//////    e.putproperty("arr1",anode);
//////    e.PUT();
////    e.append("arr1",anode);
////    System.out.println("properties " + e.getEntityProperty("arr1"));
////
////    e.shift("arr1");
////    e.PUT();
////    e.RELOAD();
////    System.out.println("properties " + e.getEntityProperty("arr1"));
////
////    UsergridEntity e1 = new UsergridEntity("shoes");
////    System.out.println("uuid " + e1.getUuidString());
////
////    e1.putproperty("getName","nike");
////
////    e1.save();
//    ;
//
////    int[] arr1 = {10, 20, 30};
////    e.append("samplearray", arr1);
//
////    System.out.println(c.getConnections(Direction.IN,e,"visited").first());
////    System.out.println("-----------------");
////
//////    System.out.println(e.getEntityProperty("file-metadata").toString());
////    System.out.println(e.hasAsset());
////    System.out.println("..................");
////        Map<String, Object> params = null;
////    params.put("Accept","plain/text");
////    System.out.println(c.apiRequest("GET",null,"plain/text",ORG_NAME,APP_NAME,"restaurants","amici"));
////    System.out.println("////////");
//
//
//    c.appAuth();
////    UsergridResponse r = Usergrid.authorizeAppClient(APP_CLIENT_ID, APP_CLIENT_SECRET);
//
////    System.out.println(r.getAccessToken());
////
////    // new query builder
//    UsergridQuery q = null;
////
////    UsergridEntity e = Usergrid.GET("pets", "max").first();
////    e.PUT();
////
////    QueryResult qr = null;
////
////    for (int x = 0; x < 20; x++) {
////      UsergridEntity pet = new UsergridEntity(COLLECTION);
////      pet.setProperty("getName", "pet-" + x);
////      pet.setProperty("age", x);
////      pet.setLocation(-1, -2);
////      pet.PUT();
////    }
////
////    try {
////      Thread.sleep(3000);
////    } catch (InterruptedException e1) {
////      e1.printStackTrace();
////    }
////
////    System.out.println("GETTING by query...");
////
//    // new query builder
////    q = new UsergridQuery.Builder()
////        .collection(COLLECTION)
////        .limit(500)
////        .locationWithin(5.0, -1, -2)
////        .build();
//    //not(" distance ", "500")





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
