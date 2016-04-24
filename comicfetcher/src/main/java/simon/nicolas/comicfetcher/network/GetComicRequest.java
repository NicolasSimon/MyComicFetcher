package simon.nicolas.comicfetcher.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import simon.nicolas.comicfetcher.ComicConfig;
import simon.nicolas.comicfetcher.exception.ComicException;
import simon.nicolas.comicfetcher.object.Comic;
import simon.nicolas.comicfetcher.parser.ComicParser;

/**
 * AsyncTask used to fetch a comic specified in {@link ComicConfig#shouldFetchComicOfTheDay()}
 * or {@link ComicConfig#getComicNumber()}
 *
 * This class takes care of
 *   - calling the right URL based on the comic to fetch
 *   - parsing the data into a {@link Comic} object
 *   - handle Exceptions and deal accordingly
 *   - return the result back to the caller, with the comic fetched and / or the exception raised {@link GetComicInterface#onResult(Comic, ComicException)}
 *   - notify the caller of the progress with {@link GetComicInterface#onUpdate(int)}
 */
public class GetComicRequest extends AsyncTask<Void, Integer, Comic> {
    private static final String                 COMIC_OF_THE_DAY_URL = "http://xkcd.com/info.0.json";
    private static final String                 COMIC_NUMBER_PREFIX = "http://xkcd.com/";
    private static final String                 COMIC_NUMBER_SUFFIX = "/info.0.json";

    private ComicException                      mException = null;

    private GetComicInterface                   mInterface;
    private NetworkInfo                         mNetworkInfo = null;

    private ComicConfig mConfig;

    public GetComicRequest(Context ctx, GetComicInterface i) {
        mInterface = i;
        ConnectivityManager connectivityManager = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            mNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        mConfig = ComicConfig.getInstance();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if ((mInterface != null) && (values != null) && (values.length > 0)) {
            mInterface.onUpdate(values[0]);
        }
    }

    @Override
    protected Comic doInBackground(Void... params) {
        if ((mNetworkInfo == null) || (!mNetworkInfo.isConnectedOrConnecting())) {
            mException = new ComicException(ComicException.EXCEPTION_NETWORK_NOT_CONNECTED);
            return (null);
        }
        publishProgress(0);
        try {
            String url = COMIC_OF_THE_DAY_URL;
            if ((!mConfig.shouldFetchComicOfTheDay())
                && (mConfig.getComicNumber() >= 0)) {
                url = COMIC_NUMBER_PREFIX + mConfig.getComicNumber() + COMIC_NUMBER_SUFFIX;
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            publishProgress(20);
            Response response;

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                mException = new ComicException(ComicException.EXCEPTION_NETWORK_FAILED);
                e.printStackTrace();
                return (null);
            }
            publishProgress(60);
            String jsonData = response.body().string();
            JSONObject JSONComic = new JSONObject(jsonData);

            publishProgress(80);
            return (ComicParser.parseComic(JSONComic));

        } catch (IOException e) {
            mException = new ComicException(ComicException.EXCEPTION_NETWORK_FAILED);
            e.printStackTrace();
        } catch (JSONException f) {
            mException = new ComicException(ComicException.EXCEPTION_PARSING);
            f.printStackTrace();
        }
        return (null);
    }

    @Override
    protected void onPostExecute(Comic comic) {
        super.onPostExecute(comic);
        if (mInterface != null) {
            mInterface.onResult(comic, mException);
        }
    }
}
