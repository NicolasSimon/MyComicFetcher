package simon.nicolas.comicfetcher.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;

import simon.nicolas.comicfetcher.ComicConfig;
import simon.nicolas.comicfetcher.ComicInterface;
import simon.nicolas.comicfetcher.R;
import simon.nicolas.comicfetcher.exception.ComicException;
import simon.nicolas.comicfetcher.network.GetComicInterface;
import simon.nicolas.comicfetcher.network.GetComicRequest;
import simon.nicolas.comicfetcher.object.Comic;

/**
 * This activity holds everything needed to :
 *   - launch a request for a certain comic (defined in the ComicConfig)
 *   - display the results (comic fetched, error state, buttons)
 *   - offers the users the possibility to :
 *       - tap the image to reveal the short description, back and forth
 *       - tap thumbs up to vote and return to the previous screen
 *       - tap thumbs down to vote and return to the previous screen
 *       - simply press back and return to the previous screen
 *
 *  This activity also holds 2 different types of forwarding the error / result to the caller :
 *    - by the {@link ComicInterface#onResult(int)}
 *    - by {@link ComicDisplayActivity#broadcastResult(int)}
 *
 *    Note that the type of return is defined by the user using the {@link ComicConfig#shouldBroadcast()}
 */
public class ComicDisplayActivity extends AppCompatActivity implements GetComicInterface {
    /**
     * Used in {@link ComicInterface#onResult(int)} when an error occurred
     */
    public static final int             RESULT_ERROR = -1;
    /**
     * Used in {@link ComicInterface#onResult(int)} when the user voted up
     */
    public static final int             RESULT_THUMB_UP = 42;
    /**
     * Used in {@link ComicInterface#onResult(int)} when the user voted down
     */
    public static final int             RESULT_THUMB_DOWN = 43;
    /**
     * Used in {@link ComicInterface#onResult(int)} when the user pressed back
     */
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

