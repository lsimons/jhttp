package net.jhttp;

import java.io.IOException;

class HttpRequestBuilderImpl implements HttpRequestBuilder {
    private HttpClient httpClient;

    public HttpRequestBuilder GET(String url) {
        return this;
    }

    public HttpRequestBuilder bind(HttpClient client) {
        this.httpClient = client;
        return this;
    }
    
    public String responseBody() throws IOException {
        return null;
    }
}
