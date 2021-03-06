package com.crazydude.nearbytweets.api;

import com.crazydude.nearbytweets.db.DatabaseManager;
import com.crazydude.nearbytweets.models.Tweet;
import com.crazydude.nearbytweets.models.User;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;

/**
 * Created by Crazy on 02.03.2017.
 */

public class DataMapper {

    public static Tweet toTweet(final Status status) {
        return new Tweet() {

            @Override
            public long getId() {
                return status.getId();
            }

            @Override
            public String getMessage() {
                return status.getText();
            }

            @Override
            public boolean isFavorited() {
                return DatabaseManager.isTweetFavorited(status.getId());
            }

            @Override
            public User getUser() {
                return toUser(status.getUser());
            }
        };
    }

    public static List<Tweet> toTweetList(List<Status> statusList) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (Status status : statusList) {
            Tweet tweet = toTweet(status);
            tweets.add(tweet);
        }

        return tweets;
    }

    public static User toUser(final twitter4j.User user) {
        return new User() {
            @Override
            public long getId() {
                return user.getId();
            }

            @Override
            public String getAvatarURL() {
                return user.getBiggerProfileImageURL();
            }

            @Override
            public String getUsername() {
                return user.getScreenName();
            }
        };
    }
}
