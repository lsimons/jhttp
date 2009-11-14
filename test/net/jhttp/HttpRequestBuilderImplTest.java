package net.jhttp;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.easymock.EasyMock.*;
import org.easymock.Capture;

import java.io.IOException;

@Test(groups={"func"})
public class HttpRequestBuilderImplTest {
    HttpRequestBuilder hrb;
    HttpClient hc;
    HttpResponse res;
    
    HttpRequestBuilder newInstance() {
        return new HttpRequestBuilderImpl();
    }
    
    void replayAll() {
        replay(hc, res);
    }
    
    void verifyAll() {
        verify(hc, res);
    }
    
    @BeforeMethod
    public void setUp() {
        hrb = newInstance();
        hc = createMock(HttpClient.class);
        res = createMock(HttpResponse.class);
    }
    
    @Test
    public void testGET() {
        HttpRequestBuilder hrb2 = hrb.GET("http://www.example.org/");
        assert hrb2 != null;
        assert hrb2 == hrb;
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBadGET() throws Exception {
        hrb.GET("httpfffft!");
    }

    @Test
    public void testBind() {
        replayAll();
        hrb.bind(hc);
        verifyAll();
    }
    
    public void responseBody() throws IOException {
        String expectedBody = "Response body";
        String requestURL = "http://www.example.org/";
        String requestHost = "www.example.org";
        int requestPort = -1;
        String requestURI = "/";

        Capture<HttpRequest> c = new Capture<HttpRequest>();
        expect(hc.execute(capture(c))).andReturn(res);
        expect(res.getBodyAsString()).andReturn(expectedBody);

        replayAll();

        hrb.bind(hc);
        String body = hrb.GET(requestURL).responseBody();

        HttpRequest capturedRequest = c.getValue();
        assert requestURI.equals(capturedRequest.getRequestURI());
        assert requestHost.equals(capturedRequest.getHost());
        assert requestPort == capturedRequest.getPort();
        
        assert expectedBody.equals(body);
        
        verifyAll();
    }
}
