package net.jhttp;

import java.io.IOException;
import java.net.MalformedURLException;

class HttpRequestBuilderImpl implements HttpRequestBuilder {
    private HttpClient httpClient;
    private HttpRequest request;

    public HttpRequestBuilder GET(String url) {
        return newRequest("GET", url);
    }

    public HttpRequestBuilder newRequest(String method, String url) {
        try {
            request = new HttpRequestImpl(method, url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public HttpRequestBuilder bind(HttpClient client) {
        this.httpClient = client;
        return this;
    }

    public HttpRequest build() {
        try {
            return request;
        } finally {
            request = null;
        }
    }

    public HttpResponse response() throws IOException {
        return httpClient.execute(build());
    }

    public String responseBody() throws IOException {
        return response().getBodyAsString();
    }
}
