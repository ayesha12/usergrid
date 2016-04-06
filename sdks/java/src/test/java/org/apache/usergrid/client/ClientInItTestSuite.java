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

import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridEnums;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridUserAuth;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ClientInItTestSuite {

    @Before
    public void before() {
        Usergrid.initSharedInstance(SDKTestConfiguration.ORG_NAME, SDKTestConfiguration.APP_NAME, SDKTestConfiguration.USERGRID_URL, SDKTestConfiguration.authFallBack);
    }

    @After
    public void after() {
        Usergrid.reset();
    }

    @Test
    public void clientAppInit() throws JSONException {
        Usergrid.setAuthMode(SDKTestConfiguration.authFallBack);
        try {
            UsergridResponse response = Usergrid.authenticateApp(new UsergridAppAuth(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET));
            assertTrue("no error thrown", response.getResponseError() == null);
            assertTrue("response status is OK", response.ok() == true);
            assertTrue("should have a valid token",response.getAccessToken().length() > 10);
            assertTrue("client.appAuth.token should be set to the token returned from Usergrid",
                    Usergrid.getAppAuth().getAccessToken().equals(response.getAccessToken()));
            assertTrue("client.appAuth.isValid should be true",Usergrid.getAppAuth().isValidToken() == true);
            assertTrue("client.appAuth.expiry should be set to a future date",
                    Usergrid.getAppAuth().getExpiry() > System.currentTimeMillis());
        } catch (IllegalArgumentException e) {
            assertTrue(" error thrown", e != null);
        }

        //should authenticate by passing clientId and clientSecret in an object
        Usergrid.setAuthMode(SDKTestConfiguration.authFallBack);
        try {
            UsergridAppAuth auth = new UsergridAppAuth(SDKTestConfiguration.APP_CLIENT_ID, SDKTestConfiguration.APP_CLIENT_SECRET);
            UsergridResponse response = Usergrid.authenticateApp(auth);
            assertTrue("no error thrown", response.getResponseError() == null);
            assertTrue("response status is OK", response.ok() == true);
            assertTrue("should have a valid token",response.getAccessToken().length() > 10);
            assertTrue("client.appAuth.token should be set to the token returned from Usergrid",
                    Usergrid.getAppAuth().getAccessToken().equals(response.getAccessToken()));
            assertTrue("client.appAuth.isValid should be true",Usergrid.getAppAuth().isValidToken() == true);
            assertTrue("client.appAuth.expiry should be set to a future date",
                    Usergrid.getAppAuth().getExpiry() > System.currentTimeMillis());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            assertTrue(" error thrown", e == null);
        }


    }

    @Test
    public void clientUserInit() {
        Usergrid.setAuthMode(UsergridEnums.UsergridAuthMode.USER);
        try {
            UsergridResponse response = Usergrid.authenticateUser(new UsergridUserAuth(SDKTestConfiguration.APP_UserName, SDKTestConfiguration.APP_Password));
            assertTrue("no error thrown", response.getResponseError() == null);
            assertTrue("response status is OK", response.ok() == true);
            assertTrue("should have a valid token",response.getAccessToken().length() > 10);
            assertTrue("client.appAuth.token should be set to the token returned from Usergrid",
                    Usergrid.getCurrentUser().userAuth.getAccessToken().equals(response.getAccessToken()));
            assertTrue("client.appAuth.isValid should be true",Usergrid.getCurrentUser().userAuth.isValidToken() == true);
            assertTrue("client.appAuth.expiry should be set to a future date",
                    Usergrid.getCurrentUser().userAuth.getExpiry() > System.currentTimeMillis());
        } catch (IllegalArgumentException e) {
            assertTrue(" error thrown", e != null);
        }

        Usergrid.setAuthMode(UsergridEnums.UsergridAuthMode.USER);
        try {
            UsergridUserAuth auth = new UsergridUserAuth(SDKTestConfiguration.APP_UserName, SDKTestConfiguration.APP_Password);
            UsergridResponse response = Usergrid.authenticateUser(auth);
            assertTrue("no error thrown", response.getResponseError() == null);
            assertTrue("response status is OK", response.ok() == true);
            assertTrue("should have a valid token",response.getAccessToken().length() > 10);
            assertTrue("client.appAuth.token should be set to the token returned from Usergrid",
                    Usergrid.getCurrentUser().userAuth.getAccessToken().equals(response.getAccessToken()));
            assertTrue("client.appAuth.isValid should be true",Usergrid.getCurrentUser().userAuth.isValidToken() == true);
            assertTrue("client.appAuth.expiry should be set to a future date",
                    Usergrid.getCurrentUser().userAuth.getExpiry() > System.currentTimeMillis());
        } catch (IllegalArgumentException e) {
            assertTrue(" error thrown", e != null);
        }

    }

}
