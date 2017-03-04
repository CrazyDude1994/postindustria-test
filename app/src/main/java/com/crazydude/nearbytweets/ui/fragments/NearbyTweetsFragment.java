package com.crazydude.nearbytweets.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.crazydude.nearbytweets.api.TwitterAPI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Crazy on 03.03.2017.
 */

public class NearbyTweetsFragment extends TweetsListFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_PERMISSIONS_CODE = 0;
    private TwitterAPI mTwitterAPI;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void loadData() {
        mTweetsAdapter.clear();
        mTwitterAPI.searchTweets(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 10)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getDefaultObserver());
    }

    @Override
    protected void loadMore(long maxId) {
        mTwitterAPI.searchTweets(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 10, maxId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getDefaultObserver());
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, REQUEST_PERMISSIONS_CODE);
        } else {
            loadPosition();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadPosition();
        }
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

    private void loadPosition() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            loadData();
        }
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
