package com.crazydude.nearbytweets.db;

import com.crazydude.nearbytweets.models.Tweet;
import com.crazydude.nearbytweets.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Crazy on 02.03.2017.
 */

public class DataMapper {

    public static Tweet toTweet(final TweetModel tweet) {
        return new Tweet() {

            @Override
            public long getId() {
                return tweet.getId();
            }

            @Override
            public String getMessage() {
                return tweet.getMessage();
            }

            @Override
            public boolean isFavorited() {
                return DatabaseManager.isTweetFavorited(tweet.getId());
            }

            @Override
            public User getUser() {
                return toUser(tweet.getUser());
            }
        };
    }

    public static List<Tweet> toTweetList(List<TweetModel> tweetModelList) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (TweetModel tweetModel : tweetModelList) {
            Tweet tweet = toTweet(tweetModel);
            tweets.add(tweet);
        }

        return tweets;
    }

    public static User toUser(final UserModel user) {
        return new User() {
            @Override
            public long getId() {
                return user.getId();
            }

            @Override
            public String getAvatarURL() {
                return user.getAvatarURL();
            }

            @Override
            public String getUsername() {
                return user.getUsername();
            }
        };
    }
}
