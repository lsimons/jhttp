package net.jhttp;

class HttpResponseImpl extends HttpMessageImpl implements HttpResponse {
    private int statusCode;
    private String reasonPhrase;

    public HttpResponseImpl(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

}
