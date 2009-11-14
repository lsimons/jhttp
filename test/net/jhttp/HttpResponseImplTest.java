package net.jhttp;

import org.testng.annotations.Test;

@Test(groups={"func"})
public class HttpResponseImplTest {
    @Test
    public void testHttpResponseImpl() {
        int statusCode = 200;
        String reasonPhrase = "OK";
        String body = "data";
        
        HttpResponseImpl res = new HttpResponseImpl(statusCode, reasonPhrase);
        res.setBody(body);
        
        assert statusCode == res.getStatusCode();
        assert reasonPhrase.equals(res.getReasonPhrase());
        assert body.equals(res.getBodyAsString());
    }
}