    private ComicException              mLastComicException;
    private Comic                       mLastComic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_display);

        /**
         * We launch a check to make sure that the {@link ComicInterface} is set up
         * in case the user is willing to use it
         */
        mInterface = ComicConfig.getInstance().getInterface();
        if ((mInterface == null) && (!ComicConfig.getInstance().shouldBroadcast())) {
            try {
                shouldTrow();
            } catch (ComicException e) {
                e.printStackTrace();
                finish();
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

    /**
     * Simple helper to throw a {@link ComicException#EXCEPTION_INTERFACE_NOT_SET}
     * @throws ComicException the exception to be thrown
     */
    private void shouldTrow() throws ComicException {
        throw (new ComicException(ComicException.EXCEPTION_INTERFACE_NOT_SET));
    }

    /**
     * Simple helper to create the Progress dialog to be displayed while fetching the comic requested
     */
    private void createDialog() {
        mDialog = new ProgressDialog(this);
        mDialog.setTitle(getString(R.string.fetching_comic_title));
        mDialog.setMessage(getString(R.string.fetching_comic_message));
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setCancelable(false);
        mDialog.setMax(100);
    }

    /**
     * Show the dialog, then request the comic
     */
    private void launchRequest() {
        if (mDialog != null) {
            mDialog.show();
        }
        mIsFetching = true;

        mLastComic = null;
        mLastComicException = null;
        new GetComicRequest(this, this).execute();
    }

    /**
     * Set up listeners on views
     */
    private void listenToEvents() {
        if (mThumbsUp != null) {
            mThumbsUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voteUp();
                }
            });
        }
        if (mThumbsDown != null) {
            mThumbsDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voteDown();
                }
            });
        }
        if (mFrameSwitcher != null) {
            mFrameSwitcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchView();
                }
            });
        }
        if (mViewSwitcher != null) {
            mViewSwitcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchView();
                }
            });
        }
    }

    /**
     * Override the default {@link AppCompatActivity#onBackPressed()}
     * in order to set up our own behavior
     */
    @Override
    public void onBackPressed() {
        if (!mIsFetching) {
            if (ComicConfig.getInstance().shouldBroadcast()) {
                broadcastResult(RESULT_EXIT);
            } else {
                if (mInterface != null) {
                    mInterface.onResult(RESULT_EXIT);
                }
                terminate();
            }
        }
    }

    /**
     * Called upon thumbs up pressed.
     * Makes sure to transmit the result as per {@link ComicConfig} before terminating
     */
    private void voteUp() {
        if (!mIsFetching) {
            if (ComicConfig.getInstance().shouldBroadcast()) {
                broadcastResult(RESULT_THUMB_UP);
            } else {
                if (mInterface != null) {
                    mInterface.onResult(RESULT_THUMB_UP);
                }
                terminate();
            }
        }
    }

    /**
     * Called upon thumbs down pressed.
     * Makes sure to transmit the result as per {@link ComicConfig} before terminating
     */
    private void voteDown() {
        if (!mIsFetching) {
            if (ComicConfig.getInstance().shouldBroadcast()) {
                broadcastResult(RESULT_THUMB_DOWN);
            } else {
                if (mInterface != null) {
                    mInterface.onResult(RESULT_THUMB_DOWN);
                }
                terminate();
            }
        }
    }

    /**
     * Called upon main area pressed
     * Calls the right behavior depending on the state of the view (showing image or text)
     */
    private void switchView() {
        if (!mIsFetching) {
            manageText();
        }
    }

    /**
     * Calls either a reveal animation or switches pages in the viewSwitcher, depending on
     * the current {@link android.os.Build.VERSION#SDK_INT} of the user
     */
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

    /**
     * Simple viewSwitcher between text and image
     */
    private void manageTextPre21() {
        if (mViewSwitcher != null) {
            if (mViewSwitcher.getDisplayedChild() == 0) {
                mViewSwitcher.showNext();
            } else {
                mViewSwitcher.showPrevious();
            }
        }
    }

    /**
     * For {@link android.os.Build.VERSION_CODES#LOLLIPOP} and above
     * Starts a reveal animation to show the text
     */
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

    /**
     * For {@link android.os.Build.VERSION_CODES#LOLLIPOP} and above
     * Starts a reveal animation to hide the text
     */
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

    /**
     * Called after the results have been transmitted, to finish this activity
     */
    private void terminate() {
        this.finish();
    }

    /**
     * Set the comic just fetched and displays it on the view
     * Loads the {@link Comic#getImageUrl()} inside the {@link ComicDisplayActivity#mComicImage}
     * Loads the title inside the {@link ComicDisplayActivity#mComicTitle}
     * Loads the {@link Comic#getAlt()} inside the {@link ComicDisplayActivity#mComicDescription}
     * @param comic the comic just fetched
     */
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

    /**
     * Helper to decide which better title to display
     * @param comic the comic to get the title from
     * @return {@link Comic#getTitle()} if not null, {@link Comic#getSafeTitle()} otherwise
     */
    private String getMostRelevantTitle(Comic comic) {
        if (comic == null) {
            return (null);
        }
        return (TextUtils.isEmpty(comic.getTitle()) ? comic.getSafeTitle() : comic.getTitle());
    }

    /**
     * Called from the {@link GetComicRequest#onProgressUpdate(Integer...)}
     * @param progress the current progress to display to the user
     */
    @Override
    public void onUpdate(int progress) {
        if (mDialog != null) {
            mDialog.setProgress(progress);
        }
    }

    /**
     * Used to display an {@link AlertDialog} on the screen in case of an error
     * To allow the user to :
     *   - cancel and go back to the previous screen
     *   - retry and relaunch the request
     * @param exception the exception to be displayed
     */
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

    /**
     * Broadcast the result to whoever is listening
     * Adds to the broadcast
     *   - the exception that occurs, or null otherwise
     *   - the comic that was fetched, or null otherwise
     * @param result the result to be sent
     */
    private void broadcastResult(int result) {
        Intent i = new Intent(ComicConfig.BROADCAST_FILTER);
        i.putExtra(ComicConfig.BROADCAST_EXCEPTION, mLastComicException);
        i.putExtra(ComicConfig.BROADCAST_COMIC, mLastComic);
        i.putExtra(ComicConfig.BROADCAST_RESULT, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        terminate();
    }

    /**
     * Called by {@link GetComicRequest#onPostExecute(Comic)}
     * @param comic : the comic that was fetched, or null
     * @param exception : the exception that was raised during the fetching, or null
     */
    @Override
    public void onResult(Comic comic, ComicException exception) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mIsFetching = false;

        mLastComic = comic;
        mLastComicException = exception;

        if (exception != null) {
            if (ComicConfig.getInstance().shouldBroadcast()) {
                broadcastResult(RESULT_ERROR);
            } else {
                setErrorState(exception);
            }
        } else {
            setComicInformation(comic);
        }
    }
}
