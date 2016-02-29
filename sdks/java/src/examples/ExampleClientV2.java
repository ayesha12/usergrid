import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.model.Connection;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.query.QueryResult;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;

import java.util.HashMap;
//import org.apache.usergrid.java.client.query


/**
 * Created by ApigeeCorporation on 6/26/15.
 */
public class ExampleClientV2 {

  public static final String APP_CLIENT_ID = "YXA6WMhAuFJTEeWoggrRE9kXrQ";
  public static final String APP_CLIENT_SECRET = "YXA6_Nn__Xvx8rEwsYaKgEm5SWFwtJ0";
  public static final String USERGRID_URL = "https://api-connectors-prod.apigee.net/appservices";
  public static final String ORG_NAME = "api-connectors";
  public static final String APP_NAME = "sdksandbox";

  public static void main(String[] args) {

    // below is the sample code
    Usergrid.initialize(USERGRID_URL, ORG_NAME, APP_NAME);

    // if you want to have direct access, you can get an instance of the singleton
    UsergridClient client = Usergrid.getInstance();

    UsergridResponse r = client.authorizeAppClient(APP_CLIENT_ID, APP_CLIENT_SECRET);

    UsergridEntity petMax = new UsergridEntity("pet");
    petMax.setProperty("getName", "max")
        .setProperty("age", 15)
        .setProperty("weight", 21)
        .setProperty("owner", (String) null);

    // these functions will use the singleton client instance
//    r = petMax.PUT();

//    r = Usergrid.GET("pet", "max");
//    UsergridEntity e = Usergrid.GET("pet", "max").first();

//    petMax.PUT(); // PUT to default client to update, fails if doesn't exist?
//    petMax.DELETE(); // DELETE to default client

//    client.DELETE(petMax);
//    client.DELETE("type", "<getName|UUID>");

    UsergridEntity owner = new UsergridEntity();
    owner.changeType("owner");
    owner.setProperty("getName", "jeff");
    owner.setProperty("age", 15);
//    r = owner.PUT();

    // new function to create connections (singleton instance)
//    Connection conn = owner.createConnection(petMax, "owns");
//    UsergridResponse connRes = owner.connect(petMax, "owns");

    for (int x = 0; x < 20; x++) {
      UsergridEntity pet = new UsergridEntity("pet");
      pet.setProperty("getName", "pet-" + x);
      pet.setProperty("age", x);
      pet.setLocation(-12.123123f, -12.123123f);
      pet.PUT();
    }

    // new query builder
    UsergridQuery q = new UsergridQuery.Builder()
        .collection("pets")
        .limit(5)
        .gt("age", 0)
        .lte("age", 15)
        .locationWithin(5.0f, -12.123123f, -12.123123f)
        .desc("age")
        .build();

    // singleton operation
    QueryResult qr = Usergrid.getInstance().GET(q);
    UsergridEntity first = qr.first();
    UsergridEntity last = qr.last();

    boolean keepGoing = true;

    while (keepGoing) {

      for (UsergridEntity entity : qr) {
        System.out.println(entity);
      }

      if (qr.hasMorePages())
        qr = qr.retrieveNextPage();
      else
        keepGoing = false;
    }


    qr = q.GET();

    QueryResult rput = q.PUT(new HashMap<String, Object>());
//    UsergridResponse rdel = q.DELETE();
  }
}
