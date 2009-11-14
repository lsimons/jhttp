package net.jhttp;

class HttpClientBuilderImpl implements HttpClientBuilder {
    HttpClientImpl client = new HttpClientImpl();
    
    public HttpClientBuilder requestExecutor(RequestExecutor requestExecutor) {
        client.setRequestExecutor(requestExecutor);
        return this;
    }

    public HttpClient build() {
        return client;
    }
}
