package simon.nicolas.comicfetcher;

/**
 * Simple interface used to get the result from the comic display
 * Result can be one of the following :
 *   - {@link simon.nicolas.comicfetcher.activity.ComicDisplayActivity#RESULT_ERROR}
 *   - {@link simon.nicolas.comicfetcher.activity.ComicDisplayActivity#RESULT_THUMB_UP}
 *   - {@link simon.nicolas.comicfetcher.activity.ComicDisplayActivity#RESULT_THUMB_DOWN}
 *   - {@link simon.nicolas.comicfetcher.activity.ComicDisplayActivity#RESULT_EXIT}
 */
public interface ComicInterface {
    void onResult(int result);
}
