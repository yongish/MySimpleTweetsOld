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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.TwitterApplication;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MentionsTimelineFragment extends TweetsListFragment {
    private RecyclerView rvTweets;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ProgressBar pb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        pb = (ProgressBar) v.findViewById(R.id.pbLoading);
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


        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);

        client = TwitterApplication.getRestClient();    // singleton client
        if (TwitterClient.isOnline()) {
            populateTimeline();
        } else {
            Toast.makeText(getContext(), "Check Internect Connection", Toast.LENGTH_LONG).show();
        }
    }

    // Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from the json
    public void populateTimeline() {
        if (pb != null) pb.setVisibility(ProgressBar.VISIBLE);
        client.getMentionsTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                pb.setVisibility(ProgressBar.INVISIBLE);
                Log.d("DEBUG", json.toString());
                // JSON HERE
                // DESERIALIZE JSON
                // CREATE MODELS AND ADD THEM TO THE ADAPTER
                // LOAD THE MODEL DATA INTO LISTVIEW
//                aTweets.clear();
                ArrayList<Tweet> newTweets = Tweet.fromJSONArray(json);
                addAll(newTweets);
//                aTweets.notifyDataSetChanged(); // TODO: Find out how many tweets are fetched. Avoid using notifyDataSetChanged().
                setSwipeContainerRefreshingFalse();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                pb.setVisibility(ProgressBar.INVISIBLE);
                String errString = "";
                if (errorResponse != null) errString = errorResponse.toString();
            }
        });
    }

    public void addAll(List<Tweet> tweets) {
        this.tweets.addAll(tweets);
        aTweets.addAll(tweets);
        aTweets.notifyDataSetChanged();
    }
}
