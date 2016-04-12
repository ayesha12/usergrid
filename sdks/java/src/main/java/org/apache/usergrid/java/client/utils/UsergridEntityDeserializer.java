package org.apache.usergrid.java.client.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.usergrid.java.client.model.UsergridEntity;

import java.io.IOException;

public class UsergridEntityDeserializer extends JsonDeserializer<UsergridEntity> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public UsergridEntity deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        UsergridEntity entity = UsergridEntityDeserializer.objectMapper.readValue(jsonParser,UsergridEntity.class);
        Class<? extends UsergridEntity> entitySubClass = UsergridEntity.customSubclassForType(entity.getType());
        if( entitySubClass != null ) {
            entity = JsonUtils.mapper.convertValue(entity,entitySubClass);
        }
        return entity;
    }
}
