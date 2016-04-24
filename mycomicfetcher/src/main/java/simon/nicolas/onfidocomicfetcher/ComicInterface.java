package simon.nicolas.onfidocomicfetcher;

import java.io.Serializable;

/**
 * Created by Nicolas on 22/04/2016.
 * (c) Touchnote Ltd., 2015
 */
public interface ComicInterface extends Serializable {
    void onResult(int result);
}
