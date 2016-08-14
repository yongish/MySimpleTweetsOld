package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MentionsTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    private long lowestUid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lowestUid = Long.MAX_VALUE;
        client = TwitterApplication.getRestClient();    // singleton client
        populateTimeline();
    }

    // Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from the json
    public void populateTimeline() {
        client.getMentionsTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                // JSON HERE
                // DESERIALIZE JSON
                // CREATE MODELS AND ADD THEM TO THE ADAPTER
                // LOAD THE MODEL DATA INTO LISTVIEW
                //aTweets.addAll(Tweet.fromJSONArray(json));

//                aTweets.clear();
                ArrayList<Tweet> newTweets = Tweet.fromJSONArray(json);
//                lowestUid = getLowestUid(newTweets);
//                tweets.addAll(newTweets);
                addAll(newTweets);
//                aTweets.notifyDataSetChanged(); // TODO: Find out how many tweets are fetched. Avoid using notifyDataSetChanged().
                setSwipeContainerRefreshingFalse();

//                // Add to database.
//                for (Tweet tweet: newTweets) {
//                    long userRemoteId = tweet.user.remote_id;
//                    User existingUser = new Select().from(User.class).where("remote_id = ?", userRemoteId).executeSingle();
//                    if (existingUser == null)
//                        tweet.user.save();
//
//                    long tweetRemoteId = tweet.remote_id;
//                    Tweet existingTweet = new Select().from(Tweet.class).where("remote_id = ?", tweetRemoteId).executeSingle();
//                    if (existingTweet == null) {
//                        if (existingUser != null) {
//                            tweet.user = existingUser;
//                        }
//                        tweet.save();
//                    }
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String errString = "";
                if (errorResponse != null) errString = errorResponse.toString();
            }
        });
    }
}
