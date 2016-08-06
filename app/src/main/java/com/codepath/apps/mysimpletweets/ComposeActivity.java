package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApplication.getRestClient();
    }

    public void postTweet(View v) {
       String status = ((EditText)findViewById(R.id.etTweet)).getText().toString();

       client.postTweet(new JsonHttpResponseHandler() {
           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
               Log.d("DEBUG", json.toString());
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
