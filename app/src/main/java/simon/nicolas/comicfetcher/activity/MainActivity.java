package simon.nicolas.comicfetcher.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import simon.nicolas.comicfetcher.ComicConfig;
import simon.nicolas.comicfetcher.ComicInterface;
import simon.nicolas.comicfetcher.exception.ComicException;
import simon.nicolas.comicfetcher.object.Comic;
import simon.nicolas.onfidocomicfetcher.R;

public class MainActivity extends AppCompatActivity implements ComicInterface {
    private TextView                mTvResultOfTheDay;
    private TextView                mTvResultNumber;
    private BroadcastReceiver       mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvResultOfTheDay = (TextView) findViewById(R.id.tvComicOfDayResult);
        mTvResultNumber = (TextView) findViewById(R.id.tvComicNumberResult);

        ComicConfig.getInstance()
                .setInterface(this)
                .useBroadcast(true);

        mResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    ComicException e = (ComicException) intent.getSerializableExtra(ComicConfig.BROADCAST_EXCEPTION);
                    Comic comic = (Comic) intent.getSerializableExtra(ComicConfig.BROADCAST_COMIC);
                    int resultCode = intent.getIntExtra(ComicConfig.BROADCAST_RESULT, ComicDisplayActivity.RESULT_ERROR);

                    String messageToPrint;

                    if (e != null) {
                        messageToPrint = e.getMessage();
                    } else {
                        switch (resultCode) {
                            case ComicDisplayActivity.RESULT_THUMB_UP:
                                messageToPrint = (comic == null) ? "Thumbs up!" : "Thumbs up on comic number " + comic.getNumber();
                                break;
                            case ComicDisplayActivity.RESULT_THUMB_DOWN:
                                messageToPrint = (comic == null) ? "Thumbs down!" : "Thumbs down on comic number " + comic.getNumber();
                                break;
                            case ComicDisplayActivity.RESULT_EXIT:
                                messageToPrint = "Exit with back press";
                                break;
                            case ComicDisplayActivity.RESULT_ERROR:
                                messageToPrint = "An error occured";
                                break;
                            default:
                                messageToPrint = "I dunno!";
                                break;
                        }
                    }
                    setResultString(messageToPrint);
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mResultReceiver, new IntentFilter(ComicConfig.BROADCAST_FILTER));
    }

    private void setResultString(String message) {
        if (ComicConfig.getInstance().shouldFetchComicOfTheDay()) {
            if (mTvResultOfTheDay != null) {
                mTvResultOfTheDay.setText(message);
            }
        } else {
            if (mTvResultNumber != null) {
                mTvResultNumber.setText(message);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mResultReceiver);
    }

    @Override
    public void onResult(int result) {
        String text;
        switch (result) {
            case ComicDisplayActivity.RESULT_THUMB_UP:
                text = "Yay, I love it too !";
                break;
            case ComicDisplayActivity.RESULT_THUMB_DOWN:
                text = "booooh, don't like?";
                break;
            default:
                text = "cancelled";
                break;
        }
        setResultString(text);
    }
}
