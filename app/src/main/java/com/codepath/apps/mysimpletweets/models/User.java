package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel(analyze = {User.class})
@Table(name = "Users")
public class User extends Model {
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    public long remote_id;
    @Column(name = "uid")
    public long uid;
    @Column(name = "name")
    public String name;
    @Column(name = "screen_name")
    public String screenName;
    @Column(name = "profile_image_url")
    public String profileImageUrl;
    private String tagline;
    private int followersCount;
    private int followingsCount;

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

    public String getTagline() {
        return tagline;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return followingsCount;
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
            u.remote_id = u.uid;
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
            u.tagline = json.getString("description");
            u.followersCount = json.getInt("followers_count");
            u.followingsCount = json.getInt("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

    public static ArrayList<User> fromRelationships (JSONObject json) {
        ArrayList<User> users = new ArrayList<>();
        try {
            JSONArray jsonArray = json.getJSONArray("users");
            users = fromJSONArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static ArrayList<User> fromJSONArray (JSONArray jsonArray) {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject userJson = jsonArray.getJSONObject(i);
                User user = User.fromJSON(userJson);
                if (user != null) {
                    users.add(user);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return users;
    }
}
