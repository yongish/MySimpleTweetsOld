package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Media;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private SwipeRefreshLayout swipeContainer;
    private long lowestUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        tweets = new ArrayList<>();
        RecyclerView rvTweets = (RecyclerView) findViewById(R.id.rvTweets);

        aTweets = new TweetsArrayAdapter(this, tweets);
        rvTweets.setAdapter(aTweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);

        Configuration.Builder config = new Configuration.Builder(this);
        config.addModelClasses(Tweet.class, User.class, Media.class);
        ActiveAndroid.initialize(this);

        // Query ActiveAndroid for list of data and load result back to adapter user addAll
        List<Tweet> queryResults = new Select().from(Tweet.class).orderBy("created_at DESC").limit(100).execute();
        tweets.addAll(queryResults);
        aTweets.notifyDataSetChanged();


        lowestUid = Long.MAX_VALUE;
        client = TwitterApplication.getRestClient();    // singleton client
        populateTimeline();


        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline();
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
    }

    // Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from the json
    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                // JSON HERE
                // DESERIALIZE JSON
                // CREATE MODELS AND ADD THEM TO THE ADAPTER
                // LOAD THE MODEL DATA INTO LISTVIEW
                //aTweets.addAll(Tweet.fromJSONArray(json));
                aTweets.clear();
                ArrayList<Tweet> newTweets = Tweet.fromJSONArray(json);
                lowestUid = getLowestUid(newTweets);
                tweets.addAll(newTweets);
                aTweets.notifyDataSetChanged(); // TODO: Find out how many tweets are fetched. Avoid using notifyDataSetChanged().
                swipeContainer.setRefreshing(false);

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
                Toast.makeText(getBaseContext(), errString, Toast.LENGTH_LONG).show();
            }
        }, lowestUid);
    }

    private long getLowestUid(ArrayList<Tweet> tweets) {
        long lowestUid = Long.MAX_VALUE;
        for (Tweet tweet: tweets) {
            long uid = tweet.getUid();
            if (uid < lowestUid) lowestUid = uid;
        }
        return lowestUid;
    }

    private final int REQUEST_CODE = 20;
    public void onComposeAction(MenuItem mi) {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String tweetBody = data.getExtras().getString("tweetBody");
            getOwnUserDetails(tweetBody);
        }
    }

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
    }

}
