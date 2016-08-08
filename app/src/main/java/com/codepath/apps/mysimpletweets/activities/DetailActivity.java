package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.MediaAdapter;
import com.codepath.apps.mysimpletweets.models.Media;
import com.codepath.apps.mysimpletweets.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    TextView tvDetailName;
    List<Media> medias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        loadDetails();
    }

    private void loadDetails() {
        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ImageView ivDetailImage = (ImageView) findViewById(R.id.ivDetailImage);
        Glide.with(getBaseContext()).load(tweet.getUser().getProfileImageUrl()).into(ivDetailImage);

        tvDetailName = (TextView) findViewById(R.id.tvDetailName);
        tvDetailName.setText(tweet.user.screenName);

        TextView tvDetailTimestamp = (TextView) findViewById(R.id.tvDetailTimestamp);
        tvDetailTimestamp.setText(tweet.createdAt);

        // Load media images.
        loadImages(tweet.remote_id);
    }

    private void loadImages(long rid) {
        RecyclerView rvMedia = (RecyclerView) findViewById(R.id.rvMedia);
        medias = Media.getAll(rid);
        if (medias != null && medias.size() > 0) {
            MediaAdapter adapter = new MediaAdapter(this, medias);
            rvMedia.setAdapter(adapter);
            rvMedia.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public void compose(View v) {
        Intent i = new Intent(DetailActivity.this, ComposeActivity.class);
        i.putExtra("name", tvDetailName.getText());
        startActivity(i);
    }
}
