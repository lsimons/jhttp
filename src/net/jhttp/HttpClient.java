package net.jhttp;

import java.io.IOException;

/**
 * Main work interface for this package. Represents a configured client that 
 * is ready to make outgoing http requests. May keep hold of connections, may 
 * be backed by a connection pool, etc. Most of its methods follow the 
 * builder pattern and produce a {@link HttpRequestBuilder}.
 */
public interface HttpClient {
    /**
     * Start creating a GET request object bound to this http client.
     * 
     * @param url the (absolute) url to GET 
     * @return a new request builder
     */
    HttpRequestBuilder GET(String url);

    /**
     * Start creating a HEAD request object bound to this http client.
     *
     * @param url the (absolute) url to HEAD 
     * @return a new request builder
     */
    HttpRequestBuilder HEAD(String url);

    /**
     * Execute a http request using this http client.
     * 
     * @param request the request to execute
     * @return the http response
     * @throws IOException if any problem occurs
     */
    HttpResponse execute(HttpRequest request) throws IOException;
}
