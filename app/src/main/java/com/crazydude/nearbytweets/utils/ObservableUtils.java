package com.crazydude.nearbytweets.utils;

import android.support.v7.widget.SearchView;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;

/**
 * Created by Crazy on 03.03.2017.
 */

public class ObservableUtils {

    public static Observable<String> searchViewObservable(final SearchView searchView) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (!e.isDisposed()) {
                            e.onNext(query);
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (!e.isDisposed()) {
                            e.onNext(newText);
                            return true;
                        }
                        return false;
                    }
                });

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        searchView.setOnQueryTextListener(null);
                    }
                });
            }
        });
    }
}
