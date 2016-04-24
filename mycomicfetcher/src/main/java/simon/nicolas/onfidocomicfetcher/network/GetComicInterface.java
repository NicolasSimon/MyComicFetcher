package simon.nicolas.comicfetcher.network;

import android.support.annotation.Nullable;

import simon.nicolas.comicfetcher.exception.ComicException;
import simon.nicolas.comicfetcher.object.Comic;

/**
 * Created by Nicolas on 22/04/2016.
 * (c) Touchnote Ltd., 2015
 */
public interface GetComicInterface {
    void onUpdate(int progress);
    void onResult(Comic comic, @Nullable ComicException exception);
}
