package simon.nicolas.comicfetcher.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import simon.nicolas.comicfetcher.ComicConfig;
import simon.nicolas.comicfetcher.activity.ComicDisplayActivity;
import simon.nicolas.comicfetcher.ComicInterface;

/**
 * {@link Button} that can be embedded in any code to display a simple button that takes the user
 * to {@link ComicDisplayActivity} in order to fetch the comic of the day
 */
public class ComicButton extends Button implements View.OnClickListener {
    private Context             mContext;

    public ComicButton(Context context) {
        super(context);
        init(context);
    }

    public ComicButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ComicButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        setClickable(true);
        setOnClickListener(this);
        mContext = ctx;
    }

    @Override
    public void onClick(View v) {
        ComicConfig.getInstance().comicOfTheDayOnly(true);
        Intent intent = new Intent(mContext, ComicDisplayActivity.class);
        mContext.startActivity(intent);
    }
}
