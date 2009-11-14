package net.jhttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * This basic SimpleRequestExecutor deletes to {@link java.net.URLConnection}.
 * As such it doesn't really implement all the features that we will need, but
 * its easy to start with. It'll be removed eventually.
 */
class SimpleRequestExecutor implements RequestExecutor {
    private static Pattern characterEncodingRE = Pattern.compile(
            "^.*?;\\s*charset\\s*=\\s*([^\\s;]+)\\s*(?:;.*)?$",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    
    public HttpResponse execute(HttpRequest request) throws IOException {
        URL url = HttpRequestImpl.getURL(request);
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(request.getMethod());
        
        conn.connect();
        try {
            int code = conn.getResponseCode();
            String message = conn.getResponseMessage();
            String body = getBodyAsString(conn);

            HttpResponseImpl res = new HttpResponseImpl(code, message);
            res.setBody(body);
            
            return res;
        } catch (FileNotFoundException e) {
            return new HttpResponseImpl(404, "Not Found");
        } finally {
            conn.disconnect();
        }
    }

    static String getBodyAsString(HttpURLConnection conn)
            throws IOException {
        String characterEncoding = sniffCharacterEncoding(conn);

        InputStream is = new BufferedInputStream(conn.getInputStream());
        try {
            Reader r = new InputStreamReader(is, characterEncoding);
            StringWriter w = new StringWriter();

            copy(r, w);

            return w.getBuffer().toString();
        } finally {
            is.close();
        }
    }

    static void copy(Reader r, StringWriter w) throws IOException {
        char[] buf = new char[1024];
        int read;
        while( (read = r.read(buf)) != -1) {
            w.write(buf, 0, read);
        }
    }

    static String sniffCharacterEncoding(HttpURLConnection conn) {
        return sniffCharacterEncoding(conn.getContentType());
    }

    static String sniffCharacterEncoding(String contentType) {
        if(contentType != null) {
            Matcher m = characterEncodingRE.matcher(contentType);
            if (m.matches()) {
                String characterEncoding = m.group(1);
                try {
                    Charset.forName(characterEncoding);
                    return characterEncoding.trim();
                } catch (IllegalArgumentException e) {
                    // fall back
                }
            }
        }
        return "UTF-8";
    }
}
