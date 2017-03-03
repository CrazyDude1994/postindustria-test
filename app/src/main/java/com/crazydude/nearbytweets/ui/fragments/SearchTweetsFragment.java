package com.crazydude.nearbytweets.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crazydude.nearbytweets.R;
import com.crazydude.nearbytweets.api.TwitterAPI;
import com.crazydude.nearbytweets.models.Tweet;
import com.crazydude.nearbytweets.utils.ObservableUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.crazydude.nearbytweets.utils.Constants.PAGE_SIZE;

/**
 * Created by Crazy on 03.03.2017.
 */

public class SearchTweetsFragment extends TweetsListFragment {

    private TwitterAPI mTwitterAPI;
    private Disposable mSearchDisposable;
    private String mLastQuery = "";
    private Disposable mPaginationDisposable;
    private SearchView mSearchView;

    @Override
    public void onRefresh() {
        mTweetsAdapter.clear();
        mSearchView.setQuery(mLastQuery, true);
    }

    @Override
    public void onHashtagClicked(String hashtag) {
        super.onHashtagClicked(hashtag);
        mSearchView.setQuery(hashtag, true);
    }

    @Override
    public void onMentionClicked(String mention) {
        super.onMentionClicked(mention);
        mSearchView.setQuery(mention, true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPaginationObservable();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mTwitterAPI = TwitterAPI.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSearchDisposable.dispose();
        mPaginationDisposable.dispose();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        initSearchView(searchView);
    }

    private void initPaginationObservable() {
        mPaginationDisposable = ObservableUtils.recyclerViewObservable(mRecyclerView, mLinearLayoutManager)
                .flatMap(new Function<Long, ObservableSource<List<Tweet>>>() {
                    @Override
                    public ObservableSource<List<Tweet>> apply(Long maxId) throws Exception {
                        return mTwitterAPI.searchTweets(mLastQuery, maxId).subscribeOn(Schedulers.io());
                    }
                })
                .takeUntil(new Predicate<List<Tweet>>() {
                    @Override
                    public boolean test(List<Tweet> tweets) throws Exception {
                        return tweets.size() < PAGE_SIZE;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Tweet>>() {
                    @Override
                    public void accept(List<Tweet> tweets) throws Exception {
                        mTweetsAdapter.addData(tweets);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void initSearchView(SearchView searchView) {
        mSearchView = searchView;
        mSearchDisposable = ObservableUtils.searchViewObservable(searchView)
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mSwipeRefreshLayout.setEnabled(s.isEmpty());
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !s.isEmpty();
                    }
                })
                .debounce(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<List<Tweet>>>() {
                    @Override
                    public ObservableSource<List<Tweet>> apply(String query) throws Exception {
                        mLastQuery = query;
                        return mTwitterAPI.searchTweets(query).subscribeOn(Schedulers.io());
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getContext(), R.string.error_while_loading, Toast.LENGTH_SHORT).show();
                    }
                })
                .retry()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Tweet>>() {
                    @Override
                    public void accept(List<Tweet> tweets) throws Exception {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mTweetsAdapter.setData(tweets);
                        mRecyclerView.scrollToPosition(0);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }
}
