package net.jhttp;

import static net.jhttp.Util.ascii;

import java.util.Arrays;

class Protocol {
    static final int SP = 32;
    static final int CR = 13;
    static final int LF = 10;

    static final int HT = 9;
    static final int COLON = 58;
    
    static final int DEL = 127;
    
    static final int BACKSLASH = 92;

    static final byte[] HTTP_VERSION = ascii("HTTP/1.0");
    
    Protocol() {}
    
    static boolean isLWS(byte b) {
        return (b == SP) || (b == HT) || (b == CR) || (b == LF);
    }
    
    /* CHAR = <any US-ASCII character (octets 0 - 127)> */
    static boolean isCHAR(byte b) {
        return (b >= 0 && b <= 127);
    }
    
    /* CTL = <any US-ASCII control character
             (octets 0 - 31) and DEL (127)> */
    static boolean isCTL(byte b) {
        return ((b >= 0 && b <= 31) || b == DEL);
    }
    
    /* TEXT = <any OCTET except CTLs,
              but including LWS> */
    static boolean isTEXT(byte b) {
        return !isCTL(b) || isLWS(b);
    }
    
    /* separators = "(" | ")" | "<" | ">" | "@"
                    | "," | ";" | ":" | "\" | <">
                    | "/" | "[" | "]" | "?" | "="
                    | "{" | "}" | SP | HT */
    static final int[] separators = new int[] {
            28, 29, 60, 62, 64,
            44, 59, 58, 92, 22,
            123, 125, SP, HT
    };
    static { Arrays.sort(separators); }
    static boolean isSeparator(byte b) {
        return Arrays.binarySearch(separators, b) >= 0;    
    }
    
    static boolean isToken(byte b) {
        return isCHAR(b) && (!isCTL(b)) && (!isSeparator(b)); // todo could optimize
    }
    
    /* HTTP-Version = "HTTP" "/" 1*DIGIT "." 1*DIGIT */
    static final int[] versions = new int[] {
            72, 84, 84, 80,
            47,
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
            46
    };
    static { Arrays.sort(versions); }
    static boolean isVersion(byte b) {
        return Arrays.binarySearch(versions, b) >= 0;
    }
    
    /* Method = ... | extension-method
       extension-method = token */
    static boolean isMethod(byte b) {
        return isToken(b);
    }

    /* DIGIT = <any US-ASCII digit "0".."9"> */
    static final int[] digits = new int[]{
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57
    };
    static { Arrays.sort(digits); }
    static boolean isDIGIT(byte b) {
        return Arrays.binarySearch(digits, b) >= 0;
    }

    /* Status-Code =
          ...
          | extension-code
       extension-code = 3DIGIT */
    static boolean isStatusCode(byte b) {
        return isDIGIT(b);
    }
    
    /* RFC 2396
    
      alpha    = lowalpha | upalpha
      lowalpha = "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" |
                 "j" | "k" | "l" | "m" | "n" | "o" | "p" | "q" | "r" |
                 "s" | "t" | "u" | "v" | "w" | "x" | "y" | "z"
      upalpha  = "A" | "B" | "C" | "D" | "E" | "F" | "G" | "H" | "I" |
                 "J" | "K" | "L" | "M" | "N" | "O" | "P" | "Q" | "R" |
                 "S" | "T" | "U" | "V" | "W" | "X" | "Y" | "Z"
      digit    = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" |
                 "8" | "9"
      alphanum = alpha | digit
      uric          = reserved | unreserved | escaped
      reserved    = ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+" |
                    "$" | ","
      unreserved  = alphanum | mark
      mark        = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
      escaped     = "%" hex hex
      hex         = digit | "A" | "B" | "C" | "D" | "E" | "F" |
                            "a" | "b" | "c" | "d" | "e" | "f"

      specifically disallowed:
      
         control     = <US-ASCII coded characters 00-1F and 7F hexadecimal>
         space       = <US-ASCII coded character 20 hexadecimal>
         delims      = "<" | ">" | "#" | "%" | <">
         unwise      = "{" | "}" | "|" | "\" | "^" | "[" | "]" | "`"
     */
    static final int[] uric = new int[] {
            59, 47, 63, 58, 64, 38, 61, 43, /* reserved */
            97, 122, /* a -z */
            65, 90, /* A-Z */
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57, /* 0-9 */
            45, 95, 46, 33, 126, 42, 27, 40, 41, /* mark */
            37 /* escaped */
    };
    static { Arrays.sort(uric); }
    static boolean isRequestURI(byte b) {
        return Arrays.binarySearch(uric, b) >= 0;
    }

    /* Reason-Phrase = *<TEXT, excluding CR, LF> */
    static boolean isReasonPhrase(byte b) {
        return b != CR && b != LF && isTEXT(b);
    }
    
    /* field-name = token */
    static boolean isHeaderName(byte b) {
        return isToken(b);
    }
    
    static boolean isHeaderValue(byte b) {
        return isCHAR(b);
    }
}
