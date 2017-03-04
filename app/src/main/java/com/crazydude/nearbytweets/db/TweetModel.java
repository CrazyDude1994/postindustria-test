package com.crazydude.nearbytweets.db;

import com.crazydude.nearbytweets.models.Tweet;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Crazy on 02.03.2017.
 */

public class TweetModel extends RealmObject implements Tweet {

    @PrimaryKey
    private long mId;
    private String mMessage;
    private boolean mIsFavorited;
    private UserModel mUser;

    public TweetModel() {
    }

    public TweetModel(Tweet tweet) {
        mId = tweet.getId();
        mMessage = tweet.getMessage();
        mIsFavorited = tweet.isFavorited();
        mUser = new UserModel(tweet.getUser());
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public String getMessage() {
        return mMessage;
    }

    @Override
    public boolean isFavorited() {
        return mIsFavorited;
    }

    @Override
    public UserModel getUser() {
        return mUser;
    }

    public void setUser(UserModel user) {
        mUser = user;
    }

    public void setFavorited(boolean favorited) {
        mIsFavorited = favorited;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setId(long id) {
        mId = id;
    }
}
