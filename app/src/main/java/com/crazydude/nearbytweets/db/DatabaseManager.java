package com.crazydude.nearbytweets.db;

import com.crazydude.nearbytweets.models.Tweet;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Crazy on 02.03.2017.
 */

public class DatabaseManager {


    public static boolean isTweetFavorited(long id) {
        Realm instance = Realm.getDefaultInstance();
        TweetModel tweetModel = instance.where(TweetModel.class)
                .equalTo("mId", id)
                .findFirst();
        boolean isFavorited = tweetModel != null && tweetModel.isFavorited();
        instance.close();

        return isFavorited;
    }

    public static void saveTweet(final Tweet tweet) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(new TweetModel(tweet));
            }
        });
    }

    public static void removeTweet(final long id) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TweetModel model = realm.where(TweetModel.class)
                        .equalTo("mId", id)
                        .findFirst();
                if (model != null) {
                    model.deleteFromRealm();
                }
            }
        });
        realm.close();
    }

    public static List<TweetModel> loadAllFavoritedTweets() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<TweetModel> models = realm.where(TweetModel.class)
                .equalTo("mIsFavorited", true)
                .findAll();
        List<TweetModel> unManaged = realm.copyFromRealm(models);
        realm.close();
        return unManaged;
    }
}
