package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@Table(name = "Medias")
public class Media extends Model {
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    public long remote_id;
    @Column(name = "tweet_id")
    public long tweet_id;
    @Column(name = "media_url")
    public String mediaUrl;

    public Media() { super(); }

    public static void newEntry(long tweetId, JSONObject jsonObject) {
        Media media = new Media();
        try {
            media.remote_id = jsonObject.getLong("id");
            media.tweet_id = tweetId;
            media.mediaUrl = jsonObject.getString("media_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        media.save();
    }

    public static void addAll (long tweetId, JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                long rId = json.getLong("id"); // get just the remote id
                Media existing = new Select().from(Media.class).where("remote_id = ?", rId).executeSingle();
                if (existing == null) Media.newEntry(tweetId, json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Media> getAll (long tweet_id) {
        return new Select().from(Media.class).where("tweet_id = ?", tweet_id).execute();
    }
}
