package net.jhttp;

import java.io.IOException;

class HttpClientImpl implements HttpClient {
    private RequestExecutor requestExecutor;

    HttpClientImpl() {
        requestExecutor = new DefaultRequestExecutor();
    }

    void setRequestExecutor(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    public HttpRequestBuilder GET(String url) {
        return Http.requestBuilder().bind(this).GET(url);
    }

    public HttpRequestBuilder HEAD(String url) {
        return Http.requestBuilder().bind(this).HEAD(url);
    }

    public HttpResponse execute(HttpRequest request) throws IOException {
        return requestExecutor.execute(request);
    }

    public void setHttpTracer(HttpTracer tracer) {
        requestExecutor.setHttpTracer(tracer);
    }
}
