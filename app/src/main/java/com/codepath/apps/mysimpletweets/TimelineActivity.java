package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;

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
                ArrayList<Tweet> newTweets = Tweet.fromJSONArray(json);
                lowestUid = getLowestUid(newTweets);
                tweets.addAll(newTweets);
                aTweets.notifyDataSetChanged(); // TODO: Find out how many tweets are fetched. Avoid using notifyDataSetChanged().
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getBaseContext(), errorResponse.toString(), Toast.LENGTH_LONG).show();
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

    public void onComposeAction(MenuItem mi) {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
//        i.putExtra("client", client);
        startActivity(i);   // TODO: Return tweet?
    }
}
