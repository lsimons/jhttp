package net.jhttp;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;

@Test(groups={"func"})
public class HttpRequestBuilderImplTest {
    HttpRequestBuilder hrb;
    HttpClient hc;
    
    HttpRequestBuilder newInstance() {
        return new HttpRequestBuilderImpl();
    }
    
    void replayAll() {
        replay(hc);
    }
    
    void verifyAll() {
        verify(hc);
    }
    
    @BeforeMethod
    public void setUp() {
        hrb = newInstance();
        hc = createMock(HttpClient.class);
    }
    
    @Test
    public void testGET() {
        HttpRequestBuilder hrb2 = hrb.GET("http://www.example.org/");
        assert hrb2 != null;
        assert hrb2 == hrb;
    }
    
    @Test
    public void testBind() {
        replayAll();
        hrb.bind(hc);
        verifyAll();
    }
    
    @Test(groups={"broken"})
    public void responseBody() throws IOException {
        replayAll();
        hrb.bind(hc);
        String body = hrb.GET("http://www.example.org/").responseBody();
        assert body != null;
        verifyAll();
    }
}
