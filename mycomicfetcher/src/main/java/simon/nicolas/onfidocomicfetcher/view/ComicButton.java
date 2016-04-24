package simon.nicolas.comicfetcher.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import simon.nicolas.onfidocomicfetcher.ComicInterface;
import simon.nicolas.comicfetcher.activity.ComicDisplayActivity;

/**
 * Created by Nicolas on 22/04/2016.
 * (c) Touchnote Ltd., 2015
 */
public class ComicButton extends Button implements View.OnClickListener {
    private ComicInterface          mInterface;

    public ComicButton(Context context) {
        super(context);
    }

    public ComicButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ComicButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setInterface(ComicInterface i) {
        mInterface = i;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), ComicDisplayActivity.class);
        intent.putExtra(ComicDisplayActivity.TAG_INTERFACE, mInterface);
        getContext().startActivity(intent);
    }
}
