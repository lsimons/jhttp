package net.jhttp;

/**
 * This is a very basic implementation of {@link HttpTracer} that logs to
 * {@link System#out}.
 */
public class ConsoleHttpTracer implements HttpTracer {
    public void requestLine(String line) {
        System.out.print("> ");
        System.out.println(line);
    }

    public void requestHeader(String headerName, String headerValue) {
        System.out.print("> ");
        System.out.print(headerName);
        System.out.print(": ");
        System.out.println(headerValue);
    }

    public void requestComplete() {
        System.out.println(">");
    }

    public void statusLine(String line) {
        System.out.print("< ");
        System.out.println(line);
    }

    public void responseHeader(String headerName, String headerValue) {
        System.out.print("< ");
        System.out.print(headerName);
        System.out.print(": ");
        System.out.println(headerValue);
    }

    public void responseComplete() {
        System.out.println("<");
    }
}
