package com.crazydude.nearbytweets.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazydude.nearbytweets.R;
import com.crazydude.nearbytweets.ui.adapters.TweetsAdapter;
import com.crazydude.nearbytweets.ui.views.TweetView;

/**
 * Created by Crazy on 02.03.2017.
 */

public abstract class TweetsListFragment extends Fragment implements TweetView.TweetListener, SwipeRefreshLayout.OnRefreshListener {

    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLinearLayoutManager;
    protected TweetsAdapter mTweetsAdapter;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    abstract public void onRefresh();

    @Override
    public void onHashtagClicked(String hashtag) {
        //Empty implementation
    }

    @Override
    public void onMentionClicked(String mention) {
        //Empty implementation
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tweets_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.tweets_list_fragment_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.tweets_list_fragment_swipe_refresher);
        initViews();
    }

    private void initViews() {
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mTweetsAdapter = new TweetsAdapter(this);
        mRecyclerView.setAdapter(mTweetsAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }
}
