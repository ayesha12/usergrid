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
package org.apache.usergrid.java.client;

import org.apache.usergrid.java.client.UsergridEnums.UsergridAuthMode;
import org.apache.usergrid.java.client.model.UsergridAppAuth;
import org.apache.usergrid.java.client.model.UsergridUserAuth;

/**
 * The Client class for accessing the Usergrid API. Start by instantiating this
 * class though the appropriate constructor.
 */
public class UsergridClientConfig {

    // The organization identifier.
    public String orgId = null;

    // The application identifier.
    public String appId = null;

    // The base URL that all calls will be made with.
    public String baseUrl = null;

    // The `UsergridAuthFallback` value used to determine what type of token will be sent, if any.
    public UsergridAuthMode authMode = UsergridAuthMode.USER;

    public UsergridAppAuth appAuth;

    public UsergridUserAuth userAuth;

    public UsergridClientConfig(String orgId, String appId) {
        this.orgId = orgId;
        this.appId = appId;
    }

    public UsergridClientConfig(String appId, String orgId, String baseUrl) {
        this.orgId = orgId;
        this.appId = appId;
        this.baseUrl = baseUrl;
    }

    public UsergridClientConfig(String orgId, String appId, String baseUrl, UsergridAuthMode authFallback) {
        this.orgId = orgId;
        this.appId = appId;
        this.baseUrl = baseUrl;
        this.authMode = authFallback;
    }

}
