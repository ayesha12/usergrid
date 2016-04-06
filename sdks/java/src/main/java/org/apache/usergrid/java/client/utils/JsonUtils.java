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
package org.apache.usergrid.java.client.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.apache.usergrid.java.client.exception.UsergridException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class JsonUtils {

    @NotNull private static ObjectMapper mapper = new ObjectMapper();

    @NotNull
    public static ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }

    @Nullable
    public static String getStringProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name) {
        JsonNode value = properties.get(name);
        if (value != null) {
            return value.asText();
        }
        return null;
    }

    public static void setStringProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name, @Nullable final String value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, JsonNodeFactory.instance.textNode(value));
        }
    }

    public static void setArrayProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name, @Nullable final ArrayList<Object> value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, JsonNodeFactory.instance.POJONode(value));
        }
    }

    @Nullable
    public static Long getLongProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name) {
        JsonNode value = properties.get(name);
        if (value != null) {
            return value.asLong(0);
        }
        return null;
    }

    public static void setLongProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name, @Nullable final Long value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, JsonNodeFactory.instance.numberNode(value));
        }
    }

    public static void setFloatProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name, @Nullable final Float value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, JsonNodeFactory.instance.numberNode(value));
        }
    }

    @NotNull
    public static Boolean getBooleanProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name) {
        JsonNode value = properties.get(name);
        return value != null && value.asBoolean();
    }

    public static void setBooleanProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name, @Nullable Boolean value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, JsonNodeFactory.instance.booleanNode(value));
        }
    }

    @Nullable
    public static UUID getUUIDProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name) {
        JsonNode value = properties.get(name);
        if (value != null) {
            UUID uuid = null;
            try {
                uuid = UUID.fromString(value.asText());
            } catch (Exception ignore) {
            }
            return uuid;
        }
        return null;
    }

    public static void setUUIDProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name, @Nullable final UUID value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, JsonNodeFactory.instance.textNode(value.toString()));
        }
    }

    @NotNull
    public static String toJsonString(@NotNull final Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonGenerationException e) {
            throw new UsergridException("Unable to generate json", e);
        } catch (JsonMappingException e) {
            throw new UsergridException("Unable to map json", e);
        } catch (IOException e) {
            throw new UsergridException("IO error", e);
        }
    }

    @NotNull
    public static <T> T parse(@NotNull final String json, @NotNull final Class<T> c) {
        try {
            return mapper.readValue(json, c);
        } catch (JsonGenerationException e) {
            throw new UsergridException("Unable to generate json", e);
        } catch (JsonMappingException e) {
            throw new UsergridException("Unable to map json", e);
        } catch (IOException e) {
            throw new UsergridException("IO error", e);
        }
    }

    @NotNull
    public static JsonNode toJsonNode(@NotNull final Object obj) {
        return mapper.convertValue(obj, JsonNode.class);
    }

    @NotNull
    public static <T> T fromJsonNode(@NotNull final JsonNode json, @NotNull final Class<T> c) {
        try {
            JsonParser jp = json.traverse();
            return mapper.readValue(jp, c);
        } catch (JsonGenerationException e) {
            throw new UsergridException("Unable to generate json", e);
        } catch (JsonMappingException e) {
            throw new UsergridException("Unable to map json", e);
        } catch (IOException e) {
            throw new UsergridException("IO error", e);
        }
    }

    @Nullable
    public static <T> T getObjectProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name, @NotNull final Class<T> c) {
        JsonNode value = properties.get(name);
        if (value != null) {
            return fromJsonNode(value, c);
        }
        return null;
    }

    public static void setObjectProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name, @Nullable final ObjectNode value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, value);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getProperty(@NotNull final Map<String, JsonNode> properties, @NotNull final String name) {
        JsonNode value = properties.get(name);
        if( value == null ) {
            return null;
        } else if (value instanceof TextNode) {
            return (T) value.asText();
        } else if (value instanceof LongNode) {
            Long valueLong = value.asLong();
            return (T) valueLong;
        } else if (value instanceof BooleanNode) {
            Boolean valueBoolean = value.asBoolean();
            return (T) valueBoolean;
        } else if (value instanceof IntNode) {
            Integer valueInteger = value.asInt();
            return (T) valueInteger;
        } else if (value instanceof FloatNode) {
            return (T) Float.valueOf(value.toString());
        } else {
            return (T) value;
        }
    }
}
