package net.jhttp;

import org.testng.annotations.Test;

import java.util.Map;

@Test(groups={"int"})
public class BasicIntegrationTest {
    @Test
    public void getGoogle() throws Exception {
        String b = Http.client().GET("http://www.google.co.uk/").responseBody();
        assert b != null;
        assert b.contains("Search");
    }

    @Test
    public void getGoogleAsUserAgent() throws Exception {
        String b = Http.client().GET("http://www.google.co.uk/")
                .header("User-Agent", "jhttp-integration-test/0.0.1")
                .responseBody();
        assert b != null;
        assert b.contains("Search");
    }

    @Test
    public void getGoogleWithoutSettingContentLength() throws Exception {
        String b = Http.client().GET("http://www.google.co.uk/")
                .header("Content-Length", null)
                .responseBody();
        assert b != null;
        assert b.contains("Search");
    }

    @Test
    public void getGoogleWithANullHeader() throws Exception {
        String b = Http.client().GET("http://www.google.co.uk/")
                .header(null, "this still works, amazingly")
                .responseBody();
        assert b != null;
        assert b.contains("Search");
    }

    @Test
    public void getGoogleWhileTracing() throws Exception {
        HttpClient client = Http.clientBuilder()
                .enableTracing(new ConsoleHttpTracer())
                .build();
        String b = client.GET("http://www.google.co.uk/").responseBody();
        assert b != null;
        assert b.contains("Search");
    }

    @Test
    public void getGoogle404() throws Exception {
        String u = "http://www.google.com/thispagedoesnotexist/";
        int code = Http.client().GET(u).response().getStatusCode();
        assert 404 == code;
    }

    @Test
    public void getGoogleSSL() throws Exception {
        String u = "https://www.google.com/";
        int code = Http.client().GET(u).response().getStatusCode();
        assert 300 <= code && code <= 400;
    }

    @Test
    public void headGoogle() throws Exception {
        String u = "http://www.google.co.uk/";
        int code = Http.client().HEAD(u).response().getStatusCode();
        assert 200 <= code && code <= 400;
    }

    @Test
    public void headGoogleHeaders() throws Exception {
        String u = "http://www.google.co.uk/";
        HttpResponse res = Http.client().HEAD(u).response();
        int code = res.getStatusCode();
        assert 200 <= code && code <= 400;
        Map<String, String> headers = res.getHeaders();
        String contentType = headers.get("Content-Type");
        assert contentType != null;
        assert contentType.toLowerCase().contains("text/html");
    }
}
