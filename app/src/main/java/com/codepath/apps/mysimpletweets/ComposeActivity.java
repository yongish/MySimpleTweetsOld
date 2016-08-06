package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

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
        getOwnUserDetails();
    }

    public void getOwnUserDetails() {
        client.getUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                self = User.fromJSON(json);
                TextView tvOwnName = (TextView) findViewById(R.id.tvOwnName);
                tvOwnName.setText("@" + TwitterClient.SCREEN_NAME);
                Picasso.with(getBaseContext()).load(self.getProfileImageUrl()).into((ImageView)findViewById(R.id.ivOwnProfileImage));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("FAILED GET USER DETAILS", errorResponse.toString());
            }
        });

    }

    public void postTweet(View v) {
       String status = ((EditText)findViewById(R.id.etTweet)).getText().toString();

       client.postTweet(new JsonHttpResponseHandler() {
           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
               Log.d("DEBUG", json.toString());
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
    }

    public void backToTimeline(View v) {
        this.finish();
    }
}
