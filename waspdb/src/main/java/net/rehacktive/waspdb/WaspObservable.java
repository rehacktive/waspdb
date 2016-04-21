package net.rehacktive.waspdb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefano on 17/03/2015.
 */
public class WaspObservable {

    List<WaspObserver> observers;

    public void register(WaspObserver observer) {
        if (observers == null)
            observers = new ArrayList<>();

        if (!observers.contains(observer))
            observers.add(observer);
    }

    public void unregister(WaspObserver observer) {
        if (observers == null) return;

        observers.remove(observer);
    }

    public void notifyObservers() {
        try {
            if (observers == null) return;

            for (WaspObserver observer : observers) {
                if (observer != null) observer.onChange();

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
