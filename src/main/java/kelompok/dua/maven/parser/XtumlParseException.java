package kelompok.dua.maven.parser;

/**
 * Exception yang dilemparkan ketika terjadi error dalam parsing model xTUML
 */
public class XtumlParseException extends Exception {

    public XtumlParseException(String message) {
        super(message);
    }

    public XtumlParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public XtumlParseException(Throwable cause) {
        super(cause);
    }
}