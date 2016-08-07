package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        loadDetails();
    }

    private void loadDetails() {
        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ImageView ivDetailImage = (ImageView) findViewById(R.id.ivDetailImage);
        Picasso.with(getBaseContext()).load(tweet.getUser().getProfileImageUrl()).into(ivDetailImage);

        TextView tvDetailName = (TextView) findViewById(R.id.tvDetailName);
        tvDetailName.setText(tweet.body);

        TextView tvDetailTimestamp = (TextView) findViewById(R.id.tvDetailTimestamp);
        tvDetailTimestamp.setText(tweet.createdAt);
    }
}
