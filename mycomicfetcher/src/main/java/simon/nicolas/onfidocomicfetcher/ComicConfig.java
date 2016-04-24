package simon.nicolas.onfidocomicfetcher;

/**
 * Created by Nicolas on 23/04/2016.
 * (c) Touchnote Ltd., 2015
 */
public class ComicConfig {
    public static final String              BROADCAST_ERROR = "Error";
    public static final String              BROADCAST_ERROR_REASON = "Reason";

    private static ComicConfig              mInstance;
    private boolean                         mShouldDisplayErrors;
    private boolean                         mShouldFetchComicOfTheDay;
    private int                             mComicNumber = -1;

    public static synchronized ComicConfig getInstance() {
        if (mInstance == null) {
            mInstance = new ComicConfig();
        }
        return (mInstance);
    }

    private ComicConfig() {
        mShouldDisplayErrors = true;
    }

    public ComicConfig displayErrors(boolean displayErrors) {
        mShouldDisplayErrors = displayErrors;
        return (this);
    }

    public ComicConfig comicOfTheDayOnly(boolean only) {
        mShouldFetchComicOfTheDay = only;
        return (this);
    }

    public ComicConfig comicNumber(int number) {
        if (number >= 0) {
            mShouldFetchComicOfTheDay = false;
            mComicNumber = number;
        }
        return (this);
    }

    public boolean shouldDisplayErrors() {
        return mShouldDisplayErrors;
    }

    public boolean shouldFetchComicOfTheDay() {
        return (mShouldFetchComicOfTheDay);
    }

    public int getComicNumber() {
        return (mComicNumber);
    }
}
