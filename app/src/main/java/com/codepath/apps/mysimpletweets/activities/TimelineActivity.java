package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.TweetsListFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    private TweetsListFragment fragmentTweetsList;
    private TwitterClient client;
    private long lowestUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        lowestUid = Long.MAX_VALUE;

        client = TwitterApplication.getRestClient();    // singleton client
        populateTimeline();

        if (savedInstanceState == null) {
            // access the fragment
            fragmentTweetsList = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
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
                fragmentTweetsList.addAll(newTweets);
//                aTweets.notifyDataSetChanged(); // TODO: Find out how many tweets are fetched. Avoid using notifyDataSetChanged().
                fragmentTweetsList.setSwipeContainerRefreshingFalse();

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

    private final int REQUEST_CODE = 20;
    public void onComposeAction(MenuItem mi) {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String tweetBody = data.getExtras().getString("tweetBody");
//            getOwnUserDetails(tweetBody);
        }
    }
/*
    private User self;
    public void getOwnUserDetails(final String tweetBody) {
        client.getUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                self = User.fromJSON(json);

                tweets.add(0, new Tweet(tweetBody, self));    // Add to front of list.
                aTweets.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("FAILED GET USER DETAILS", errorResponse.toString());
            }
        });
    }*/

}
