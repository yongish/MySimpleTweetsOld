package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    private long lowestUid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lowestUid = Long.MAX_VALUE;
        client = TwitterApplication.getRestClient();    // singleton client

/*        Configuration.Builder config = new Configuration.Builder(getActivity());
        config.addModelClasses(Tweet.class, User.class, Media.class);
        ActiveAndroid.initialize(getActivity());

        // Query ActiveAndroid for list of data and load result back to adapter user addAll
        List<Tweet> queryResults = new Select().from(Tweet.class).orderBy("created_at DESC").limit(100).execute();
        addAll(queryResults);*/

        populateTimeline();
    }

    // Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from the json
    public void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
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

                // Add to database.
                for (Tweet tweet: newTweets) {
                    long userRemoteId = tweet.user.remote_id;
                    User existingUser = new Select().from(User.class).where("remote_id = ?", userRemoteId).executeSingle();
                    if (existingUser == null)
                        tweet.user.save();

                    long tweetRemoteId = tweet.remote_id;
                    Tweet existingTweet = new Select().from(Tweet.class).where("remote_id = ?", tweetRemoteId).executeSingle();
                    if (existingTweet == null) {
                        if (existingUser != null) {
                            tweet.user = existingUser;
                        }
                        tweet.save();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String errString = "";
                if (errorResponse != null) errString = errorResponse.toString();
            }
        }, lowestUid);
    }


    public void getOwnUserDetails(final String tweetBody) {
        client.getUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                User self = User.fromJSON(json);

                addToFront(tweetBody, self);

//                tweets.add(0, new Tweet(tweetBody, self));    // Add to front of list.
//                aTweets.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("FAILED GET USER DETAILS", errorResponse.toString());
            }
        });
    }
}
