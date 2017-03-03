package com.crazydude.nearbytweets.db;

import com.crazydude.nearbytweets.models.Tweet;
import com.crazydude.nearbytweets.models.User;

/**
 * Created by Crazy on 02.03.2017.
 */

public class TweetModel implements Tweet {

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public User getUser() {
        return null;
    }
}
