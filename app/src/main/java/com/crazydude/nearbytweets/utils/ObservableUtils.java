package com.crazydude.nearbytweets.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import com.crazydude.nearbytweets.ui.adapters.TweetsAdapter;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Cancellable;

import static com.crazydude.nearbytweets.utils.Constants.PAGE_SIZE;

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

    public static Observable<Long> recyclerViewObservable(final RecyclerView recyclerView, final LinearLayoutManager linearLayoutManager) {
        return Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(final ObservableEmitter<Long> emitter) throws Exception {
                final RecyclerView.OnScrollListener listener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (!emitter.isDisposed()) {
                            TweetsAdapter adapter = ((TweetsAdapter) recyclerView.getAdapter());
                            if (adapter.getItemCount() > 0) {
                                int position = linearLayoutManager.findLastVisibleItemPosition();
                                int updatePosition = adapter.getItemCount() - 1 - (PAGE_SIZE / 2);
                                if (position >= updatePosition) {
                                    emitter.onNext(adapter.getData(0).getId());
                                }
                            }
                        }
                    }
                };

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        recyclerView.removeOnScrollListener(listener);
                    }
                });
                recyclerView.addOnScrollListener(listener);
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).distinctUntilChanged();
    }
}
