package com.crazydude.nearbytweets.ui.adapters;

import android.view.ViewGroup;

import com.crazydude.nearbytweets.models.Tweet;
import com.crazydude.nearbytweets.ui.views.TweetView;

/**
 * Created by Crazy on 03.03.2017.
 */

public class TweetsAdapter extends BaseAdapter<Tweet, TweetView, BaseViewHolder<TweetView>> {

    private TweetView.TweetListener mTweetListener;

    public TweetsAdapter(TweetView.TweetListener tweetListener) {
        mTweetListener = tweetListener;
    }

    @Override
    public BaseViewHolder<TweetView> onCreateViewHolder(ViewGroup parent, int viewType) {
        TweetView tweetView = new TweetView(parent.getContext());
        tweetView.setTweetListener(mTweetListener);
        return new BaseViewHolder<>(tweetView);
    }
}
