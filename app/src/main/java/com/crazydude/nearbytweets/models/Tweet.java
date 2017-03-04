package com.crazydude.nearbytweets.models;

/**
 * Created by Crazy on 02.03.2017.
 */

public interface Tweet {

    long getId();

    String getMessage();

    boolean isFavorited();

    User getUser();
}
