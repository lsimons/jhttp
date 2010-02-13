package net.jhttp;

import static net.jhttp.Protocol.*;
import static net.jhttp.Util.*;

import java.nio.ByteBuffer;
import java.io.IOException;

// see parser_notes.txt for implementation notes
class Parser {
    // maximum size of buffer to accumulate between parse(...) invocations
    final static int MAX_ACCUMULATION_SIZE = 10000;
    
    // receives data as we find it while parsing
    final Listener l;
    
    Parser(Listener l) {
        this.l = l;
        reset();
    }

    // we were somehow still stuck parsing stuff, and getting forced out
    // of that situation. Better close the connection, too...
    public void forceFinish() {
        if(state == S.BG) {
            return;
        }
        if (lastHeaderName != null) {
            l.header(lastHeaderName, null);
            lastHeaderName = null;
        }
        l.messageComplete();
        reset();
    }

    // possible parser states
    enum S {
        BG,    // begin of message
        //no LF, this is a substate handled with a boolean needLF
        //no W,  this is a substate handled with a boolean skipWS
        F_1,   // in first header field
        F_1_2, // looking for second header field
        F_2,   // in second header field
        F_2_3, // looking for third header field
        F_3,   // in third header field
        H,     // looking for header
        HN,    // in header name
        HV,    // looking for header value or continuation of header value
        HVV,   // inside header value
        BS,    // looking for body
        B      // reading body
    }
    
    // all parser state
    S state;                   // current parser state
    boolean needLF;            // got CR, expect LF next
    boolean skipWS;            // hop over CR LF SP HT
    ByteBuffer accum;          // remaining bytes from buffer
    ByteBuffer lastHeaderName; // header name found
    boolean hasBody;           // whether message comes with body
    int contentLength;         // length of message body
    int remainingContent;      // how much of the message body is not read yet
    int fieldStart;            // where current field being read starts
    int fieldOffset;           // bytes out of accum to skip over
    byte b;                    // current byte
    
    // state is re-initialized after parsing a message, except for the
    // accum variable which can hold leftovers from that parse
    void reset() {
        state = S.BG;
        needLF = false;
        skipWS = false;
        lastHeaderName = null;
        hasBody = true;
        contentLength = -1;
        remainingContent = Integer.MAX_VALUE;
        fieldStart = 0;
        fieldOffset = 0;
        b = 0;
    }
    
    // should be called if the request that caused this response is a HEAD
    // request (which doesn't have a body)
    void expectHeadRequest(boolean expectHeadRequest) {
        if(state != S.BG) {
            throw new IllegalStateException(
                    "Can't change expectations mid-parse");
        }
        hasBody = expectHeadRequest;
    }

    // read from bb until it is exhausted or we finish a request
    void parse(ByteBuffer bb) throws IOException {
        if (accum != null) {
            // we had some bytes remaining from a previous parse(...)
            // TODO see if it is feasible to avoid this data copying, though
            // modulo big values we are dealing with only 3000 bytes or so
            int accumSize = accum.remaining();
            int bbSize = bb.remaining();
            byte[] b = new byte[accumSize + bbSize];
            accum.get(b, 0, accumSize);
            bb.get(b, accumSize, bbSize);
            bb = ByteBuffer.wrap(b);
            accum = null;
        }
        
        // we may have some bytes that are outside fields based on a previous
        // parse(...)
        fieldStart = bb.position() + fieldOffset;
        fieldOffset = 0;

        if (state == S.B) {
            boolean LFOK = eatLF(bb);
            if(!LFOK) {
                return;
            }
            // looks like we landed up in body state after the last parse(...)
            handleBody(bb);
            
            // there will only be any trailing data if we finished and found a
            // whole new message
            saveTrailingData(bb);
            return;
        }
        
        // try and read the status line and the headers
        while(bb.hasRemaining()) {
            boolean keepParsing = parseOneHeaderByte(bb);
            if (!keepParsing) {
                break;
            }
        }
        boolean LFOK = eatLF(bb);
        if(!LFOK) {
            return;
        }

        if (state == S.B) {
            // we arrive here only after completing the while(), so if we 
            // are here it is because we just _changed_ to B state
            if (!hasBody || contentLength == 0) {
                l.messageComplete();
                reset();
            } else {
                if(contentLength != -1) {
                    remainingContent = contentLength;
                }
                handleBody(bb);
            }
        }

        saveTrailingData(bb);
    }

