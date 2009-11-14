package net.jhttp;

class HttpClientImpl implements HttpClient {
    public HttpRequestBuilder GET(String url) {
        return Http.requestBuilder().bind(this).GET(url);
    }
}
