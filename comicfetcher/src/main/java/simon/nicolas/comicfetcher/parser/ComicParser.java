package simon.nicolas.comicfetcher.parser;

import org.json.JSONObject;

import simon.nicolas.comicfetcher.object.Comic;

/**
 * Simple {@link JSONObject} to {@link Comic} parser
 */
public class ComicParser {
    private static final String         TAG_MONTH = "month";
    private static final String         TAG_NUM = "num";
    private static final String         TAG_LINK = "link";
    private static final String         TAG_YEAR = "year";
    private static final String         TAG_NEWS = "news";
    private static final String         TAG_SAFE_TITLE = "safe_title";
    private static final String         TAG_TRANSCRIPT = "transcript";
    private static final String         TAG_ALT = "alt";
    private static final String         TAG_IMG = "img";
    private static final String         TAG_TITLE = "title";
    private static final String         TAG_DAY = "day";

    public static Comic parseComic(JSONObject object) {
        if (object == null) {
            return (null);
        }
        Comic comic = new Comic();

        comic.setMonth(object.optString(TAG_MONTH, null));
        comic.setNumber(object.optInt(TAG_NUM, 0));
        comic.setLink(object.optString(TAG_LINK, null));
        comic.setYear(object.optString(TAG_YEAR, null));
        comic.setNews(object.optString(TAG_NEWS, null));
        comic.setSafeTitle(object.optString(TAG_SAFE_TITLE, null));
        comic.setTranscript(object.optString(TAG_TRANSCRIPT, null));
        comic.setAlt(object.optString(TAG_ALT, null));
        comic.setImageUrl(object.optString(TAG_IMG, null));
        comic.setTitle(object.optString(TAG_TITLE, null));
        comic.setDay(object.optString(TAG_DAY, null));

        return (comic);
    }
}
