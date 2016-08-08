package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

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
@Parcel(analyze = {Tweet.class})
@Table(name = "Tweets")
public class Tweet extends Model {
    @Column(name = "remote_id", unique = true)
    public long remote_id;
    @Column(name = "uid")
    public long uid;   // unique id for the tweet
    @Column(name = "body")
    public String body;
    @Column(name = "created_at")
    public String createdAt;
    private String timeAgo;
    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public User user;

    public Tweet() {
        super();
    }

    public Tweet(String body, User user) {
        super();
        this.body = body;
        this.user = user;
        this.createdAt = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy").toString();
        this.timeAgo = "Just now";
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

    public String getTimeAgo() {
        return timeAgo;
    }

    // Deserialize the JSON
    public static Tweet fromJSON (JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        // Extract the values from the JSON, store them
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.remote_id = tweet.uid;
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.timeAgo = getRelativeTimeAgo(jsonObject.getString("created_at"));
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            // Add media URLs to Media table.
            Media.addAll(tweet.remote_id, jsonObject.getJSONArray("media"));
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
