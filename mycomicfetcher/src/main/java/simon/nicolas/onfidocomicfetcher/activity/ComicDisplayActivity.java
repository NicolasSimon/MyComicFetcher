package simon.nicolas.comicfetcher.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;

import simon.nicolas.onfidocomicfetcher.ComicConfig;
import simon.nicolas.onfidocomicfetcher.ComicInterface;
import simon.nicolas.onfidocomicfetcher.R;
import simon.nicolas.comicfetcher.exception.ComicException;
import simon.nicolas.comicfetcher.network.GetComicInterface;
import simon.nicolas.comicfetcher.network.GetComicRequest;
import simon.nicolas.comicfetcher.object.Comic;
import simon.nicolas.comicfetcher.utils.TextUtils;

public class ComicDisplayActivity extends AppCompatActivity implements GetComicInterface, View.OnClickListener {
    public static final String          TAG_INTERFACE = "Interface";

    public static final int             RESULT_THUMB_UP = 42;
    public static final int             RESULT_THUMB_DOWN = 43;
    public static final int             RESULT_EXIT = 44;

    private ImageView                   mThumbsUp;
    private ImageView                   mThumbsDown;
    private ImageView                   mComicImage;

    private FrameLayout                 mFrameSwitcher;
    private ViewSwitcher                mViewSwitcher;

    private TextView                    mComicTitle;
    private TextView                    mComicDescription;

    private ComicInterface              mInterface;

    private boolean                     mIsFetching = false;
    private boolean                     mIsAnimating = false;

