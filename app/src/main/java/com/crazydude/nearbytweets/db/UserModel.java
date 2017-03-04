package com.crazydude.nearbytweets.db;

import com.crazydude.nearbytweets.models.User;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Crazy on 02.03.2017.
 */

public class UserModel extends RealmObject implements User {

    @PrimaryKey
    private long mId;
    private String mAvatarUrl;
    private String mUsername;

    public UserModel() {
    }

    public UserModel(User user) {
        mAvatarUrl = user.getAvatarURL();
        mUsername = user.getUsername();
        mId = user.getId();
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public String getAvatarURL() {
        return mAvatarUrl;
    }

    @Override
    public String getUsername() {
        return mUsername;
    }
}
