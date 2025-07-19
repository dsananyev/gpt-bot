package com.dsa.api;

import com.dsa.util.PropertiesLoader;
import lombok.Getter;

@Getter
public enum ApiProvider {
    OPEN_AI;

    private final String url;
    private final String endpoint;
    private final String contentType;
    private final String token;

    private static final PropertiesLoader loader = new PropertiesLoader();

    ApiProvider() {
        PropertiesLoader loader = new PropertiesLoader();
        String prefix = this.name() + "_";

        this.url = loader.getProperty(prefix + "BASE_URI");
        this.endpoint = loader.getProperty(prefix + "ENDPOINT");
        this.contentType = loader.getProperty(prefix + "CONTENT_TYPE");
        this.token = loader.getProperty(prefix + "API_TOKEN");
    }

}
