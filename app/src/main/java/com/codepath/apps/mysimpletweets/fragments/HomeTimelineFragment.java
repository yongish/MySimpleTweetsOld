package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
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

public class HomeTimelineFragment extends TweetsListFragment {

    private long lowestUid;
    private RecyclerView rvTweets;

    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);

        rvTweets.setAdapter(aTweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline();
            }
        });

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
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

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lowestUid = Long.MAX_VALUE;
        client = TwitterApplication.getRestClient();    // singleton client

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);

        Configuration.Builder config = new Configuration.Builder(getActivity());
        config.addModelClasses(Tweet.class, User.class, Media.class);
        ActiveAndroid.initialize(getActivity());

        // Query ActiveAndroid for list of data and load result back to adapter user addAll
        List<Tweet> queryResults = new Select().from(Tweet.class).orderBy("created_at DESC").limit(100).execute();
        addAll(queryResults);

        populateTimeline();
    }

    // Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from the json
    public void populateTimeline() {
        lowestUid = getLowestUid();

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
                tweets.addAll(newTweets);
                aTweets.addAll(newTweets);
//                addAll(newTweets);
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


    public void addToFront(String tweetBody, User user) {
        tweets.add(0, new Tweet(tweetBody, user));
        aTweets.notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweets) {
        this.tweets.addAll(tweets);
        aTweets.addAll(tweets);
        aTweets.notifyDataSetChanged();
    }

    protected long getLowestUid() {
//        protected long getLowestUid(ArrayList<Tweet> tweets) {
        long lowestUid = Long.MAX_VALUE;
        for (Tweet tweet: tweets) {
            long uid = tweet.getUid();
            if (uid < lowestUid) lowestUid = uid;
        }
        return lowestUid;
    }
}
