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
package org.apache.usergrid.java.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.usergrid.java.client.Usergrid;
import org.apache.usergrid.java.client.UsergridClient;
import org.apache.usergrid.java.client.UsergridEnums.*;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.apache.usergrid.java.client.utils.JsonUtils;
import org.codehaus.jettison.json.JSONException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@SuppressWarnings("unused")
@JsonSerialize(include = NON_NULL)
public class UsergridUser extends UsergridEntity {

    public final static String USER_ENTITY_TYPE = "user";

    public UsergridUserAuth userAuth = null;

    public UsergridUser() {
        super();
        setType(USER_ENTITY_TYPE);
    }

    public UsergridUser(@NotNull final UsergridEntity usergridEntity) {
        super();
        properties = usergridEntity.properties;
        setType(USER_ENTITY_TYPE);
    }

    public UsergridUser(@NotNull final String name, HashMap<String, Object> propertyMap) throws JSONException {
        super();
        setType(USER_ENTITY_TYPE);
        setName(name);
        putProperties(propertyMap);
    }

    public UsergridUser(@NotNull final String username, @NotNull final String password) {
        super();
        setUsername(username);
        setPassword(password);
    }

    public UsergridUser(@NotNull final String name, @NotNull final String username, @NotNull final String email, @NotNull final String password) {
        super();
        setName(name);
        setUsername(username);
        setEmail(email);
        setPassword(password);
    }

    @Nullable
    public String getUsername() { return JsonUtils.getStringProperty(properties, UsergridUserProperties.USERNAME.toString()); }
    public void setUsername(@NotNull final String username) { JsonUtils.setStringProperty(properties, UsergridUserProperties.USERNAME.toString(), username); }

    @Nullable
    public String getName() { return JsonUtils.getStringProperty(properties, UsergridUserProperties.NAME.toString()); }
    public void setName(@NotNull final String name) { JsonUtils.setStringProperty(properties, UsergridUserProperties.NAME.toString(), name); }

    @Nullable
    public String getEmail() { return JsonUtils.getStringProperty(properties, UsergridUserProperties.EMAIL.toString()); }
    public void setEmail(@NotNull final String email) { JsonUtils.setStringProperty(properties, UsergridUserProperties.EMAIL.toString(), email); }

    @Nullable
    public String getPassword() { return JsonUtils.getStringProperty(properties, UsergridUserProperties.PASSWORD.toString()); }
    public void setPassword(@NotNull final String password) { JsonUtils.setStringProperty(properties, UsergridUserProperties.PASSWORD.toString(), password); }

    public boolean isActivated() { return JsonUtils.getBooleanProperty(properties, UsergridUserProperties.ACTIVATED.toString()); }
    public void setActivated(@NotNull final Boolean activated) { JsonUtils.setBooleanProperty(properties, UsergridUserProperties.ACTIVATED.toString(), activated); }

    public boolean isDisabled() { return JsonUtils.getBooleanProperty(properties, UsergridUserProperties.DISABLED.toString()); }
    public void setDisabled(@NotNull final Boolean disabled) { JsonUtils.setBooleanProperty(properties, UsergridUserProperties.DISABLED.toString(), disabled); }

    @Nullable
    public String uuidOrUsername() {
        String uuidOrUsername = this.getUuid();
        if( uuidOrUsername == null ) {
            uuidOrUsername = this.getUsername();
        }
        return uuidOrUsername;
    }

    @Nullable
    public String usernameOrEmail() {
        String usernameOrEmail = this.getUsername();
        if( usernameOrEmail == null ) {
            usernameOrEmail = this.getEmail();
        }
        return usernameOrEmail;
    }

    public boolean checkAvailable(@Nullable final String email, @Nullable final String username) {
        return checkAvailable(Usergrid.getInstance(), email, username);
    }

    public boolean checkAvailable(@NotNull final UsergridClient client, @Nullable final String email, @Nullable final String username) {
        if (email == null && username == null) {
            throw new IllegalArgumentException("email and username both are null ");
        }

        UsergridQuery query = new UsergridQuery(USER_ENTITY_TYPE);
        if (username == null) {
            query.eq(UsergridUserProperties.EMAIL.toString(), email);
        } else if (email == null) {
            query.eq(UsergridUserProperties.USERNAME.toString(), username);
        } else {
            query.eq(UsergridUserProperties.EMAIL.toString(), email).or().eq(UsergridUserProperties.USERNAME.toString(), username);
        }
        return client.GET(query).first() != null;
    }

    public void create() throws JSONException {
        create(Usergrid.getInstance());
    }

    // FIXME: FIX THIS AND THE METHOD ABOVE TO RETURN SOMETHING
    public void create(@NotNull final UsergridClient client) throws JSONException {
        UsergridEntity entity = null;
        UsergridResponse entityResponse = null;
        try {
            entityResponse = client.GET(USER_ENTITY_TYPE, this.getUsername());
            entity = entityResponse.entity();
        } catch (Exception e) {
        }
        if (entity != null) {
            this.properties = entity.properties;
        } else {
            if (getName() == null)
                this.setName(getUsername());
            this.setType(USER_ENTITY_TYPE);
            entityResponse = client.POST(this);
            this.properties = entityResponse.entity().properties;
        }
    }
}
