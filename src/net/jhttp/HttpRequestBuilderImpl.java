package net.jhttp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

class HttpRequestBuilderImpl implements HttpRequestBuilder {
    private HttpClient httpClient;
    private HttpRequestImpl request;

    public HttpRequestBuilder GET(String url) {
        return newRequest("GET", url);
    }

    public HttpRequestBuilder HEAD(String url) {
        return newRequest("HEAD", url);
    }

    public HttpRequestBuilder PUT(String url) {
        return newRequest("PUT", url);
    }

    public HttpRequestBuilder POST(String url) {
        return newRequest("POST", url);
    }

    public HttpRequestBuilder DELETE(String url) {
        return newRequest("DELETE", url);
    }

    public HttpRequestBuilder OPTIONS(String url) {
        if ("*".equals(url)) {
            request = new HttpRequestImpl(null, "OPTIONS", url, null, -1);
        } else {
            newRequest("OPTIONS", url);
        }
        return this;
    }

    public HttpRequestBuilder TRACE(String url) {
        return newRequest("TRACE", url);
    }

    public HttpRequestBuilder header(String name, String value) {
        Map<String, String> headers = request.getHeaders();
        headers.put(name, value);
        return this;
    }

    public HttpRequestBuilder body(String body) {
        request.setBody(body);
        return this;
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
