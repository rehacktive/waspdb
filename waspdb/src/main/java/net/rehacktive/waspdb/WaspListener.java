package net.rehacktive.waspdb;

import android.util.Log;

/**
 * Created by stefano on 17/03/2015.
 */
public abstract class WaspListener<T> {

    abstract public void onDone(T ret);

    public void onError(String error) {
        Log.d("WASPDB", "" + error);
    }

}