    private ProgressDialog              mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_display);

        if (getIntent() != null) {
            mInterface = (ComicInterface) getIntent().getSerializableExtra(TAG_INTERFACE);
            if (mInterface == null) {
                try {
                    shouldTrow();
                } catch (ComicException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                shouldTrow();
            } catch (ComicException e) {
                e.printStackTrace();
            }
        }

        mThumbsUp = (ImageView) findViewById(R.id.thumbsUp);
        mThumbsDown = (ImageView) findViewById(R.id.thumbsDown);
        mComicImage = (ImageView) findViewById(R.id.comicImage);

        mFrameSwitcher = (FrameLayout) findViewById(R.id.displaySwitcher);
        mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

        mComicTitle = (TextView) findViewById(R.id.comicTitle);
        mComicDescription = (TextView) findViewById(R.id.comicDescription);

        listenToEvents();

        createDialog();

        launchRequest();
    }

    private void shouldTrow() throws ComicException {
        throw (new ComicException(ComicException.EXCEPTION_INTERFACE_NOT_SET));
    }

    private Dialog createDialog() {
        mDialog = new ProgressDialog(this);
        mDialog.setTitle(getString(R.string.fetching_comic_title));
        mDialog.setMessage(getString(R.string.fetching_comic_message));
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setCancelable(false);
        mDialog.setMax(100);
        return (mDialog);
    }

    private void launchRequest() {
        if (mDialog != null) {
            mDialog.show();
        }
        mIsFetching = true;

        new GetComicRequest(this, this).execute();
    }

    private void listenToEvents() {
        if (mThumbsUp != null) {
            mThumbsUp.setOnClickListener(this);
        }
        if (mThumbsDown != null) {
            mThumbsDown.setOnClickListener(this);
        }
        if (mFrameSwitcher != null) {
            mFrameSwitcher.setOnClickListener(this);
        }
        if (mViewSwitcher != null) {
            mViewSwitcher.setOnClickListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mIsFetching) {
            if (mInterface != null) {
                mInterface.onResult(RESULT_EXIT);
            }
            terminate();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.thumbsUp:
                voteUp();
                break;
            case R.id.thumbsDown:
                voteDown();
                break;
            case R.id.displaySwitcher:
            case R.id.viewSwitcher:
                switchView();
                break;
        }
    }

    private void voteUp() {
        if (!mIsFetching) {
            if (mInterface != null) {
                mInterface.onResult(RESULT_THUMB_UP);
            }
            terminate();
        }
    }

    private void voteDown() {
        if (!mIsFetching) {
            if (mInterface != null) {
                mInterface.onResult(RESULT_THUMB_DOWN);
            }
            terminate();
        }
    }

    private void switchView() {
        if (!mIsFetching) {
            manageText();
        }
    }

    private void manageText() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if ((mComicDescription != null) && (mComicDescription.getVisibility() == View.VISIBLE)) {
                hideTextPost21();
            } else {
                revealTextPost21();
            }
        } else {
            manageTextPre21();
        }
    }

    private void manageTextPre21() {
        if (mViewSwitcher != null) {
            if (mViewSwitcher.getDisplayedChild() == 0) {
                mViewSwitcher.showNext();
            } else {
                mViewSwitcher.showPrevious();
            }
        }
    }

    @TargetApi(21)
    private void revealTextPost21() {
        if ((mComicDescription == null) || mIsAnimating) {
            return;
        }
        mIsAnimating = true;
        int cx = mComicDescription.getWidth() / 2;
        int cy = mComicDescription.getHeight();

        float finalRadius = (float) Math.hypot(cx, cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(mComicDescription, cx, cy, 0, finalRadius);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAnimating = false;
            }
        });
        mComicDescription.setVisibility(View.VISIBLE);
        anim.start();
    }

    @TargetApi(21)
    private void hideTextPost21() {
        if ((mComicDescription == null) || mIsAnimating) {
            return;
        }
        mIsAnimating = true;
        int cx = mComicDescription.getWidth() / 2;
        int cy = mComicDescription.getHeight();

        float initialRadius = (float) Math.hypot(cx, cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(mComicDescription, cx, cy, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mComicDescription.setVisibility(View.INVISIBLE);
                mIsAnimating = false;
            }
        });
        anim.start();
    }

    private void terminate() {
        this.finish();
    }

    private void setComicInformation(Comic comic) {
        if (comic != null) {
            //We ensure that the imageView is present, as well as the url in the comic
            if ((mComicImage != null) && !TextUtils.isEmpty(comic.getImageUrl())) {
                Picasso.with(this).load(comic.getImageUrl()).into(mComicImage);
            }
            if (mComicTitle != null) {
                mComicTitle.setText(getMostRelevantTitle(comic));
            }
            if (mComicDescription != null) {
                mComicDescription.setText(comic.getAlt());
            }
        }
    }

    private String getMostRelevantTitle(Comic comic) {
        if (comic == null) {
            return (null);
        }
        return (TextUtils.isEmpty(comic.getTitle()) ? comic.getSafeTitle() : comic.getTitle());
    }

    @Override
    public void onUpdate(int progress) {
        if (mDialog != null) {
            mDialog.setProgress(progress);
        }
    }

    private void setErrorState(ComicException exception) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String message;
        switch (exception.getMessage()) {
            case ComicException.EXCEPTION_INTERFACE_NOT_SET:
                message = getString(R.string.error_message_interface_not_set);
                break;
            case ComicException.EXCEPTION_NETWORK_FAILED:
                message = getString(R.string.error_message_network_failed);
                break;
            case ComicException.EXCEPTION_NETWORK_NOT_CONNECTED:
                message = getString(R.string.error_message_network_not_connected);
                break;
            case ComicException.EXCEPTION_PARSING:
                message = getString(R.string.error_message_parsing);
                break;
            default:
                message = getString(R.string.error_message_else);
                break;
        }

        builder
                .setTitle(R.string.error_title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.button_retry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchRequest();
                    }
                })
                .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .show();
    }

    private void broadcastError(ComicException e) {
        Intent i = new Intent(ComicConfig.BROADCAST_ERROR);
        i.putExtra(ComicConfig.BROADCAST_ERROR_REASON, e);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        onBackPressed();
    }

    @Override
    public void onResult(Comic comic, ComicException exception) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mIsFetching = false;
        if (exception != null){
            if (ComicConfig.getInstance().shouldDisplayErrors()) {
                setErrorState(exception);
            } else {
                broadcastError(exception);
            }
        } else {
            setComicInformation(comic);
        }
    }
}
