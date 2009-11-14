package net.jhttp;

class HttpClientImpl implements HttpClient {
    public HttpRequestBuilder GET(String url) {
        return Http.request().bind(this).GET(url);
    }
}
