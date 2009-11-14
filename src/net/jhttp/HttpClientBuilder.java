package net.jhttp;

/**
 * Builder that allows creating a custom {@link HttpClient} instance. Use 
 * this to tune the http client behavior and/or to set options common to all
 * http requests made using a client.
 */
public interface HttpClientBuilder {
    /**
     * Finalize building the http client and return the final result. The
     * builder should be discarded after this call.
     * 
     * @return a ready-to-go http client
     */
    HttpClient build();
}
