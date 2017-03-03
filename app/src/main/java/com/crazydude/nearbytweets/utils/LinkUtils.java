package com.crazydude.nearbytweets.utils;

import android.graphics.Color;

import com.apradanas.simplelinkabletext.Link;

import java.util.regex.Pattern;

/**
 * Created by Crazy on 03.03.2017.
 */

public class LinkUtils {

    public static Link getHashtagLink() {
        return new Link(Pattern.compile("(#\\w+)"))
                .setUnderlined(true)
                .setTextStyle(Link.TextStyle.ITALIC);
    }

    public static Link getMentionLink() {
        return new Link(Pattern.compile("(@\\w+)"))
                .setUnderlined(false)
                .setTextColor(Color.parseColor("#D00000"))
                .setTextStyle(Link.TextStyle.BOLD);
    }
}
