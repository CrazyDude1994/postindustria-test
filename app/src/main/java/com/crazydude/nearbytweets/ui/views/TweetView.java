package com.crazydude.nearbytweets.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazydude.nearbytweets.R;
import com.crazydude.nearbytweets.models.Tweet;
import com.squareup.picasso.Picasso;
import com.wordpress.priyankvex.smarttextview.SmartTextCallback;
import com.wordpress.priyankvex.smarttextview.SmartTextView;

/**
 * Created by Crazy on 02.03.2017.
 */

public class TweetView extends FrameLayout implements ViewModel<Tweet>, SmartTextCallback, View.OnClickListener {

    private SmartTextView mMessageTextView;
    private TextView mUsernameTextView;
    private ImageView mAvatarView;
    private TweetListener mListener;
    private ImageView mIsFavoritedButton;
    private Tweet mData;

    public TweetView(Context context) {
        super(context);
        init();
    }

    public TweetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TweetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TweetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setTweetListener(TweetListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onFavoritedClicked(mData);
        }
    }

    @Override
    public void hashTagClick(String hashTag) {
        if (mListener != null) {
            mListener.onHashtagClicked(hashTag);
        }
    }

    @Override
    public void mentionClick(String mention) {
        if (mListener != null) {
            mListener.onHashtagClicked(mention);
        }
    }

    @Override
    public void emailClick(String email) {

    }

    @Override
    public void phoneNumberClick(String phoneNumber) {

    }

    @Override
    public void webUrlClick(String webUrl) {
        if (mListener != null) {
            mListener.onLinkClicked(webUrl);
        }
    }

    @Override
    public void setData(Tweet data) {
        mData = data;
        try {
            mMessageTextView.setText(data.getMessage());
        } catch (IndexOutOfBoundsException e) {
            Log.d("TweetView", "Failed to set text " + data.getMessage());
        }
        mUsernameTextView.setText(data.getUser().getUsername());
        if (data.isFavorited()) {
            mIsFavoritedButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            mIsFavoritedButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
        Picasso.with(getContext())
                .load(data.getUser().getAvatarURL())
                .into(mAvatarView);
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_tweet, this);

        mMessageTextView = (SmartTextView) view.findViewById(R.id.view_tweet_message);
        mMessageTextView.setDetectMentions(true);
        mMessageTextView.setDetectHashTags(true);
        mMessageTextView.setSmartTextCallback(this);
        mUsernameTextView = (TextView) view.findViewById(R.id.view_tweet_username);
        mAvatarView = (ImageView) view.findViewById(R.id.view_tweet_avatar);
        mIsFavoritedButton = (ImageView) view.findViewById(R.id.view_tweet_favorite_button);
        mIsFavoritedButton.setOnClickListener(this);
    }

    public interface TweetListener {

        void onHashtagClicked(String hashtag);

        void onMentionClicked(String mention);

        void onLinkClicked(String url);

        void onFavoritedClicked(Tweet tweet);
    }
}
