package net.jhttp;

import static net.jhttp.Util.asciiCopy;
import static net.jhttp.Protocol.isCHAR;
import static net.jhttp.Protocol.isSeparator;
import static net.jhttp.Protocol.isToken;
import static net.jhttp.Protocol.isTEXT;
import static net.jhttp.Protocol.BACKSLASH;

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
    private static boolean validHttpVersion(ByteBuffer field) {
        return httpVersion.matcher(asciiCopy(field)).matches();
    }

    public void startLineSecondField(ByteBuffer field) {
        assert field != null;
        assert field.remaining() == 3;
        assert validResponseCode(field);
        delegate.startLineSecondField(field);
    }

    private final static Pattern httpResponseCode = Pattern.compile(
            "^[0-9][0-9][0-9]$");
    private static boolean validResponseCode(ByteBuffer field) {
        return httpResponseCode.matcher(asciiCopy(field)).matches();
    }

    private final static int MAX_REASON_PHRASE_LENGTH = 4096;
    public void startLineThirdField(ByteBuffer field) {
        assert field != null;
        assert field.remaining() <= MAX_REASON_PHRASE_LENGTH;
        assert validReasonPhrase(field);
        delegate.startLineThirdField(field);
    }

    private static boolean validReasonPhrase(ByteBuffer bb) {
        final int pos = bb.position();
        try {
            while (bb.remaining() > 0) {
                int b = bb.get();
                if (!isTEXT((byte) b)) {
                    return false;
                }
            }
        } finally {
            bb.position(pos);
        }
        return true;
    }

    private final static int MAX_HEADER_NAME_LENGTH = 512;
    private final static int MAX_HEADER_VALUE_LENGTH = 4096;
    public void header(ByteBuffer name, ByteBuffer value) {
        assert name != null;
        assert name.remaining() <= MAX_HEADER_NAME_LENGTH;
        assert value == null || value.remaining() <= MAX_HEADER_VALUE_LENGTH;
        //assert validHeaderName(name);
        assert validHeaderValue(value);
        delegate.header(name, value);
    }

    /* this is already done by the Parser:
    private static boolean validHeaderName(ByteBuffer bb) {
        if (bb.remaining() == 0) { return false; }
        
        final int pos = bb.position();
        try {
            while(bb.remaining() > 0) {
                int b = bb.get();
                if(!isToken((byte)b)) {
                    return false;
                }
            }
        } finally {
            bb.position(pos);
        }
        return true;
    }*/

    private static boolean validHeaderValue(ByteBuffer bb) {
        if (bb == null || bb.remaining() == 0) {
            return true;
        }
        /* field-value    = *( field-content | LWS )
           field-content  = <the OCTETs making up the field-value
                        and consisting of either *TEXT or combinations
                        of token, separators, and quoted-string>
           quoted-string  = ( <"> *(qdtext | quoted-pair ) <"> )
           qdtext         = <any TEXT except <">>
           quoted-pair    = "\" CHAR */
        // TODO parse fully rather than just check validity of characters

        final int pos = bb.position();
        int lastB = 0;
        try {
            while (bb.remaining() > 0) {
                byte b = bb.get();
                if (isTEXT(b)) {
                    lastB = b;
                    continue;
                }
                if (isToken( b)) {
                    lastB = b;
                    continue;
                }
                if (isSeparator(b)) {
                    lastB = b;
                    continue;
                }
                if (isCHAR(b) && lastB == BACKSLASH) {
                    lastB = b;
                    continue;
                }
                return false;
            }
        } finally {
            bb.position(pos);
        }
        return true;
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
