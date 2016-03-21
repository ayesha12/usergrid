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
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.apache.usergrid.java.client.utils.JsonUtils;
import org.codehaus.jettison.json.JSONException;

import java.util.HashMap;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;
import static org.apache.usergrid.java.client.utils.JsonUtils.*;

public class UsergridUser extends UsergridEntity {

    public final static String ENTITY_TYPE = "user";

    public final static String PROPERTY_USERNAME = "username";
    public final static String PROPERTY_EMAIL = "email";
    public final static String PROPERTY_NAME = "getName";
    public final static String PROPERTY_FIRSTNAME = "firstname";
    public final static String PROPERTY_MIDDLENAME = "middlename";
    public final static String PROPERTY_LASTNAME = "lastname";
    public final static String PROPERTY_ACTIVATED = "activated";
    public final static String PROPERTY_PICTURE = "picture";
    public final static String PROPERTY_DISABLED = "disabled";
    private static final String PROPERTY_PASSWORD = "password";

    public UsergridUserAuth userAuth = null;

    public UsergridUser() {
        super();
        changeType(ENTITY_TYPE);
    }

    public UsergridUser(UsergridEntity usergridEntity) {
        super();
        properties = usergridEntity.properties;
        changeType(ENTITY_TYPE);
    }

    public UsergridUser(String name, HashMap<String, Object> propertyMap) throws JSONException {
        super();
        changeType(ENTITY_TYPE);
        setName(name);
        putProperties(propertyMap);
    }

    public UsergridUser(String username, String userPassword) {
        super();
        setUsername(username);
        setPassword(userPassword);
    }

    public UsergridUser(String name, String username, String emailid, String password) {
        super();
        setName(name);
        setUsername(username);
        setEmail(emailid);
        setPassword(password);
    }


    @Override
    @JsonIgnore
    public String getNativeType() {
        return ENTITY_TYPE;
    }

    @Override
    @JsonIgnore
    public List<String> getPropertyNames() {
        List<String> properties = super.getPropertyNames();
        properties.add(PROPERTY_USERNAME);
        properties.add(PROPERTY_EMAIL);
        properties.add(PROPERTY_NAME);
        properties.add(PROPERTY_FIRSTNAME);
        properties.add(PROPERTY_MIDDLENAME);
        properties.add(PROPERTY_LASTNAME);
        properties.add(PROPERTY_ACTIVATED);
        properties.add(PROPERTY_PICTURE);
        properties.add(PROPERTY_DISABLED);
        return properties;
    }

    public void create() throws JSONException {

        UsergridEntity entity = null;
        UsergridResponse entityResponse = null;
        try {
            entityResponse = Usergrid.getInstance().GET(ENTITY_TYPE, this.getUsername());
            entity = entityResponse.entity();
        } catch (Exception e) {
//			e.printStackTrace();
        }
        if (entity != null) {
            this.properties = entity.properties;
        } else {
            if (getName() == null)
                this.setName(getUsername());
            this.setType(ENTITY_TYPE);
            entityResponse = Usergrid.getInstance().createEntity(this);
            this.properties = entityResponse.entity().properties;
        }
    }

    public void remove() {
        Usergrid.getInstance().DELETE(this);
    }

    @JsonSerialize(include = NON_NULL)
    public String getUsername() {
        return JsonUtils.getStringProperty(properties, PROPERTY_USERNAME);
    }

    public void setUsername(String username) {
        setStringProperty(properties, PROPERTY_USERNAME, username);
    }

    @JsonSerialize(include = NON_NULL)
    public String getName() {
        return JsonUtils.getStringProperty(properties, PROPERTY_NAME);
    }

    public void setName(String name) {
        setStringProperty(properties, PROPERTY_NAME, name);
    }

    @JsonSerialize(include = NON_NULL)
    public String getEmail() {
        return JsonUtils.getStringProperty(properties, PROPERTY_EMAIL);
    }

    public void setEmail(String email) {
        setStringProperty(properties, PROPERTY_EMAIL, email);
    }

    public void setPassword(String password) {
        setStringProperty(properties, PROPERTY_PASSWORD, password);
    }

    public boolean checkAvailable(String email, String username) {
        UsergridClient client = Usergrid.getInstance();
        UsergridQuery qry = null;
        if (email == null && username == null)
            new IllegalArgumentException("email and username both are null ");
        else if (username == null)
            qry = new UsergridQuery(ENTITY_TYPE).eq("email", email);
        else if (email == null)
            qry = new UsergridQuery(ENTITY_TYPE).eq("username", username);
        else
            qry = new UsergridQuery(ENTITY_TYPE).eq("email", email).or().eq("username", username);

        if (client.GET(qry).first() != null)
            return true;
        else
            return false;
    }

    @JsonSerialize(include = NON_NULL)
    public Boolean isActivated() {
        return getBooleanProperty(properties, PROPERTY_ACTIVATED);
    }

    public void setActivated(Boolean activated) {
        setBooleanProperty(properties, PROPERTY_ACTIVATED, activated);
    }

    @JsonSerialize(include = NON_NULL)
    public Boolean isDisabled() {
        return getBooleanProperty(properties, PROPERTY_DISABLED);
    }

    public void setDisabled(Boolean disabled) {
        setBooleanProperty(properties, PROPERTY_DISABLED, disabled);
    }

    @JsonSerialize(include = NON_NULL)
    public String getFirstname() {
        return JsonUtils.getStringProperty(properties, PROPERTY_FIRSTNAME);
    }

    public void setFirstname(String firstname) {
        setStringProperty(properties, PROPERTY_FIRSTNAME, firstname);
    }

    @JsonSerialize(include = NON_NULL)
    public String getMiddlename() {
        return JsonUtils.getStringProperty(properties, PROPERTY_MIDDLENAME);
    }

    public void setMiddlename(String middlename) {
        setStringProperty(properties, PROPERTY_MIDDLENAME, middlename);
    }

    @JsonSerialize(include = NON_NULL)
    public String getLastname() {
        return JsonUtils.getStringProperty(properties, PROPERTY_LASTNAME);
    }

    public void setLastname(String lastname) {
        setStringProperty(properties, PROPERTY_LASTNAME, lastname);
    }

    @JsonSerialize(include = NON_NULL)
    public String getPicture() {
        return JsonUtils.getStringProperty(properties, PROPERTY_PICTURE);
    }

    public void setPicture(String picture) {
        setStringProperty(properties, PROPERTY_PICTURE, picture);
    }

}
