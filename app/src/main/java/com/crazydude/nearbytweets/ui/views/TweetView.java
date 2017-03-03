package com.crazydude.nearbytweets.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableTextView;
import com.crazydude.nearbytweets.R;
import com.crazydude.nearbytweets.models.Tweet;
import com.crazydude.nearbytweets.utils.LinkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by Crazy on 02.03.2017.
 */

public class TweetView extends FrameLayout implements ViewModel<Tweet> {

    private LinkableTextView mMessageTextView;
    private TextView mUsernameTextView;
    private ImageView mAvatarView;
    private TweetListener mListener;

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
    public void setData(Tweet data) {
        Link hashtagLink = LinkUtils.getHashtagLink().setClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String text) {
                if (mListener != null) {
                    mListener.onHashtagClicked(text);
                }
            }
        });

        Link mentionLink = LinkUtils.getMentionLink().setClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String text) {
                if (mListener != null) {
                    mListener.onMentionClicked(text);
                }
            }
        });

        mMessageTextView.setText(data.getMessage())
                .addLink(hashtagLink)
                .addLink(mentionLink)
                .build();
        mUsernameTextView.setText(data.getUser().getUsername());
        Picasso.with(getContext())
                .load(data.getUser().getAvatarURL())
                .into(mAvatarView);
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_tweet, this);

        mMessageTextView = (LinkableTextView) view.findViewById(R.id.view_tweet_message);
        mUsernameTextView = (TextView) view.findViewById(R.id.view_tweet_username);
        mAvatarView = (ImageView) view.findViewById(R.id.view_tweet_avatar);
    }

    public interface TweetListener {

        void onHashtagClicked(String hashtag);

        void onMentionClicked(String mention);
    }
}
