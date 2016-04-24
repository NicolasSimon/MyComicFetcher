package simon.nicolas.comicfetcher.network;

import android.support.annotation.Nullable;

import simon.nicolas.comicfetcher.exception.ComicException;
import simon.nicolas.comicfetcher.object.Comic;

/**
 * Interface used to pass result of the {@link GetComicRequest} back to the caller
 */
public interface GetComicInterface {
    void onUpdate(int progress);
    void onResult(Comic comic, @Nullable ComicException exception);
}
