package com.dsa.api;


import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


@Slf4j
public class ApiService {

    private final OkHttpClient httpClient = new OkHttpClient();

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public Request createRequest(ApiProvider provider, String body) {
        return createRequestBuilder(provider)
                .post(RequestBody.create(body, MediaType.parse(provider.getContentType())))
                .build();
    }


    private Request.Builder createRequestBuilder(ApiProvider provider) {
        log.info("Created Request.Builder for ApiProvider: {}", provider.name());
        return new Request.Builder()
                .url(provider.getUrl() + provider.getEndpoint())
                .addHeader("Authorization", "Bearer " + provider.getToken())
                .addHeader("Content-Type", provider.getContentType());
    }


}
