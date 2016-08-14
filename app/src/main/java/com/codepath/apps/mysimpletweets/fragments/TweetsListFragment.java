package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.TimelineActivity;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Media;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

public class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private RecyclerView rvTweets;
    private SwipeRefreshLayout swipeContainer;

    private TimelineActivity activityTimeline;

    // inflation logic

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
        rvTweets.setAdapter(aTweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
//                populateTimeline();
                activityTimeline.populateTimeline();
            }
        });

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                populateTimeline();
                activityTimeline.populateTimeline();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return v;
    }

    // creation lifecycle event
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>();

        Configuration.Builder config = new Configuration.Builder(getActivity());
        config.addModelClasses(Tweet.class, User.class, Media.class);
        ActiveAndroid.initialize(getActivity());

        // Query ActiveAndroid for list of data and load result back to adapter user addAll
        List<Tweet> queryResults = new Select().from(Tweet.class).orderBy("created_at DESC").limit(100).execute();
        tweets.addAll(queryResults);

//        aTweets.notifyDataSetChanged();
        if (savedInstanceState == null) {
            activityTimeline = (TimelineActivity) getActivity();
        }
    }

    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }

    public void setSwipeContainerRefreshingFalse() {
        swipeContainer.setRefreshing(false);
    }

    private long getLowestUid(ArrayList<Tweet> tweets) {
        long lowestUid = Long.MAX_VALUE;
        for (Tweet tweet: tweets) {
            long uid = tweet.getUid();
            if (uid < lowestUid) lowestUid = uid;
        }
        return lowestUid;
    }
}
