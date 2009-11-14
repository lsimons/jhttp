package net.jhttp;

class HttpClientBuilderImpl implements HttpClientBuilder {
    public HttpClient build() {
        HttpClientImpl c = new HttpClientImpl();
        return c;
    }
}
