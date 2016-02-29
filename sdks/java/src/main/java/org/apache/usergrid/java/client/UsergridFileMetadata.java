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

import com.fasterxml.jackson.databind.JsonNode;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.usergrid.java.client.filter.ErrorResponseFilter;
import org.apache.usergrid.java.client.model.*;
import org.apache.usergrid.java.client.query.EntityQueryResult;
import org.apache.usergrid.java.client.query.LegacyQueryResult;
import org.apache.usergrid.java.client.query.QueryResult;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;


/**
 * The Client class for accessing the Usergrid API. Start by instantiating this
 * class though the appropriate constructor.
 */
public class UsergridFileMetadata {

    public static final String FILE_METADATA = "file-metadata";
    public static final String eTag_STRING = "eTag";
    public static final String CONTENT_TYPE_STRING = "content-type ";
    public static final String CONTENT_LENGTH_STRING= "content-length";
    public static final Long LAST_MODIFIED_STRING = null;
    public static final String CHECKSUM_STRING = "checksum";

    public static String eTag = "";
    public static String content_Type = "";
    public static int content_Length = 0;
    public static Long last_Modified_Timestamp = null;
    public static String checksum= "";
    public static SimpleDateFormat last_Modified_Date;

    public void init(Map<String,JsonNode> metadataInfo) {
        eTag = metadataInfo.get(eTag_STRING).toString();
        content_Type = metadataInfo.get(CONTENT_TYPE_STRING).toString();
        content_Length = metadataInfo.get(CONTENT_LENGTH_STRING).asInt();
        checksum = metadataInfo.get(CHECKSUM_STRING).toString();
        last_Modified_Timestamp = metadataInfo.get(LAST_MODIFIED_STRING).asLong();

        if(last_Modified_Timestamp > 0){
            Date date = new Date (last_Modified_Timestamp*1000L);
            last_Modified_Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            last_Modified_Date.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    }





}
