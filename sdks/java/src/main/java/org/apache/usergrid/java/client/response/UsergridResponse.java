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

import static org.apache.usergrid.java.client.utils.JsonUtils.toJsonString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import javax.annotation.Nullable;

import org.apache.usergrid.java.client.*;
import org.apache.usergrid.java.client.model.UsergridEntity;
import org.apache.usergrid.java.client.model.UsergridUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UsergridResponse {

  private String accessToken;

  public UsergridResponseError responseError = null;

  private String path;
  private String uri;
  private String status;
  private long timestamp;
  private UUID application;
  private List<UsergridEntity> entities;
  private UUID next;
  private String cursor;
  private String action;
  private List<Object> list;
  private Object data;
  private Map<String, UUID> applications;
  private Map<String, JsonNode> metadata;
  private Map<String, List<String>> params;
  private List<AggregateCounterSet> counters;
  private ClientCredentialsInfo credentials;

  private List<QueueInfo> queues;
  private UUID last;
  private UUID queue;
  private UUID consumer;

  private UsergridUser user;

  private final Map<String, JsonNode> properties = new HashMap<String, JsonNode>();
  private int statuscode;
  private Map<String, JsonNode> header;
  private static final Logger log = LoggerFactory.getLogger(UsergridEntity.class);

  @JsonAnyGetter
  public Map<String, JsonNode> getProperties() {
    return properties;
  }

  @JsonAnySetter
  public void setProperty(String key, JsonNode value) {
    properties.put(key, value);
  }

  @JsonProperty("access_token")
  @JsonSerialize(include = Inclusion.NON_NULL)
  public String getAccessToken() {
    return accessToken;
  }

  @JsonProperty("access_token")
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public UUID getApplication() {
    return application;
  }

  public void setApplication(UUID application) {
    this.application = application;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public List<UsergridEntity> getEntities() {
    return entities;
  }

  public void setEntities(List<UsergridEntity> entities) {
    this.entities = entities;
  }

  public int getEntityCount() {
    if (entities == null) {
      return 0;
    }
    return entities.size();
  }

  @Nullable
  public UsergridEntity getFirstEntity() {
    return first();
  }

  public <T extends UsergridEntity> T getFirstEntity(Class<T> t) {
    return UsergridEntity.toType(getFirstEntity(), t);
  }

  @Nullable
  public UsergridEntity getLastEntity() {
    return last();
  }

  public <T extends UsergridEntity> T getLastEntity(Class<T> t) {
    return UsergridEntity.toType(getLastEntity(), t);
  }

  public <T extends UsergridEntity> List<T> getEntities(Class<T> t) {
    return UsergridEntity.toType(entities, t);
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public UUID getNext() {
    return next;
  }

  public void setNext(UUID next) {
    this.next = next;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public String getCursor() {
    return cursor;
  }

  public void setCursor(String cursor) {
    this.cursor = cursor;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public List<Object> getList() {
    return list;
  }

  public void setList(List<Object> list) {
    this.list = list;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public Map<String, UUID> getApplications() {
    return applications;
  }

  public void setApplications(Map<String, UUID> applications) {
    this.applications = applications;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public Map<String, JsonNode> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, JsonNode> metadata) {
    this.metadata = metadata;
  }

  public void setHeaders(Map<String, JsonNode> headers) {this.header = headers;}

  public Map<String, JsonNode> getHeaders() {return this.header ;}


  @JsonSerialize(include = Inclusion.NON_NULL)
  public Map<String, List<String>> getParams() {
    return params;
  }

  public void setParams(Map<String, List<String>> params) {
    this.params = params;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public List<AggregateCounterSet> getCounters() {
    return counters;
  }

  public void setCounters(List<AggregateCounterSet> counters) {
    this.counters = counters;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public ClientCredentialsInfo getCredentials() {
    return credentials;
  }

  public void setCredentials(ClientCredentialsInfo credentials) {
    this.credentials = credentials;
  }

  @JsonSerialize(include = Inclusion.NON_NULL)


  /**
   * A UsergridUser instance that contains details about the last authenticated user.
   */

  public UsergridUser currentUser() {
    return user;
  }

  public void setUser(UsergridUser user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return toJsonString(this);
  }

  @JsonSerialize(include = Inclusion.NON_NULL)
  public UUID getLast() {
    return last;
  }

  /**
   * get the first entity in the 'entities' array in the response
   *
   * @return A UsergridEntity if the entities array has elements, null otherwise
   */
  @Nullable
  public UsergridEntity first() {
    if (getEntities() != null && getEntities().size() > 0) {
      return getEntities().get(0);
    }

    return null;
  }
  public UsergridEntity user(){
    if(first().getType() == "user" )
      return first();
    else
      new Exception("Entity not of the type user");
    return null;
  }

  public List<UsergridEntity> users() {
    if(first().getType() == "user" )
      return entities;
    else
      new Exception("Entity not of the type user");
    return null;
  }


  /**
   * .entity is an alias for .first
   * @return
   */

  @Nullable
  public UsergridEntity entity() {
    return first();
  }


  /**
   * get the last entity in the 'entities' array in the response
   *
   * @return A UsergridEntity if the entities array has elements, null otherwise
   */
  @Nullable
  public UsergridEntity last() {
    if (getEntities() != null && getEntities().size() > 0) {
      return getEntities().get(getEntities().size() - 1);
    }

    return null;
  }

  public boolean hasNextPage(){
    if (getCursor() != null)
      return true;
    return false;
  }

  public List<UsergridEntity> loadNextpage(){
    if (hasNextPage()){
      Map<String, Object> paramsMap = new HashMap<String,Object>();
      paramsMap.put("cursor",getCursor());
      UsergridClient client = Usergrid.getInstance();

      String[] segments = {client.getOrgId(),client.getAppId(),this.first().getType()};

      UsergridRequest request = new UsergridRequest(UsergridEnums.UsergridHttpMethod.GET, MediaType.APPLICATION_JSON_TYPE,
              client.config.baseUrl,paramsMap,null,segments);
      UsergridResponse resp = UsergridRequestmanager.performRequest(request); //client.apiRequest("GET",paramsMap,null,client.getOrgId(),client.getAppId(),this.first().getType());
      return resp.entities;
    }
    log.info("there are no more enetities to load. Cursor is empty.");
    return null;
  }


  public static UsergridResponse fromException(Exception ex) {
    UsergridResponse response = new UsergridResponse();
    if(ex instanceof ClientErrorException){
      ClientErrorException clientError = (ClientErrorException) ex;
      response.responseError = new UsergridResponseError(clientError.getResponse().getStatusInfo().toString(),clientError.getResponse().getStatus(),
              clientError.getResponse().toString(),clientError.getClass().toString());
    }
    else
    response.responseError = new UsergridResponseError(ex.getClass().toString(),0,ex.getMessage(),ex.getClass().toString());
    return response;
  }

  public int getStatusIntCode(){
    return this.statuscode;
  }

  public void setStatusIntCode(int status) { this.statuscode = status; }

  public boolean ok(){
    if (this.statuscode < 400 )
      return true;
    return false;
  }
}