    /**
     * Skip over an expected LF.
     *
     * Todo: fix the exception/assertion design of this method
     * 
     * @param bb the buffer to read a byte from
     * @return true if we skipped over, false otherwise
     * @throws IOException if a CR is not followed by LF
     */
    boolean eatLF(ByteBuffer bb) throws IOException {
        if(needLF) {
            if(bb.hasRemaining()) {
                b = bb.get();
                if (b != LF) {
                    throw new IOException("CR not followed by LF");
                }
                needLF = false;
                return true;
            }
            return false;
        }
        return true;
    }

    // process body bytes
    void handleBody(ByteBuffer bb) throws IOException {
        if(!hasBody || contentLength == 0 || remainingContent == 0) {
            l.messageComplete();
            reset();
            return;
        }

        int bbSize = bb.remaining() - fieldStart;
        if(bbSize < remainingContent) {
            emit(S.B, bb, fieldStart, bb.limit());
            remainingContent -= bbSize;
        } else if(bbSize > remainingContent) {
            int limit = fieldStart + remainingContent;
            emit(S.B, bb, fieldStart, limit);
            l.messageComplete();
            reset();
            remainingContent = 0;
        } else {
            emit(S.B, bb, fieldStart, bb.limit());
            l.messageComplete();
            reset();
            remainingContent = 0;
        }
        fieldStart = bb.position();
    }

    /**
     * Reads one byte from the buffer.
     * 
     * Todo: fix the exception/assertion design of this method
     * 
     * @param bb the buffer to read from
     * @return true if parsing should continue, false otherwise
     * @throws IOException if a CR is not followed by LF
     * @throws AssertionError if encountered other illegal byte
     */
    boolean parseOneHeaderByte(ByteBuffer bb) throws IOException {
        b = bb.get();
        
        if (needLF) {
            if (b != LF) {
                throw new IOException("CR not followed by LF");
            }
            needLF = false;
            skipWS = false;
            return true;
        }

        if (skipWS) {
            if (isLWS(b)) {
                if (b == CR) {
                    needLF = true;
                }
                return true;
            }
            skipWS = false;
        }

        switch(state) {
            case BG:
                state = S.F_1;
                l.messageStart();
                // falls through
            case F_1:
                if (b == SP) {
                    emit(S.F_1, bb, fieldStart, bb.position() - 1);
                    state = S.F_1_2;
                } else {
                    assert isVersion(b) || isMethod(b);
                }
                break;
            case F_1_2:
                state = S.F_2;
                fieldStart = bb.position() - 1;
                // falls through
            case F_2:
                if (b == SP) {
                    emit(S.F_2, bb, fieldStart, bb.position() - 1);
                    state = S.F_2_3;
                } else {
                    assert isStatusCode(b) || isRequestURI(b);
                }
                break;
            case F_2_3:
                state = S.F_3;
                fieldStart = bb.position() - 1;
                // falls through
            case F_3:
                if (b == CR) {
                    emit(S.F_3, bb, fieldStart, bb.position() - 1);
                    needLF = true;
                    state = S.H;
                    fieldStart = bb.position() + 1;
                } else {
                    if (!isReasonPhrase(b) && !isVersion(b)) {
                        assert isReasonPhrase(b) || isVersion(b);
                    } else {
                        assert isReasonPhrase(b) || isVersion(b);
                    }
                }
                break;
            case H:
                switch (b) {
                    case CR:
                        needLF = true;
                        state = S.B;
                        return false;
                    default:
                        state = S.HN;
                        assert isHeaderName(b);
                        break;
                }
                break;
            case HN:
                if (b == COLON) {
                    emit(S.HN, bb, fieldStart, bb.position() - 1);
                    skipWS = true;
                    state = S.HVV;
                    fieldStart = bb.position();
                } else {
                    assert isHeaderName(b);
                }
                break;
            case HV:
                switch(b) {
                    case SP:
                    case HT:
                        skipWS = true;
                        state = S.HVV;
                        break;
                    case CR:
                        emit(S.HVV, bb, fieldStart, bb.position() - 3);
                        needLF = true;
                        state = S.B;
                        fieldStart = bb.position() + 1;
                        return false;
                    default:
                        assert isHeaderValue(b);
                        emit(S.HVV, bb, fieldStart, bb.position() - 3);
                        state = S.HN;
                        fieldStart = bb.position() - 1;
                        break;
                }
                break;
            case HVV:
                if (b == CR) {
                    needLF = true;
                    state = S.HV;
                } else {
                    assert isHeaderValue(b);
                }
                break;
            default:
                throw new RuntimeException("todo");
        }
        return true;
    }

