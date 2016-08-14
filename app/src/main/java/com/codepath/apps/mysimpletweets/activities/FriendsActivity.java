package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.FriendsAdapter;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.TwitterApplication;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FriendsActivity extends AppCompatActivity {
    ArrayList<User> users;
    FriendsAdapter adapter;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        client = TwitterApplication.getRestClient();

        RecyclerView rvUsers = (RecyclerView) findViewById(R.id.rvUsers);
        users = new ArrayList<>();
        adapter = new FriendsAdapter(this, users);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        client.getFriendsList(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                ArrayList<User> newUsers = User.fromRelationships(json);
                users.addAll(newUsers);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
