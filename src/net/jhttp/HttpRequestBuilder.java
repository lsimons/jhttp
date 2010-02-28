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
     * Start building a new HEAD request for the specified URL.
     *
     * @return this builder
     * @param url the url to HEAD
     */
    HttpRequestBuilder HEAD(String url);

    /**
     * Start building a new GET request for the specified URL.
     *
     * @return this builder
     * @param url the url to PUT
     */
    HttpRequestBuilder PUT(String url);

    /**
     * Start building a new POST request for the specified URL.
     *
     * @return this builder
     * @param url the url to POST
     */
    HttpRequestBuilder POST(String url);

    /**
     * Start building a new DELETE request for the specified URL.
     *
     * @return this builder
     * @param url the url to DELETE
     */
    HttpRequestBuilder DELETE(String url);

    /**
     * Start building a new OPTIONS request for the specified URL.
     *
     * @return this builder
     * @param url the url to OPTIONS
     */
    HttpRequestBuilder OPTIONS(String url);

    /**
     * Start building a new TRACE request for the specified URL.
     *
     * @return this builder
     * @param url the url to TRACE
     */
    HttpRequestBuilder TRACE(String url);

    /**
     * Add a request header to this builder.
     * 
     * @param name the header name. Should not be null.
     * @param value the header value. Can be null. Use an empty string to set
     *     an empty header.
     * @return this builder
     */
    HttpRequestBuilder header(String name, String value);

    /**
     * Set the request body for this builder.
     *
     * @param body the request body. Should not be null.
     * @return this builder
     */
    HttpRequestBuilder body(String body);

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
