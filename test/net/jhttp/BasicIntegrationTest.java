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

    @Test
    public void optionsApache() throws Exception {
        String u = "http://www.apache.org/";
        HttpResponse res = Http.clientBuilder()
                .build()
                .OPTIONS(u).response();
        int code = res.getStatusCode();
        assert 200 <= code && code <= 400;
        Map<String, String> headers = res.getHeaders();
        String allow = headers.get("Allow");
        assert allow != null;
        assert allow.toUpperCase().contains("GET");
    }

    @Test
    public void optionsApacheServer() throws Exception {
        String u = "http://www.apache.org/";
        HttpClient c = Http.clientBuilder()
                .enableTracing(new ConsoleHttpTracer())
                .build();
        HttpRequestBuilder hrb = Http.requestBuilder();
        HttpRequest req = hrb.OPTIONS("*")
                .header("Host", "www.apache.org")
                .build();
        HttpResponse res = c.execute(req);
        int code = res.getStatusCode();
        assert 200 <= code && code <= 400;
    }

    @Test
    public void traceApache() throws Exception {
        String u = "http://www.apache.org/";
        HttpResponse res = Http.clientBuilder()
                .build()
                .TRACE(u).response();
        int code = res.getStatusCode();
        assert 200 <= code && code <= 400;
        Map<String, String> headers = res.getHeaders();
        String contentType = headers.get("Content-Type");
        assert contentType != null;
        assert contentType.toLowerCase().contains("message/http");
    }

    @Test(groups = {"broken"})
    public void putWorkspace() throws Exception {
        String u = "http://localhost:8080/brickabrack/ws/78BF2E19-5193-47A5-84F6-EC8445DCCA3F";
        HttpResponse res = Http.clientBuilder()
                .enableTracing(new ConsoleHttpTracer())
                .build()
                .PUT(u)
                .header("Content-Type", "application/xml;charset=UTF-8")
                .body(
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<workspace xmlns=\"http://schemas.fabric.bbc.co.uk/brickabrack/v1/\"" +
        " id=\"urn:uuid:78BF2E19-5193-47A5-84F6-EC8445DCCA3F\">\n" +
"  <name>Charles Dickens</name>\n" +
"  <children>\n" +
"    <workspace id=\"urn:uuid:3B13D2F8-347E-4988-8DA3-A2D3F91F2B24\">\n" +
"      <name>Novels</name>\n" +
"    </workspace>\n" +
"  </children>\n" +
"</workspace>"
                ).response();
        int code = res.getStatusCode();
        assert 200 <= code && code <= 300;
    }
}
