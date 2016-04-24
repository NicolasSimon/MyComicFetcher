package simon.nicolas.comicfetcher.exception;

import simon.nicolas.comicfetcher.ComicConfig;

/**
 * Simple Helper to have our own {@link Exception}
 * To provide the user or the developer more information about what happened
 */
public class ComicException extends Exception {
    /**
     * In case of an error we know nothing about
     */
    public static final String              EXCEPTION_UNDEFINED = "undefined";
    /**
     * When the developer didn't provide an {@link simon.nicolas.comicfetcher.ComicInterface}
     * but the flag {@link ComicConfig#shouldBroadcast()} is set to false
     */
    public static final String              EXCEPTION_INTERFACE_NOT_SET = "interface";
    /**
     * When there was an issue on the network
     */
    public static final String              EXCEPTION_NETWORK_FAILED = "network failed";
    /**
     * If the user is not connected
     */
    public static final String              EXCEPTION_NETWORK_NOT_CONNECTED = "No connection";
    /**
     * When an error occured during parsing of the fetched data
     */
    public static final String              EXCEPTION_PARSING = "Parsing";

    public ComicException(String name) {
        super(name);
    }

    public ComicException(Throwable throwable) {
        super(throwable);
    }

    public ComicException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ComicException() {
        super();
    }
}
