package com.crazydude.nearbytweets.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazydude.nearbytweets.R;
import com.crazydude.nearbytweets.db.DatabaseManager;
import com.crazydude.nearbytweets.db.TweetModel;
import com.crazydude.nearbytweets.models.Tweet;
import com.crazydude.nearbytweets.ui.adapters.TweetsAdapter;
import com.crazydude.nearbytweets.ui.views.TweetView;
import com.crazydude.nearbytweets.utils.Constants;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Crazy on 02.03.2017.
 */

public abstract class TweetsListFragment extends Fragment implements TweetView.TweetListener, SwipeRefreshLayout.OnRefreshListener {

    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLinearLayoutManager;
    protected TweetsAdapter mTweetsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mPaginationEnabled = true;
    private boolean mIsLoading = false;
    private Disposable mDisposable;

    protected abstract void loadData();

    protected abstract void loadMore(long maxId);

    @Override
    public void onRefresh() {
        loadData();
        resetPagination();
    }

    @Override
    public void onHashtagClicked(String hashtag) {
        //Empty implementation
    }

    @Override
    public void onMentionClicked(String mention) {
        //Empty implementation
    }

    @Override
    public void onLinkClicked(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void onFavoritedClicked(Tweet tweet) {
        TweetModel tweetModel = new TweetModel(tweet);
        if (!tweet.isFavorited()) {
            tweetModel.setFavorited(true);
            DatabaseManager.saveTweet(tweetModel);
        } else {
            tweetModel.setFavorited(false);
            DatabaseManager.removeTweet(tweet.getId());
        }
        mTweetsAdapter.update(tweetModel);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    protected void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    protected void allowNextPage() {
        mIsLoading = false;
    }

    protected void disableRefresh() {
        mSwipeRefreshLayout.setEnabled(false);
    }

    protected void disablePagination() {
        mPaginationEnabled = false;
    }

    protected void resetPagination() {
        mPaginationEnabled = true;
        mIsLoading = false;
    }

    protected Observer<List<Tweet>> getDefaultObserver() {
        return new Observer<List<Tweet>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(List<Tweet> tweets) {
                mTweetsAdapter.addData(tweets);
                Log.d("RxJava", "loadData completed with " + tweets.size());

                if (tweets.size() < Constants.PAGE_SIZE) {
                    disablePagination();
                    Log.d("RxJava", "Load data disable pagination");
                } else {
                    allowNextPage();
                    Log.d("RxJava", "Load data enable pagination");
                }
            }

            @Override
            public void onError(Throwable e) {
                setRefreshing(false);
                Log.d("RxJava", "Load data error " + e.toString());
            }

            @Override
            public void onComplete() {
                setRefreshing(false);
                Log.d("RxJava", "Load data on completed");
            }
        };
    }

    private void initViews() {
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mTweetsAdapter = new TweetsAdapter(this);
        mRecyclerView.setAdapter(mTweetsAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mPaginationEnabled || mIsLoading) {
                    return;
                }

                TweetsAdapter adapter = ((TweetsAdapter) recyclerView.getAdapter());
                if (adapter.getItemCount() > 0) {
                    int position = mLinearLayoutManager.findLastVisibleItemPosition();
                    int updatePosition = adapter.getItemCount() - 1 - (Constants.PAGE_SIZE / 2);
                    if (position >= updatePosition) {
                        loadMore(adapter.getData(adapter.getItemCount() - 1).getId() - 1);
                        mIsLoading = true;
                    }
                }
            }
        });
    }
}
