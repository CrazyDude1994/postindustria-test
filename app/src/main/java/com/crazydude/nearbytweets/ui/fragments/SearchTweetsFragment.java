package com.crazydude.nearbytweets.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.crazydude.nearbytweets.R;
import com.crazydude.nearbytweets.api.TwitterAPI;
import com.crazydude.nearbytweets.utils.ObservableUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Crazy on 03.03.2017.
 */

public class SearchTweetsFragment extends TweetsListFragment {

    public static final String STARTING_QUERY_ARG = "starting_query";
    private TwitterAPI mTwitterAPI;
    private Disposable mSearchDisposable;
    private String mLastQuery = "";
    private SearchView mSearchView;

    public static SearchTweetsFragment newInstance(String startingQuery) {
        SearchTweetsFragment searchTweetsFragment = new SearchTweetsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(STARTING_QUERY_ARG, startingQuery);
        searchTweetsFragment.setArguments(bundle);

        return searchTweetsFragment;
    }

    @Override
    protected void loadData() {
        resetPagination();
        mTweetsAdapter.clear();
        mTwitterAPI.searchTweets(mLastQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getDefaultObserver());
    }

    @Override
    protected void loadMore(long maxId) {
        mTwitterAPI.searchTweets(mLastQuery, maxId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getDefaultObserver());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSearchDisposable.dispose();
    }

    @Override
    public void onHashtagClicked(String hashtag) {
        mSearchView.setQuery(hashtag, true);
    }

    @Override
    public void onMentionClicked(String mention) {
        mSearchView.setQuery(mention, true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mLastQuery = arguments.getString(STARTING_QUERY_ARG, "");

        setHasOptionsMenu(true);
        mTwitterAPI = TwitterAPI.getInstance();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        initSearchView(searchItem);
    }

    private void initSearchView(final MenuItem searchItem) {
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchDisposable = ObservableUtils.searchViewObservable(mSearchView)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !s.isEmpty();
                    }
                })
                .debounce(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String query) throws Exception {
                        mLastQuery = query;
                        loadData();
                        Log.d("RxJava", "Search: " + query);
                    }
                });
        if (!mLastQuery.isEmpty()) {
            MenuItemCompat.expandActionView(searchItem);
            mSearchView.setQuery(mLastQuery, false);
        }
    }
}
