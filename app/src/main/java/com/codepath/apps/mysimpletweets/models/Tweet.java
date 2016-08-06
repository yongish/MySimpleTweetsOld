package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

// Parse the JSON + Store the data, encapsulate state logic or display logic
@Parcel
public class Tweet {
    // list out the attributes
    private String body;
    private long uid;   // unique id for the tweet
    private User user;  // store embedded user object
    private String createdAt;

    public Tweet() {}

    public Tweet(String body, User user) {
        this.body = body;
        this.user = user;
        this.createdAt = "Just now";
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Deserialize the JSON
    public static Tweet fromJSON (JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        // Extract the values from the JSON, store them
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = getRelativeTimeAgo(jsonObject.getString("created_at"));
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Return the tweet object
        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray (JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        // Iterate the json array and create tweets
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        // Return the finished list
        return tweets;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    private static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime() - getTimeDifference();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    // Twitter does not provide timezone. Assume PST based on observation.
    private static long getTimeDifference() {
        TimeZone twitterTimeZone = TimeZone.getTimeZone("America/Los_Angeles");
//        TimeZone twitterTimeZone = TimeZone.getTimeZone("America/Chicago");
        TimeZone systemTimeZone = Calendar.getInstance().getTimeZone();
        return twitterTimeZone.getRawOffset() - systemTimeZone.getRawOffset() + twitterTimeZone.getDSTSavings() - systemTimeZone.getDSTSavings();
    }
}
