package net.jhttp;

import org.testng.annotations.Test;

@Test(groups={"func"})
public class HttpTest {
    @Test
    public void testProtectedConstructor() {
        Http h = new MyHttp();
    }
    
    @Test
    public void testClientBuilder() {
        HttpClientBuilder b = Http.clientBuilder();
        assert b != null;
    }
    
    @Test
    public void testClient() {
        HttpClient c = Http.client();
        assert c != null;
    }
    
    @Test
    public void testRequest() {
        HttpRequestBuilder b = Http.requestBuilder();
        assert b != null;
    }

    private static class MyHttp extends Http {
        public MyHttp() {
            super();
        }
    }
}
