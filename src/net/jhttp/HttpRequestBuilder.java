package net.jhttp;

import java.io.IOException;

/**
 * Builder for http requests. Can be used by itself to produce http requests
 * that are not bound to a {@link HttpClient} to create unbound requests, but
 * is more commonly used when bound. A bound request builder can be used to 
 * actually execute http requests and retrieve responses.
 */
public interface HttpRequestBuilder {
    /**
     * Associate this builder with the provided {@link HttpClient}. A request
     * builder must be bound to actually execute http requests and retrieve
     * responses.
     *
     * @return this builder
     * @param client the HttpClient to bind to
     */
    HttpRequestBuilder bind(HttpClient client);
    
    /**
     * Start building a new GET request for the specified URL.
     *
     * @return this builder
     * @param url the url to GET
     */
    HttpRequestBuilder GET(String url);

    /**
     * Start building a new request with the specified method and URL.
     * 
     * @param method the http request method to use
     * @param url the url to connect to
     * @return this builder
     */
    HttpRequestBuilder newRequest(String method, String url);

    /**
     * Finish building the http request and return it.
     * 
     * @return the http request.
     */
    HttpRequest build();

    /**
     * Finish building the http request and execute it using the bound http
     * client.
     * 
     * @return the http respose
     * @throws IOException if any error occurs
     */
    HttpResponse response() throws IOException;

    /**
     * Finish building the request, execute it, and return the response body.
     *
     * @return the body of the response as a string, or null if there was no
     *   body.
     * @throws IOException if there was an error executing the request.
     */
    String responseBody() throws IOException;
}
