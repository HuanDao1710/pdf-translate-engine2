package org.example.dsp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;

public class RequestHelper<R, B> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // set connection timeout  (seconds)
            .readTimeout(60, TimeUnit.SECONDS) // set read timeout(seconds)
            .build();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private B body;
    private final Map<String, String> headers = new HashMap<>();
    private String uri;
    private final Map<String, String> params = new HashMap<>();

    public RequestHelper<R, B> withBody(B body) {
        this.body = body;
        return this;
    }

    public RequestHelper<R, B> withHeader(String header, String value) {
        headers.put(header, value);
        return this;
    }

    public RequestHelper<R, B> withURI(String uri) {
        this.uri = uri;
        return this;
    }

    public RequestHelper<R, B> withParam(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public R post(TypeReference<R> typeReference) throws IOException {
        String paramUri = params.entrySet().stream()
                .map(item -> String.format("%s=%s", item.getKey(), item.getValue()))
                .collect(Collectors.joining("&"));
        String url = uri;
        if (StringUtils.isNotBlank(paramUri)) {
            url = uri + "?" + paramUri;
        }
        var builder = new Request.Builder()
                .url(url)
                .header("Content-type", "application/json")
                .header("Accept", "application/json");

        if (Objects.nonNull(body)) {
            builder.post(RequestBody.create(objectMapper.writeValueAsBytes(body)));
        } else {
            byte[] empty = {};
            builder.post(RequestBody.create(empty));
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        Call call = client.newCall(builder.build());
        try (var response = call.execute()) {
            if (response.code() >= 200 && response.code() < 300) {
                assert response.body() != null;
                return objectMapper.readValue(response.body().string(), typeReference);
            }
            throw  new RuntimeException();
        }
    }
}
