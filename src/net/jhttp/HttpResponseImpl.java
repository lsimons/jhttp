package net.jhttp;

class HttpResponseImpl implements HttpResponse {
    private int statusCode;
    private String reasonPhrase;
    private String bodyString;
    
    public HttpResponseImpl(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public void setBody(String responseBodyString) {
        this.bodyString = responseBodyString;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getBodyAsString() {
        return this.bodyString;
    }
}
