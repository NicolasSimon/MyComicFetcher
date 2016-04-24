package simon.nicolas.comicfetcher.exception;

/**
 * Created by Nicolas on 22/04/2016.
 * (c) Touchnote Ltd., 2015
 */
public class ComicException extends Exception {
    public static final String              EXCEPTION_UNDEFINED = "An error occured";
    public static final String              EXCEPTION_INTERFACE_NOT_SET = "You must call ComicButton#setInterface(ComicInterface)";
    public static final String              EXCEPTION_NETWORK_FAILED = "There has been an error contacting the remote server. Please try again later.";
    public static final String              EXCEPTION_NETWORK_NOT_CONNECTED = "You don't seem to be connected to Internet. Please connect to a simon.nicolas.comicfetcher.network and try again later.";
    public static final String              EXCEPTION_PARSING = "There has been an error while parsing the data from the remote server.";

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
