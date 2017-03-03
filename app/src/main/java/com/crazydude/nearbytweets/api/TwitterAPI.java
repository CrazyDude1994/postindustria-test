package com.crazydude.nearbytweets.api;

import com.crazydude.nearbytweets.models.Tweet;
import com.crazydude.nearbytweets.utils.Constants;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Crazy on 02.03.2017.
 */

public class TwitterAPI {

    private static final TwitterAPI INSTANCE = new TwitterAPI();
    private Twitter mTwitter;
    private OAuth2Token mOAuth2Token;

    private TwitterAPI() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder
                .setApplicationOnlyAuthEnabled(true)
                .setOAuthConsumerKey(Constants.CONSUMER_KEY)
                .setOAuthConsumerSecret(Constants.CONSUMER_SECRET);
        TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
        mTwitter = twitterFactory.getInstance();
    }

    public static TwitterAPI getInstance() {
        return INSTANCE;
    }

    public Observable<List<Tweet>> searchTweets(String request) {
        return generateTweetSearch(new Query(request));
    }

    public Observable<List<Tweet>> searchTweets(String lastQuery, Long maxId) {
        return generateTweetSearch(new Query(lastQuery).maxId(maxId));
    }

    public Observable<List<Tweet>> searchTweets(double latitude, double longitude, double radius) {
        return generateTweetSearch(new Query().geoCode(new GeoLocation(latitude, longitude), radius, "km"));
    }

    public Observable<List<Tweet>> searchTweets(double latitude, double longitude, double radius, Integer maxId) {
        return generateTweetSearch(new Query().geoCode(new GeoLocation(latitude, longitude), radius, "km").maxId(maxId));
    }

    private Observable<List<Tweet>> generateTweetSearch(final Query query) {
        return Observable.fromCallable(new Callable<QueryResult>() {
            @Override
            public QueryResult call() throws Exception {
                try {
                    getBearerTokenIfNeeded();
                    return mTwitter.search(query.count(Constants.PAGE_SIZE));
                } catch (TwitterException e) {
                    throw Exceptions.propagate(e);
                }
            }
        }).map(new Function<QueryResult, List<Tweet>>() {
            @Override
            public List<Tweet> apply(QueryResult queryResult) throws Exception {
                return DataMapper.toTweetList(queryResult.getTweets());
            }
        });
    }

    private void getBearerTokenIfNeeded() throws TwitterException {
        if (mOAuth2Token == null) {
            mOAuth2Token = mTwitter.getOAuth2Token();
        }
    }
}
