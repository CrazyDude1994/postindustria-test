package com.crazydude.nearbytweets.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.crazydude.nearbytweets.R;
import com.crazydude.nearbytweets.ui.fragments.FavoritedTweetsFragment;
import com.crazydude.nearbytweets.ui.fragments.NearbyTweetsFragment;
import com.crazydude.nearbytweets.ui.fragments.SearchTweetsFragment;
import com.crazydude.nearbytweets.ui.fragments.TweetsListFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TweetsListFragment.TweetActionClickListener {

    private NavigationView mNavigationView;

    @Override
    public void onHashtagClicked(String hashtag) {
        switchToSearchTweetsFragment(hashtag);
    }

    @Override
    public void onMentionClicked(String mention) {
        switchToSearchTweetsFragment(mention);
    }

    @Override
    public void onWebLinkClicked(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        switch (id) {
            case R.id.nav_search:
                switchToSearchTweetsFragment("");
                break;
            case R.id.nav_nearby:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, new NearbyTweetsFragment())
                        .commit();
                break;
            case R.id.nav_favorites:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, new FavoritedTweetsFragment())
                        .commit();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void switchToSearchTweetsFragment(String query) {
        mNavigationView.setCheckedItem(R.id.nav_search);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, SearchTweetsFragment.newInstance(query))
                .commit();
    }

}
