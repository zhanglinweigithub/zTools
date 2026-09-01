package com.zhanglinwei.zTools.util;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

public final class HttpUtils {

    private final HttpClient httpClient;

    private String requestUrl;
    private Object requestBody = new Object();
    private String requestBodyString = "{}";
    private Duration timeout = Duration.ofSeconds(10);
    private Map<String, String> requestHeaders = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, String> pathParams = new HashMap<>();

    private int maxRetries = 0;
    private Duration retryInterval = Duration.ofMillis(3000);
    private Function<HttpResponse<String>, Boolean> retryCondition = response -> false;

    private HttpUtils() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public static HttpUtils create() {
        return new HttpUtils();
    }

    public HttpUtils url(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    public HttpUtils headers(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            this.requestHeaders.clear();
        } else {
            this.requestHeaders = headers;
        }

        return this;
    }

    public HttpUtils addHeader(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(this::addHeader);
        }

        return this;
    }

    public HttpUtils contentTypeJson() {
        addHeader("Content-Type", "application/json");
        return this;
    }

    public HttpUtils contentTypeFormData() {
        addHeader("Content-Type", "multipart/form-data");
        return this;
    }

    public HttpUtils contentTypeUrlencoded() {
        addHeader("Content-Type", "application/x-www-form-urlencoded");
        return this;
    }

    public HttpUtils addHeader(String key, String value) {
        if (key != null && !key.isBlank() && value != null && !value.isBlank()) {
            this.requestHeaders.put(key, value);
        }

        return this;
    }

    public HttpUtils queryParams(Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            this.queryParams.clear();
        } else {
            this.queryParams = queryParams;
        }

        return this;
    }

    public HttpUtils addQueryParam(Map<String, String> queryParams) {
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(this::addQueryParam);
        }

        return this;
    }

    public HttpUtils addQueryParam(String key, String value) {
        if (key == null || key.isBlank() || value == null || value.isBlank()) {
            throw new IllegalArgumentException("query params can't be null or blank");
        }
        this.queryParams.put(key, value);

        return this;
    }

    public HttpUtils pathParams(Map<String, String> pathParams) {
        if (pathParams == null || pathParams.isEmpty()) {
            this.pathParams.clear();
        } else {
            this.pathParams = pathParams;
        }

        return this;
    }

    public HttpUtils addPathParam(Map<String, String> pathParams) {
        if (pathParams != null && !pathParams.isEmpty()) {
            pathParams.forEach(this::addPathParam);
        }

        return this;
    }

    public HttpUtils addPathParam(String key, String value) {
        if (key == null || key.isBlank() || value == null || value.isBlank()) {
            throw new IllegalArgumentException("path params can't be null or blank");
        }
        this.pathParams.put(key, value);
        return this;
    }

    public HttpUtils timeout(Duration timeout) {
        if (timeout != null && !timeout.isNegative() && !timeout.isZero()) {
            this.timeout = timeout;
        }

        return this;
    }

    public HttpUtils retry(int maxRetries, Duration interval, Function<HttpResponse<String>, Boolean> condition) {
        if (maxRetries > 0) {
            this.maxRetries = maxRetries;
        }
        if (interval != null && !interval.isNegative() && !interval.isZero()) {
            this.retryInterval = interval;
        }
        if (condition != null) {
            this.retryCondition = condition;
        }
        return this;
    }

    public HttpUtils body(Object requestBody) {
        this.requestBody = requestBody;
        this.requestBodyString = JsonUtil.toJsonString(requestBody, false);
        return this;
    }

    public String doGet() throws Exception {
        return sendRequest("GET", HttpResponse::body);
    }

    public <T> T doGet(Class<T> responseType) throws Exception {
        return sendRequest("GET", (response) -> JsonUtil.parseObject(response.body(), responseType));
    }

    public String doPost() throws Exception {
        return sendRequest("POST", HttpResponse::body);
    }

    public <T> T doPost(Class<T> responseType) throws Exception {
        return sendRequest("POST", (response) -> JsonUtil.parseObject(response.body(), responseType));
    }

    private <T> T sendRequest(String method, Function<HttpResponse<String>, T> parseResponse) throws Exception {
        HttpRequest httpRequest = null;
        HttpResponse<String> httpResponse = null;
        Exception lastException = null;
        int retryCount = 0;
        final String requestId = UUID.randomUUID().toString();

        while (retryCount <= maxRetries) {
            try {
                long startTime = System.currentTimeMillis();

                httpRequest = buildRequest(method);
//                logger.info("[HTTP Request] RetryCount: {} RequestId: {}\n\t{} {}\n\tRequestHeaders:\n\t\t{}\n\tRequestBody:\n\t\t{}",
//                        retryCount, requestId, method, this.requestUrl, headerToLogger(), this.requestBodyString);

                httpResponse = httpClient.send(
                        httpRequest,
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

//                if (httpResponse != null) {
//                    logger.info("[HTTP Response] RetryCount: {} RequestId: {}\n\tStatus: {}, Time: {}ms\n\tResponseHeaders:\n\t\t{}\n\tResponseBody:\n\t\t{}",
//                            retryCount, requestId, httpResponse.statusCode(), System.currentTimeMillis() - startTime, headerToLogger(httpResponse.headers().map()), httpResponse.body());
//                }

                if (retryCount < maxRetries && retryCondition.apply(httpResponse)) {
                    retryCount++;
                    Thread.sleep(retryInterval.toMillis());
                    continue;
                }

                return parseResponse.apply(httpResponse);
            } catch (Exception ex) {
                lastException = ex;

//                logger.info("[Request Exception] RetryCount: {} RequestId: {}\n\tExceptionType: {}, ErrorMsg: {}", retryCount, requestId, lastException.getClass().getSimpleName(), lastException.getMessage());

                if (retryCount >= maxRetries) {
                    throw ex;
                }

                retryCount++;
                Thread.sleep(retryInterval.toMillis());
            }
        }

        throw lastException != null ? lastException : new RuntimeException("Request failed " + requestId);
    }

    private String headerToLogger() {
        if (this.requestHeaders == null || this.requestHeaders.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        this.requestHeaders.forEach((key, value) -> {
            builder.append(key).append(": ").append(value).append("\n\t\t");
        });
        builder.deleteCharAt(builder.length() - 3);
        return builder.toString();
    }

    private String headerToLogger(Map<String, List<String>> headers) {
        if (headers == null || headers.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        headers.forEach((key, value) -> {
            builder.append(key).append(": ");
            if (value != null && !value.isEmpty()) {
                builder.append(String.join(COMMA, value));
            }
            builder.append("\n\t\t");
        });

        builder.deleteCharAt(builder.length() - 3);
        return builder.toString();
    }

    private HttpRequest buildRequest(String method) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(buildRequestUri())
                .method(method, this.requestBody == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(this.requestBodyString, StandardCharsets.UTF_8))
                .timeout(this.timeout);
        requestHeaders.forEach(requestBuilder::header);
        return requestBuilder.build();
    }

    private URI buildRequestUri() {
        if (this.requestUrl == null || requestUrl.isBlank()) {
            throw new IllegalArgumentException("requestUrl can't be null or blank");
        }

        processPathParams();
        processQueryParams();

        return URI.create(this.requestUrl);
    }

    private void processPathParams() {
        if (this.pathParams == null || this.pathParams.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : this.pathParams.entrySet()) {
            String placeholder = LEFT_BRACE + entry.getKey() + RIGHT_BRACE;
            this.requestUrl = this.requestUrl.replace(placeholder, entry.getValue());
        }
    }

    private void processQueryParams() {
        if (this.queryParams == null || this.queryParams.isEmpty()) {
            return;
        }
        StringJoiner queryJoiner = new StringJoiner(AMPERSAND);
        this.queryParams.forEach((k, v) -> queryJoiner.add(k + EQUAL + encodeQueryParam(v)));

        if (this.requestUrl.contains(QUESTION_MARK)) {
            this.requestUrl = this.requestUrl + AMPERSAND + queryJoiner;
        } else {
            this.requestUrl = this.requestUrl + QUESTION_MARK + queryJoiner;
        }
    }

    private String encodeQueryParam(String value) {
        return value.replace(SPACE, URLEncoder.encode(SPACE, StandardCharsets.UTF_8))
                .replace(AMPERSAND, URLEncoder.encode(AMPERSAND, StandardCharsets.UTF_8))
                .replace(EQUAL, URLEncoder.encode(EQUAL, StandardCharsets.UTF_8));
    }
}