    // populate accum with data we didn't process
    void saveTrailingData(ByteBuffer bb) throws IOException {
        if (fieldStart < bb.position()) {
            // there is some remaining data
            fieldOffset = 0;

            if (bb.remaining() > MAX_ACCUMULATION_SIZE) {
                throw new IOException("Too much remaining data");
            }
            accum = copy(bb);
        } else if (fieldStart > bb.position()) {
            // skip some characters from the next buffer
            fieldOffset = fieldStart - bb.position();
        } else {
            // exhausted buffer, which ended on a field
            fieldOffset = 0;
        }
    }

    // hand off some chunk of data to the configured Parser.Listener
    void emit(S state, ByteBuffer bb, int start, int end) throws IOException {
        ByteBuffer view = null;
        
        if (bb != null) {
            view = bb.asReadOnlyBuffer();
            view.position(start);
            view.limit(end);
        }

        switch (state) {
            case F_1:
                l.startLineFirstField(view);
                break;
            case F_2:
                checkHasBody(view);
                l.startLineSecondField(view);
                break;
            case F_3:
                l.startLineThirdField(view);
                break;
            case HN:
                if (lastHeaderName != null) {
                    l.header(lastHeaderName, null);
                }
                lastHeaderName = copy(view);
                break;
            case HVV:
                if (hasBody) {
                    findContentLength(lastHeaderName, view);
                }
                l.header(lastHeaderName, view);
                lastHeaderName = null;
                break;
            case BS:
                if (lastHeaderName != null) {
                    l.header(lastHeaderName, view);
                    lastHeaderName = null;
                }
                break;
            case B:
                l.bodyPart(view);
                break;
            default:
                throw new RuntimeException("todo");
        }
    }

    // parse out the Content-Length if it is set
    void findContentLength(ByteBuffer name, ByteBuffer view)
            throws IOException {
        String header = asciiCopy(name);
        if ("Content-Length".equalsIgnoreCase(header)) {
            String value = asciiCopy(view).replaceAll("\\s*", "");
            try {
                contentLength = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IOException(e);
            }
        }
    }

    // set hasBody based on status code
    void checkHasBody(ByteBuffer statusCode) {
        String code = asciiCopy(statusCode);
        if(code.startsWith("1") ||
                code.equals("204") ||
                code.equals("304")) {
            hasBody = false;
        }
    }

    // to get data out of the parser, other code implements this listener
    //   see ResponseAccumulator and ResponseValidator
    interface Listener {
        void messageStart();

        void startLineFirstField(ByteBuffer field);

        void startLineSecondField(ByteBuffer field);

        void startLineThirdField(ByteBuffer field);

        void header(ByteBuffer name, ByteBuffer value);
        
        void trailer(ByteBuffer name, ByteBuffer value);

        void messageComplete();

        void bodyPart(ByteBuffer bodyPart);
    }
}
