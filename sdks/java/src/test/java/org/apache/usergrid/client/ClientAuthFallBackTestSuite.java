/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.usergrid.client;

import org.apache.usergrid.java.client.*;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ClientAuthFallBackTestSuite {
    public static UsergridClient client = null;

    @Before
    public void before() {
        Usergrid.initSharedInstance(SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.authFallBack);
        UsergridAppAuth appAuth = new UsergridAppAuth(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
        Usergrid.authenticateApp(appAuth);
        client = Usergrid.getInstance();
        String[] segments = {client.getOrgId(), client.getAppId(),"roles","guest","permissions"};
        Map<String, Object> params = new HashMap<>();
        params.put("permission","get,post,put,delete:/**");
        UsergridRequest request = new UsergridRequest(UsergridEnums.UsergridHttpMethod.DELETE, MediaType.APPLICATION_JSON_TYPE,
                client.getBaseUrl(), params, null, segments);
        UsergridResponse resp = new UsergridRequestManager(client).performRequest(request);
    }

    @After
    public void after() {
        UsergridClient client1 = client.usingAuth(client.getAppAuth());
        client1.setAuthMode(UsergridEnums.UsergridAuthMode.NONE);
        String[] segments = {client1.getOrgId(), client1.getAppId(),"roles","guest","permissions"};
        Map<String, Object> params = new HashMap<>();
        params.put("permission","get,post,put,delete:/**");
        UsergridRequest request = new UsergridRequest(UsergridEnums.UsergridHttpMethod.POST, MediaType.APPLICATION_JSON_TYPE,
                client1.getBaseUrl(), params, null, segments);
        //TODO : throws error.
        UsergridResponse resp = new UsergridRequestManager(client1).performRequest(request);
//        System.out.println(resp);

        Usergrid.reset();
    }

    @Test
    public void authFallBackNONETest() throws JSONException {
        //should fall back to using no authentication when currentUser is not authenticated and authFallback is set to NONE
        client.setAuthMode(UsergridEnums.UsergridAuthMode.NONE);
        UsergridQuery q = new UsergridQuery("users").desc("created");
        UsergridResponse resp = client.GET(q);
        assertTrue("The returned resonse should have error", resp != null);
        assertTrue("The returned resonse should have error", resp.getResponseError() != null);

    }

    @Test
    public void authFallBackAPPTest() throws JSONException {
        //should fall back to using no authentication when currentUser is not authenticated and authFallback is set to NONE
        client.setAuthMode(UsergridEnums.UsergridAuthMode.APP);
        UsergridQuery q = new UsergridQuery("users").desc("created");
        UsergridResponse resp = client.GET(q);
        assertTrue("The returned resonse should not have error", resp != null);
        assertTrue("The returned resonse should not have error", resp.getResponseError() == null);

    }


}
