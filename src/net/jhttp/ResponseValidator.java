package net.jhttp;

import static net.jhttp.Util.asciiCopy;

import java.nio.ByteBuffer;
import java.util.regex.Pattern;

class ResponseValidator implements Parser.Listener {
    private Parser.Listener delegate;

    public ResponseValidator(Parser.Listener delegate) {
        assert delegate != null;
        this.delegate = delegate;
    }

    public void messageStart() {
        delegate.messageStart();
    }

    public void startLineFirstField(ByteBuffer field) {
        assert field != null;
        assert field.remaining() == 8;
        assert validHttpVersion(field);
        delegate.startLineFirstField(field);
    }

    private final static Pattern httpVersion = Pattern.compile(
            "^HTTP/[0-9].[0-9]$");
    private boolean validHttpVersion(ByteBuffer field) {
        return httpVersion.matcher(asciiCopy(field)).matches();
    }

    public void startLineSecondField(ByteBuffer field) {
        assert field != null;
        assert field.remaining() == 3;
        assert validResponseCode(field);
        delegate.startLineSecondField(field);
    }

    private final static Pattern httpResponseCode = Pattern.compile(
            "^[2345][012][0-9]$");
    private boolean validResponseCode(ByteBuffer field) {
        return httpResponseCode.matcher(asciiCopy(field)).matches();
    }

    private final static int MAX_REASON_PHRASE_LENGTH = 4096;
    public void startLineThirdField(ByteBuffer field) {
        assert field != null;
        assert field.remaining() <= MAX_REASON_PHRASE_LENGTH;
        delegate.startLineThirdField(field);
    }

    public void header(ByteBuffer name, ByteBuffer value) {
        assert name != null;
        // TODO implement
        delegate.header(name, value);
    }

    public void trailer(ByteBuffer name, ByteBuffer value) {
        assert name != null;
        // TODO implement
        delegate.trailer(name, value);
    }

    public void messageComplete() {
        delegate.messageComplete();
    }

    public void bodyPart(ByteBuffer bodyPart) {
        assert bodyPart != null;
        delegate.bodyPart(bodyPart);
    }
}
