package com.crazydude.nearbytweets.ui.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.crazydude.nearbytweets.api.TwitterAPI;
import com.crazydude.nearbytweets.models.Tweet;
import com.crazydude.nearbytweets.utils.ObservableUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Crazy on 03.03.2017.
 */

public class NearbyTweetsFragment extends TweetsListFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TwitterAPI mTwitterAPI;
    private Disposable mPaginationDisposable;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        mTweetsAdapter.clear();
        initPagination();
    }

    @Override
    public void onHashtagClicked(String hashtag) {
        super.onHashtagClicked(hashtag);
    }

    @Override
    public void onMentionClicked(String mention) {
        super.onMentionClicked(mention);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPagination();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mTwitterAPI.searchTweets(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 10, 0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<Tweet>>() {
                        @Override
                        public void accept(List<Tweet> tweets) throws Exception {
                            mTweetsAdapter.setData(tweets);
                        }
                    });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTwitterAPI = TwitterAPI.getInstance();
        initGoogleApi();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPaginationDisposable.dispose();
    }

    private void initPagination() {
        mPaginationDisposable = ObservableUtils.recyclerViewObservable(mRecyclerView, mLinearLayoutManager)
                .flatMap(new Function<Long, ObservableSource<List<Tweet>>>() {
                    @Override
                    public ObservableSource<List<Tweet>> apply(Long maxId) throws Exception {
                        return mTwitterAPI.searchTweets(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 10).subscribeOn(Schedulers.io());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Tweet>>() {
                    @Override
                    public void accept(List<Tweet> tweets) throws Exception {
                        mTweetsAdapter.addData(tweets);
                    }
                });
    }

    private void initGoogleApi() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }
}
