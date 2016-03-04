package org.apache.usergrid.client;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by ApigeeCorporation on 9/14/15.
 */
public class UsergridInitTest {

  @Before
  public void before() {
    Usergrid.initSharedInstance(SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME);
    Usergrid.authorizeAppClient(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
  }

  
  @Test
  public void testInitUsergrid() {

  }
}
