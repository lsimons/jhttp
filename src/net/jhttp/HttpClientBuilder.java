package net.jhttp;

/**
 * Builder that allows creating a custom {@link HttpClient} instance. Use 
 * this to tune the http client behavior and/or to set options common to all
 * http requests made using a client.
 */
public interface HttpClientBuilder {
    /**
     * Enable request tracing on this http client. Mostly useful for debug
     * logging.
     *
     * @param tracer the request tracer to use
     * @return this builder
     */
    HttpClientBuilder enableTracing(HttpTracer tracer);

    /**
     * Set a custom {@link RequestExecutor} implementation. Mostly useful for
     * testing.
     * 
     * @param requestExecutor the request executor the http client should use
     * @return this builder
     */
    HttpClientBuilder requestExecutor(RequestExecutor requestExecutor);
    
    /**
     * Finalize building the http client and return the final result. The
     * builder should be discarded after this call.
     * 
     * @return a ready-to-go http client
     */
    HttpClient build();
}
