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

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
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

    private HomeTimelineFragment activityTimeline;

    // inflation logic

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
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
//        aTweets.notifyDataSetChanged();
//        if (savedInstanceState == null) {
//            activityTimeline = (HomeTimelineFragment) getFragmentManager().findFragmentById(R.id.fragment_timeline);
//            activityTimeline = (TimelineActivity) getActivity();
//        }
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

    public void setSwipeContainerRefreshingFalse() {
        swipeContainer.setRefreshing(false);
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
