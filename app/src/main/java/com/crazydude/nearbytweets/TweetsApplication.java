package com.crazydude.nearbytweets;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Crazy on 04.03.2017.
 */

public class TweetsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }
}
