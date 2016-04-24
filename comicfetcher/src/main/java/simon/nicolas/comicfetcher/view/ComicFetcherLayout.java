package simon.nicolas.comicfetcher.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import simon.nicolas.comicfetcher.ComicConfig;
import simon.nicolas.comicfetcher.R;
import simon.nicolas.comicfetcher.activity.ComicDisplayActivity;

/**
 * {@link LinearLayout} holding a {@link EditText} for the comic number input, and a {@link Button},
 * that can be embedded in any application.
 * Once the button is clicked, it retrieves the number wanted, and launches {@link ComicDisplayActivity}
 * to display the requested comic
 */
public class ComicFetcherLayout extends LinearLayout {
    private Context                 mContext;
    private EditText                mEditText;

    public ComicFetcherLayout(Context context) {
        super(context);
        init(context);
    }

    public ComicFetcherLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(11)
    public ComicFetcherLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        mContext = ctx;

        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);

        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mEditText = new EditText(mContext);
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = .5f;
        mEditText.setLayoutParams(params);

        Button button = new Button(mContext);
        button.setText(mContext.getString(R.string.button_fetch_comic_number));
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        button.setLayoutParams(params);

        this.addView(mEditText);
        this.addView(button);

        button.setClickable(true);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mEditText == null) || (TextUtils.isEmpty(mEditText.getText().toString()))) {
                    Toast.makeText(mContext, mContext.getString(R.string.toast_no_comic_number_set), Toast.LENGTH_SHORT).show();
                } else {
                    launchActivity();
                }

            }
        });
        this.invalidate();
    }

    private void launchActivity() {
        ComicConfig.getInstance().comicNumber(Integer.parseInt(mEditText.getText().toString()));
        Intent intent = new Intent(mContext, ComicDisplayActivity.class);
        mContext.startActivity(intent);
    }
}
