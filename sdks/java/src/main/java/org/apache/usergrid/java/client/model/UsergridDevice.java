package org.apache.usergrid.java.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("unused")
public class UsergridDevice extends UsergridEntity {
    @NotNull public static String DEVICE_ENTITY_TYPE = "device";

    @Nullable private String model;
    @Nullable private String platform;
    @Nullable private String osVersion;

    public UsergridDevice() {
        super(DEVICE_ENTITY_TYPE);
    }

    public UsergridDevice(@NotNull final Map<String, JsonNode> properties) {
        super(DEVICE_ENTITY_TYPE,null,properties);
    }

    @Nullable @JsonProperty("deviceModel")
    public String getModel() {
        return this.model;
    }
    @JsonProperty("deviceModel")
    public void setModel(@NotNull String model) {
        this.model = model;
    }

    @Nullable @JsonProperty("devicePlatform")
    public String getPlatform() {
        return this.platform;
    }
    @JsonProperty("devicePlatform")
    public void setPlatform(@Nullable String platform) {
        this.platform = platform;
    }

    @Nullable @JsonProperty("deviceOSVersion")
    public String getOsVersion() {
        return this.osVersion;
    }
    @JsonProperty("deviceOSVersion")
    public void setOsVersion(@Nullable String osVersion) {
        this.osVersion = osVersion;
    }
}