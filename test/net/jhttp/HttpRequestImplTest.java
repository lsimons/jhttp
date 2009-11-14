package net.jhttp;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import java.net.MalformedURLException;
import java.net.URL;

@Test(groups={"func"})
public class HttpRequestImplTest {
    @Test(dataProvider = "requestData")
    public void testHttpRequestImpl(String method, String url)
            throws Exception {
        HttpRequestImpl req = new HttpRequestImpl(method, url);
        assert method.equals(req.getMethod());
    }
    
    @Test(expectedExceptions = { MalformedURLException.class })
    public void testHttpRequestImplWithBadURL() throws Exception {
        new HttpRequestImpl("GET", "httttttpppppp###");
    }
    
    @Test(dataProvider = "requestData")
    public void testGetURL(String method, String url) throws Exception {
        URL u = HttpRequestImpl.getURL(new HttpRequestImpl(method, url));
        assert url.equals(u.toString());
    }
    
    @DataProvider(name = "requestData")
    public String[][] getRequestData() {
        return new String[][] {
                { "GET", "http://example.org/" },
                { "PUT", "http://example.org:1234/" },
                { "POST", "http://example.org/foo/bar" },
                { "DELETE", "http://example.org/foo/bar?q=2" },
                { "OPTIONS", "http://example.org/foo/bar#w=2" },
                { "HEAD", "https://example.org/" },
                { "IMAGINARY_METHOD", "https://example.org:56789/" },
        };
    }
}
