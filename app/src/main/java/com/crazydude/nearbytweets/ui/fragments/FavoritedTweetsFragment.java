package com.crazydude.nearbytweets.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.crazydude.nearbytweets.db.DataMapper;
import com.crazydude.nearbytweets.db.DatabaseManager;
import com.crazydude.nearbytweets.models.Tweet;

import java.util.List;

/**
 * Created by Crazy on 04.03.2017.
 */

public class FavoritedTweetsFragment extends TweetsListFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disablePagination();
    }

    @Override
    protected void loadData() {
        List<Tweet> tweets = DataMapper.toTweetList(DatabaseManager.loadAllFavoritedTweets());
        mTweetsAdapter.setData(tweets);
    }

    @Override
    protected void loadMore(long maxId) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        disableRefresh();
        loadData();
    }
}
