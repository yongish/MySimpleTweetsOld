package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@Table(name = "Users")
public class User extends Model {
    @Column(name = "uid", unique = true)
    public long uid;
    @Column(name = "name")
    public String name;
    @Column(name = "screen_name")
    public String screenName;
    @Column(name = "profile_image_url")
    public String profileImageUrl;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public User() {
        super();
    }

    // deserialize the user json => User
    public static User fromJSON (JSONObject json) {
        User u = new User();
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

    public List<Tweet> tweets() {
        return getMany(Tweet.class, "User");
    }
}
