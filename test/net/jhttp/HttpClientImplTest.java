package net.jhttp;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.easymock.EasyMock.*;


@Test(groups={"func"})
public class HttpClientImplTest {
    HttpClientImpl hc;
    HttpRequest req;
    HttpResponse res;
    RequestExecutor re;

    HttpClientImpl newInstance() {
        return new HttpClientImpl();
    }

    void replayAll() {
        replay(req, res, re);
    }

    void verifyAll() {
        verify(req, res, re);
    }

    @BeforeMethod
    public void setUp() {
        hc = newInstance();
        req = createMock(HttpRequest.class);
        res = createMock(HttpResponse.class);
        re = createMock(RequestExecutor.class);
    }
    
    @Test
    public void testGET() {
        HttpRequestBuilder hcb = hc.GET("http://example.org/");
        assert hcb != null;
    }
    
    @Test
    public void testExecute() throws Exception {
        expect(re.execute(req)).andReturn(res);
        replayAll();
        
        hc.setRequestExecutor(re);
        HttpResponse res = hc.execute(req);
        
        verifyAll();
    }
}
