package simon.nicolas.comicfetcher;

/**
 * Configuration on how to use the {@link simon.nicolas.comicfetcher.activity.ComicDisplayActivity}
 * as well as {@link simon.nicolas.comicfetcher.network.GetComicRequest}
 */
public class ComicConfig {
    /**
     * In case of a broadcast use instead of the {@link ComicInterface},
     * this is the filter to use to subscribe to events
     */
    public static final String              BROADCAST_FILTER = "Filter";
    /**
     * In case of a broadcast use instead of the {@link ComicInterface},
     * this is the extra to get the exception raised
     */
    public static final String              BROADCAST_EXCEPTION = "Exception";
    /**
     * In case of a broadcast use instead of the {@link ComicInterface},
     * this is the extra to get the comic fetched
     */
    public static final String              BROADCAST_COMIC = "Comic";
    /**
     * In case of a broadcast use instead of the {@link ComicInterface},
     * this is the extra to get the result, which can be :
     *   - {@link simon.nicolas.comicfetcher.activity.ComicDisplayActivity#RESULT_ERROR}
     *   - {@link simon.nicolas.comicfetcher.activity.ComicDisplayActivity#RESULT_THUMB_UP}
     *   - {@link simon.nicolas.comicfetcher.activity.ComicDisplayActivity#RESULT_THUMB_DOWN}
     *   - {@link simon.nicolas.comicfetcher.activity.ComicDisplayActivity#RESULT_EXIT}
     */
    public static final String              BROADCAST_RESULT = "Result";

    private static ComicConfig              mInstance;
    private boolean                         mUseBroadcastInstead;
    private boolean                         mShouldFetchComicOfTheDay;
    private int                             mComicNumber = -1;

    private ComicInterface                  mInterface;

    public static synchronized ComicConfig getInstance() {
        if (mInstance == null) {
            mInstance = new ComicConfig();
        }
        return (mInstance);
    }

    private ComicConfig() {
        mUseBroadcastInstead = false;
    }

    /**
     * Configuration using the broadcast to receive results instead of the {@link ComicInterface}
     * @param useBroadcast true to use broadcast, false otherwise
     * @return this {@link ComicConfig}
     */
    public ComicConfig useBroadcast(boolean useBroadcast) {
        mUseBroadcastInstead = useBroadcast;
        return (this);
    }

    /**
     * Used to set if user wants to fetch the comic of the day only or another comic
     * @param only true to fetch the comic of the day, false otherwise
     * @return this {@link ComicConfig}
     */
    public ComicConfig comicOfTheDayOnly(boolean only) {
        mShouldFetchComicOfTheDay = only;
        return (this);
    }

    /**
     * Used to set the requested comic number
     * @param number the number of the comic. if <0, nothing is changed
     * @return this {@link ComicConfig}
     */
    public ComicConfig comicNumber(int number) {
        if (number >= 0) {
            mShouldFetchComicOfTheDay = false;
            mComicNumber = number;
        }
        return (this);
    }

    /**
     * Used to set the interface to get the results
     * @param i the {@link ComicInterface} to be called to pass the results
     * @return this {@link ComicConfig}
     */
    public ComicConfig setInterface(ComicInterface i) {
        mInterface = i;
        return (mInstance);
    }

    public boolean shouldBroadcast() {
        return mUseBroadcastInstead;
    }

    public boolean shouldFetchComicOfTheDay() {
        return (mShouldFetchComicOfTheDay);
    }

    public int getComicNumber() {
        return (mComicNumber);
    }

    public ComicInterface getInterface() {
        return (mInterface);
    }
}
