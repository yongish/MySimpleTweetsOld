package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.utils.TwitterApplication;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;
    public User self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApplication.getRestClient();
        if (TwitterClient.isOnline()) {
            getOwnUserDetails();
        } else {
            Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_LONG).show();
        }

        String replyTo = getIntent().getStringExtra("name");
        if (replyTo != null) {
            TextView etTweet = (TextView) findViewById(R.id.etTweet);
            etTweet.append("@" + replyTo + " ");
        }
    }

    public void getOwnUserDetails() {
        client.getUsersShow(TwitterClient.SCREEN_NAME, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                self = User.fromJSON(json);
                TextView tvOwnName = (TextView) findViewById(R.id.tvOwnName);
                tvOwnName.setText("@" + TwitterClient.SCREEN_NAME);
                Glide.with(getBaseContext()).load(self.getProfileImageUrl()).into((ImageView)findViewById(R.id.ivOwnProfileImage));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("FAILED GET USER DETAILS", errorResponse.toString());
            }
        });
    }

    String status;
    public void postTweet(View v) {
       status = ((EditText)findViewById(R.id.etTweet)).getText().toString();

        if (TwitterClient.isOnline()) {
            client.postTweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    Log.d("DEBUG", json.toString());
                    Intent tweet = new Intent();
                    tweet.putExtra("tweetBody", status);
                    setResult(RESULT_OK, tweet);
                    finish();
//               Toast.makeText(getBaseContext(), json.toString(), Toast.LENGTH_LONG).show();
                    // JSON HERE
                    // DESERIALIZE JSON
                    // CREATE MODELS AND ADD THEM TO THE ADAPTER
                    // LOAD THE MODEL DATA INTO LISTVIEW
                    //aTweets.addAll(Tweet.fromJSONArray(json));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("FAILED TO POST TWEET", errorResponse.toString());
//               Toast.makeText(getBaseContext(), errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            }, status);
        } else {
            Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void backToTimeline(View v) {
        finish();
    }

}
