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
package org.apache.usergrid.java.client.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UsergridResponseError {

    public static String errorName;
    public static String errorDescription;
    public static String errorException;
    public static int code;

    private final Map<String, JsonNode> properties = new HashMap<String, JsonNode>();
    private int statuscode;
    private Map<String, JsonNode> header;
    private static final Logger log = LoggerFactory.getLogger(UsergridEntity.class);

    public UsergridResponseError() {
    }

    // TODO : public init();
    public UsergridResponseError(String name, int code, String description, String errorException) {
        this.errorName = name;
        this.code = code;
        this.errorDescription = description;
        this.errorException = errorException;

    }

    @JsonAnyGetter
    public Map<String, JsonNode> getProperties() {
        return properties;
    }

    @JsonAnySetter
    public void setProperty(String key, JsonNode value) {
        properties.put(key, value);
    }

    @JsonSerialize(include = Inclusion.NON_NULL)
    public String getError() {
        return errorException;
    }

    public void setError(String error) {
        this.errorException = error;
    }

    @JsonSerialize(include = Inclusion.NON_NULL)
    @JsonProperty("error_description")
    public String getErrorDescription() {
        return errorDescription;
    }

    @JsonProperty("error_description")
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }


    public int getStatusCode() {
        return this.statuscode;
    }

    public void setStatusCode(int status) {
        this.statuscode = status;
    }

    public boolean ok() {
        if (this.statuscode < 400)
            return true;
        return false;
    }
}
