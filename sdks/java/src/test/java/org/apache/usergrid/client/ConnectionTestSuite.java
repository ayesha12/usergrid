package org.apache.usergrid.client;

import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by ApigeeCorporation on 9/14/15.
 */
public class ConnectionTestSuite {

  @Before
  public void before() {
    Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
    Usergrid.authorizeAppClient(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
  }

  @Test
  public void testBasicConnection() {
    String ownerCollection = "owners" + System.currentTimeMillis();
    String petCollection = "pet" + System.currentTimeMillis();

    UsergridEntity owner = new UsergridEntity(ownerCollection);
    owner.putproperty("getName", "jeff");
    owner.putproperty("wife", "julie");

    UsergridResponse r = owner.POST();

    // assert created

    UsergridEntity pet = new UsergridEntity(petCollection);
    pet.putproperty("getName", "max");
    pet.putproperty("color", "tabby");

    r = pet.POST();

    // assert created

    r = owner.connect("owns", pet);

    //query owner -> pet

    //query pet -> owner

    r = pet.connect("ownedBy", owner);

    //query owner -> pet

    //query pet -> owner


  }
}
